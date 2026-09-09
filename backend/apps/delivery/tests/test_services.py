import pytest
from decimal import Decimal
from django.contrib.auth import get_user_model
from apps.common.exceptions import BusinessLogicError
from apps.delivery.models import Delivery, RiderProfile
from apps.delivery.services import assign_rider_to_delivery, transition_delivery
from apps.orders.models import Order

User = get_user_model()


def _user(username, role):
    return User.objects.create_user(
        username=username,
        email=f'{username}@test.com',
        password='pass123',
        role=role,
    )


def _rider(username):
    rider = _user(username, 'rider')
    RiderProfile.objects.create(user=rider, is_available=True)
    return rider


def _order(buyer, seller, status):
    return Order.objects.create(
        buyer=buyer,
        seller=seller,
        subtotal=Decimal('1000.00'),
        total=Decimal('1500.00'),
        status=status,
    )


def _assigned_delivery(order, rider):
    delivery = Delivery.objects.create(order=order, status='pending')
    return assign_rider_to_delivery(delivery, rider)


@pytest.mark.django_db
class TestTransitionDelivery:

    def test_failed_before_pickup_resets_delivery_instead_of_stranding_order(self):
        # Regression test: Delivery.order is a OneToOneField, so a delivery
        # left terminally 'failed' before pickup could never be replaced,
        # permanently stranding the order. It should reset to 'pending'
        # (unassigned) instead, so another rider can claim it.
        buyer = _user('buyer_fail1', 'buyer')
        seller = _user('seller_fail1', 'seller')
        rider = _rider('rider_fail1')
        order = _order(buyer, seller, status='ready')
        delivery = _assigned_delivery(order, rider)

        result = transition_delivery(delivery, 'failed', rider)

        order.refresh_from_db()
        assert result.status == 'pending'
        assert result.rider is None
        assert result.assigned_at is None
        assert order.status == 'ready'

    def test_reset_delivery_can_be_claimed_by_another_rider(self):
        buyer = _user('buyer_fail2', 'buyer')
        seller = _user('seller_fail2', 'seller')
        rider1 = _rider('rider_fail2a')
        rider2 = _rider('rider_fail2b')
        order = _order(buyer, seller, status='ready')
        delivery = _assigned_delivery(order, rider1)

        transition_delivery(delivery, 'failed', rider1)
        reassigned = assign_rider_to_delivery(delivery, rider2)

        assert reassigned.rider == rider2
        assert reassigned.status == 'assigned'

    def test_failed_after_pickup_stays_terminal(self):
        buyer = _user('buyer_fail3', 'buyer')
        seller = _user('seller_fail3', 'seller')
        rider = _rider('rider_fail3')
        order = _order(buyer, seller, status='in_transit')
        delivery = _assigned_delivery(order, rider)
        delivery.status = 'picked_up'
        delivery.save(update_fields=['status'])

        result = transition_delivery(delivery, 'failed', rider)

        assert result.status == 'failed'
        assert result.rider == rider

    def test_picked_up_advances_order_to_in_transit(self):
        buyer = _user('buyer_pu', 'buyer')
        seller = _user('seller_pu', 'seller')
        rider = _rider('rider_pu')
        order = _order(buyer, seller, status='ready')
        delivery = _assigned_delivery(order, rider)

        transition_delivery(delivery, 'picked_up', rider)

        order.refresh_from_db()
        assert order.status == 'in_transit'

    def test_delivered_advances_order_and_increments_rider_total(self):
        buyer = _user('buyer_del', 'buyer')
        seller = _user('seller_del', 'seller')
        rider = _rider('rider_del')
        order = _order(buyer, seller, status='ready')
        delivery = _assigned_delivery(order, rider)
        transition_delivery(delivery, 'picked_up', rider)

        transition_delivery(delivery, 'delivered', rider)

        order.refresh_from_db()
        rider.rider_profile.refresh_from_db()
        assert order.status == 'delivered'
        assert rider.rider_profile.total_deliveries == 1

    def test_wrong_rider_cannot_transition(self):
        buyer = _user('buyer_wr', 'buyer')
        seller = _user('seller_wr', 'seller')
        rider = _rider('rider_wr_a')
        intruder = _rider('rider_wr_b')
        order = _order(buyer, seller, status='ready')
        delivery = _assigned_delivery(order, rider)

        with pytest.raises(BusinessLogicError):
            transition_delivery(delivery, 'picked_up', intruder)
