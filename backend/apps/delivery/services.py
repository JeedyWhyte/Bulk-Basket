from django.utils import timezone
from apps.common.exceptions import BusinessLogicError
from .models import Delivery, RiderProfile


DELIVERY_TRANSITIONS = {
    'pending':   ['assigned'],
    'assigned':  ['picked_up', 'failed'],
    'picked_up': ['delivered', 'failed'],
    'delivered': [],
    'failed':    [],
}


def assign_rider_to_delivery(delivery, rider):
    """Assign a rider to a pending delivery."""
    if delivery.status != 'pending':
        raise BusinessLogicError(
            f"Cannot assign rider — delivery is already '{delivery.status}'."
        )
    if not hasattr(rider, 'rider_profile'):
        raise BusinessLogicError("This user does not have a rider profile.")
    if not rider.rider_profile.is_available:
        raise BusinessLogicError("This rider is not currently available.")

    delivery.rider = rider
    delivery.status = 'assigned'
    delivery.assigned_at = timezone.now()
    delivery.save(update_fields=['rider', 'status', 'assigned_at', 'updated_at'])
    return delivery


def transition_delivery(delivery, new_status, rider):
    """Move a delivery to a new status."""
    if delivery.rider != rider:
        raise BusinessLogicError(
            "You are not assigned to this delivery."
        )

    allowed = DELIVERY_TRANSITIONS.get(delivery.status, [])
    if new_status not in allowed:
        raise BusinessLogicError(
            f"Cannot move from '{delivery.status}' to '{new_status}'. "
            f"Allowed: {allowed}"
        )

    delivery.status = new_status

    if new_status == 'picked_up':
        delivery.picked_up_at = timezone.now()
    elif new_status == 'delivered':
        delivery.delivered_at = timezone.now()
        RiderProfile.objects.filter(user=rider).update(
            total_deliveries=delivery.rider.rider_profile.total_deliveries + 1
        )

    delivery.save()
    return delivery


def update_rider_location(rider, latitude, longitude):
    """Update a rider's current GPS coordinates."""
    RiderProfile.objects.filter(user=rider).update(
        current_latitude=latitude,
        current_longitude=longitude,
    )
    Delivery.objects.filter(
        rider=rider, status='picked_up'
    ).update(
        current_latitude=latitude,
        current_longitude=longitude,
    )