from django.db import transaction
from django.db.models import F
from django.utils import timezone
from apps.common.exceptions import BusinessLogicError
from apps.orders.state_machine import transition_order
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

    # Delivery.order is one-to-one, so leaving a delivery 'failed' before
    # pickup would permanently strand its order (no replacement Delivery
    # could ever be created). Reset it to 'pending' instead, so another
    # rider can claim it.
    failed_before_pickup = new_status == 'failed' and delivery.status == 'assigned'

    with transaction.atomic():
        if failed_before_pickup:
            delivery.status = 'pending'
            delivery.rider = None
            delivery.assigned_at = None
        else:
            delivery.status = new_status

            if new_status == 'picked_up':
                delivery.picked_up_at = timezone.now()
            elif new_status == 'delivered':
                delivery.delivered_at = timezone.now()
                # F() expression so concurrent completions don't lose increments.
                RiderProfile.objects.filter(user=rider).update(
                    total_deliveries=F('total_deliveries') + 1
                )

        delivery.save()

        # Keep the buyer-facing order status in sync with the delivery.
        # A failed delivery leaves the order 'in_transit' (or 'ready') so the
        # seller can arrange a new attempt manually.
        order = delivery.order
        if new_status == 'picked_up' and order.status == 'ready':
            transition_order(order, 'in_transit')
        elif new_status == 'delivered' and order.status == 'in_transit':
            transition_order(order, 'delivered')

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