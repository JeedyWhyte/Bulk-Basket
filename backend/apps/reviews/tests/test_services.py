from decimal import Decimal
from django.test import TestCase
from apps.common.exceptions import BusinessLogicError
from apps.orders.models import Order
from apps.sellers.models import SellerProfile
from apps.users.models import User
from ..services import create_review


class CreateReviewTests(TestCase):
    def setUp(self):
        self.buyer = User.objects.create_user(
            username='buyer1', password='pass12345', role='buyer',
        )
        self.seller = User.objects.create_user(
            username='seller1', password='pass12345', role='seller',
        )
        SellerProfile.objects.create(
            user=self.seller, business_name='Test Store', market_name='Test Market',
        )
        self.order = Order.objects.create(
            buyer=self.buyer,
            seller=self.seller,
            status=Order.Status.DELIVERED,
            subtotal=Decimal('1000.00'),
            total=Decimal('1500.00'),
        )

    def test_create_review_updates_seller_rating(self):
        review = create_review(self.buyer, self.order.id, rating=4, comment='Good')
        self.assertEqual(review.rating, 4)

        profile = SellerProfile.objects.get(user=self.seller)
        self.assertEqual(profile.total_ratings, 1)
        self.assertEqual(profile.rating, Decimal('4.00'))

    def test_cannot_review_non_delivered_order(self):
        self.order.status = Order.Status.PENDING
        self.order.save()

        with self.assertRaises(BusinessLogicError):
            create_review(self.buyer, self.order.id, rating=5)

    def test_cannot_review_same_order_twice(self):
        create_review(self.buyer, self.order.id, rating=3)
        with self.assertRaises(BusinessLogicError):
            create_review(self.buyer, self.order.id, rating=5)
