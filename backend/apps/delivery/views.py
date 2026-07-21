from rest_framework import generics, permissions, status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from apps.common.permissions import IsRider
from apps.common.exceptions import BusinessLogicError
from apps.orders.models import Order
from .models import Delivery, RiderProfile
from .serializers import (
    DeliverySerializer,
    RiderProfileSerializer,
    RiderProfileCreateSerializer,
    RiderLocationUpdateSerializer,
    DeliveryStatusUpdateSerializer,
)
from .services import (
    assign_rider_to_delivery,
    transition_delivery,
    update_rider_location,
)
from apps.notifications.services import notify_delivery_assigned


class RiderProfileCreateView(generics.CreateAPIView):
    """Rider sets up their profile after registration."""
    serializer_class = RiderProfileCreateSerializer
    permission_classes = [IsRider]

    def create(self, request, *args, **kwargs):
        if hasattr(request.user, 'rider_profile'):
            return Response(
                {"status": "error", "message": "Rider profile already exists."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save(user=request.user)
        return Response(
            {"status": "success", "message": "Rider profile created."},
            status=status.HTTP_201_CREATED,
        )


class RiderProfileDetailView(generics.RetrieveUpdateAPIView):
    """Rider views or updates their own profile."""
    serializer_class = RiderProfileSerializer
    permission_classes = [IsRider]

    def get_object(self):
        return self.request.user.rider_profile


class AvailableDeliveryListView(generics.ListAPIView):
    """Rider sees all unassigned deliveries they can pick up."""
    serializer_class = DeliverySerializer
    permission_classes = [IsRider]

    def get_queryset(self):
        return Delivery.objects.filter(
            status='pending'
        ).select_related('order', 'order__delivery_address')


class RiderActiveDeliveryListView(generics.ListAPIView):
    """Rider sees their currently active deliveries."""
    serializer_class = DeliverySerializer
    permission_classes = [IsRider]

    def get_queryset(self):
        return Delivery.objects.filter(
            rider=self.request.user,
            status__in=['assigned', 'picked_up'],
        ).select_related('order', 'order__delivery_address')


class DeliveryDetailView(generics.RetrieveAPIView):
    """Get details of a specific delivery."""
    serializer_class = DeliverySerializer
    permission_classes = [IsRider]
    lookup_field = 'id'

    def get_queryset(self):
        return Delivery.objects.filter(
            rider=self.request.user
        ).select_related('order', 'order__delivery_address')


@api_view(['POST'])
@permission_classes([IsRider])
def accept_delivery(request, id):
    """Rider accepts/claims an available delivery."""
    try:
        delivery = Delivery.objects.get(id=id, status='pending')
    except Delivery.DoesNotExist:
        return Response(
            {"status": "error", "message": "Delivery not found or already assigned."},
            status=status.HTTP_404_NOT_FOUND,
        )

    try:
        delivery = assign_rider_to_delivery(delivery, request.user)
        notify_delivery_assigned(delivery)
    except BusinessLogicError as e:
        return Response(
            {"status": "error", "message": str(e)},
            status=status.HTTP_400_BAD_REQUEST,
        )

    return Response({
        "status": "success",
        "message": "Delivery accepted.",
        "data": DeliverySerializer(delivery).data,
    })


@api_view(['PATCH'])
@permission_classes([IsRider])
def update_delivery_status(request, id):
    """Rider updates the status of their delivery."""
    try:
        delivery = Delivery.objects.get(id=id, rider=request.user)
    except Delivery.DoesNotExist:
        return Response(
            {"status": "error", "message": "Delivery not found."},
            status=status.HTTP_404_NOT_FOUND,
        )

    serializer = DeliveryStatusUpdateSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    try:
        delivery = transition_delivery(
            delivery,
            serializer.validated_data['status'],
            request.user,
        )
    except BusinessLogicError as e:
        return Response(
            {"status": "error", "message": str(e)},
            status=status.HTTP_400_BAD_REQUEST,
        )

    return Response({
        "status": "success",
        "message": f"Delivery status updated to '{delivery.status}'.",
        "data": DeliverySerializer(delivery).data,
    })


@api_view(['PATCH'])
@permission_classes([IsRider])
def update_location(request):
    """Rider updates their current GPS location."""
    serializer = RiderLocationUpdateSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)

    update_rider_location(
        request.user,
        serializer.validated_data['latitude'],
        serializer.validated_data['longitude'],
    )

    return Response({
        "status": "success",
        "message": "Location updated.",
    })