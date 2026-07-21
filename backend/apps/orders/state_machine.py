from apps.common.exceptions import BusinessLogicError

# Maps current status → allowed next statuses
TRANSITIONS = {
    'pending':    ['confirmed', 'cancelled'],
    'confirmed':  ['preparing', 'cancelled'],
    'preparing':  ['ready', 'cancelled'],
    'ready':      ['in_transit'],
    'in_transit': ['delivered'],
    'delivered':  [],
    'cancelled':  [],
}


def transition_order(order, new_status):
    """
    Move an order to a new status if the transition is valid.
    Raises BusinessLogicError if the transition is illegal.
    """
    allowed = TRANSITIONS.get(order.status, [])
    if new_status not in allowed:
        raise BusinessLogicError(
            f"Cannot move from '{order.status}' to '{new_status}'. "
            f"Allowed transitions: {allowed}"
        )
    order.status = new_status
    order.save(update_fields=['status', 'updated_at'])
    return order