from rest_framework import generics, permissions, status
from rest_framework.response import Response
from apps.common.permissions import IsBuyer
from apps.common.exceptions import BusinessLogicError
from .models import Payment, PaymentMethod
from .serializers import (
    PaymentMethodSerializer,
    ChargeOrderSerializer,
    PaymentSerializer,
)
from .services import charge_order


class PaymentMethodListCreateView(generics.ListCreateAPIView):
    """List or save the caller's demo payment methods."""
    serializer_class = PaymentMethodSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return PaymentMethod.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        is_first = not PaymentMethod.objects.filter(user=self.request.user).exists()
        serializer.save(user=self.request.user, is_default=is_first)


class PaymentMethodDeleteView(generics.DestroyAPIView):
    """Remove a saved demo payment method."""
    serializer_class = PaymentMethodSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return PaymentMethod.objects.filter(user=self.request.user)


class ChargeOrderView(generics.GenericAPIView):
    """Run the demo gateway against one of the buyer's own orders."""
    serializer_class = ChargeOrderSerializer
    permission_classes = [IsBuyer]

    def post(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        try:
            payment = charge_order(
                user=request.user,
                order_id=data['order_id'],
                method=data['method'],
                card_number=data.get('card_number'),
                expiry_month=data.get('expiry_month'),
                expiry_year=data.get('expiry_year'),
                save_card=data.get('save_card', False),
            )
        except BusinessLogicError as e:
            return Response(
                {"status": "error", "message": str(e)},
                status=status.HTTP_400_BAD_REQUEST,
            )

        if payment.status == Payment.Status.DECLINED:
            return Response(
                {
                    "status": "error",
                    "message": payment.failure_reason or "Payment declined.",
                    "data": PaymentSerializer(payment).data,
                },
                status=status.HTTP_402_PAYMENT_REQUIRED,
            )

        return Response(
            {
                "status": "success",
                "message": "Payment authorized.",
                "data": PaymentSerializer(payment).data,
            },
            status=status.HTTP_201_CREATED,
        )
