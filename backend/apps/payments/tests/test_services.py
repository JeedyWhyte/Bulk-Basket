from decimal import Decimal
from django.test import TestCase
from apps.common.exceptions import BusinessLogicError
from apps.orders.models import Order
from apps.users.models import User
from ..models import Payment
from ..services import charge_order, DECLINED_TEST_CARD


class ChargeOrderTests(TestCase):
    def setUp(self):
        self.buyer = User.objects.create_user(
            username='buyer1', password='pass12345', role='buyer',
        )
        self.seller = User.objects.create_user(
            username='seller1', password='pass12345', role='seller',
        )
        self.order = Order.objects.create(
            buyer=self.buyer,
            seller=self.seller,
            subtotal=Decimal('1000.00'),
            total=Decimal('1500.00'),
        )

    def test_cash_on_delivery_always_authorizes(self):
        payment = charge_order(
            self.buyer, self.order.id, Payment.Method.CASH_ON_DELIVERY,
        )
        self.assertEqual(payment.status, Payment.Status.AUTHORIZED)

    def test_valid_demo_card_authorizes(self):
        payment = charge_order(
            self.buyer, self.order.id, Payment.Method.DEMO_CARD,
            card_number='4242424242424242', expiry_month=12, expiry_year=2030,
        )
        self.assertEqual(payment.status, Payment.Status.AUTHORIZED)

    def test_known_test_card_declines(self):
        payment = charge_order(
            self.buyer, self.order.id, Payment.Method.DEMO_CARD,
            card_number=DECLINED_TEST_CARD, expiry_month=12, expiry_year=2030,
        )
        self.assertEqual(payment.status, Payment.Status.DECLINED)

    def test_cannot_charge_same_order_twice(self):
        charge_order(self.buyer, self.order.id, Payment.Method.CASH_ON_DELIVERY)
        with self.assertRaises(BusinessLogicError):
            charge_order(self.buyer, self.order.id, Payment.Method.CASH_ON_DELIVERY)
