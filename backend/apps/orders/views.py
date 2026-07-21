from rest_framework import generics, permissions, status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from apps.common.permissions import IsBuyer, IsSeller
from apps.common.exceptions import BusinessLogicError
from apps.users.models import Address
from .models import Order
from .serializers import (
    OrderSerializer,
    OrderCreateSerializer,
    OrderStatusUpdateSerializer,
)
from .services import create_order
from .state_machine import transition_order
from apps.notifications.services import notify_order_placed, notify_order_status_change


class BuyerOrderListCreateView(generics.ListCreateAPIView):
    """Buyer views their orders or places a new one."""
    permission_classes = [IsBuyer]

    def get_serializer_class(self):
        if self.request.method == 'POST':
            return OrderCreateSerializer
        return OrderSerializer

    def get_queryset(self):
        return Order.objects.filter(
            buyer=self.request.user
        ).prefetch_related('items__product')

    def create(self, request, *args, **kwargs):
        serializer = OrderCreateSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        try:
            address = Address.objects.get(
                id=data['delivery_address_id'],
                user=request.user,
            )
        except Address.DoesNotExist:
            return Response(
                {"status": "error", "message": "Delivery address not found."},
                status=status.HTTP_404_NOT_FOUND,
            )

        try:
            order = create_order(
                buyer=request.user,
                seller_id=data['seller_id'],
                items_data=data['items'],
                address=address,
            )
            notify_order_placed(order)
        except BusinessLogicError as e:
            return Response(
                {"status": "error", "message": str(e)},
                status=status.HTTP_400_BAD_REQUEST,
            )

        return Response(
            {
                "status": "success",
                "message": "Order placed successfully.",
                "data": OrderSerializer(order).data,
            },
            status=status.HTTP_201_CREATED,
        )


class BuyerOrderDetailView(generics.RetrieveAPIView):
    """Buyer views a single order."""
    serializer_class = OrderSerializer
    permission_classes = [IsBuyer]

    def get_queryset(self):
        return Order.objects.filter(
            buyer=self.request.user
        ).prefetch_related('items__product')

    lookup_field = 'id'


class SellerOrderListView(generics.ListAPIView):
    """Seller views all orders placed with them."""
    serializer_class = OrderSerializer
    permission_classes = [IsSeller]

    def get_queryset(self):
        return Order.objects.filter(
            seller=self.request.user
        ).prefetch_related('items__product')


class SellerOrderStatusUpdateView(generics.UpdateAPIView):
    """Seller updates the status of an order."""
    serializer_class = OrderStatusUpdateSerializer
    permission_classes = [IsSeller]
    lookup_field = 'id'

    def get_queryset(self):
        return Order.objects.filter(seller=self.request.user)

    def update(self, request, *args, **kwargs):
        order = self.get_object()
        serializer = OrderStatusUpdateSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)

        try:
            updated_order = transition_order(
                order, serializer.validated_data['status']
            )
            notify_order_status_change(updated_order)
        except BusinessLogicError as e:
            return Response(
                {"status": "error", "message": str(e)},
                status=status.HTTP_400_BAD_REQUEST,
            )

        return Response(
            {
                "status": "success",
                "message": f"Order status updated to '{updated_order.status}'.",
                "data": OrderSerializer(updated_order).data,
            }
        )