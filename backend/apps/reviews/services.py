from decimal import Decimal, ROUND_HALF_UP
from django.db import transaction
from apps.common.exceptions import BusinessLogicError
from apps.orders.models import Order
from apps.sellers.models import SellerProfile
from .models import Review


def create_review(buyer, order_id, rating, comment=''):
    """Rate the seller for one of the buyer's own delivered orders.

    Recomputes the seller's aggregate rating/total_ratings from all of
    their reviews so the seller profile stays in sync without a
    separate background job.
    """
    try:
        order = Order.objects.select_related('seller').get(
            id=order_id, buyer=buyer,
        )
    except Order.DoesNotExist:
        raise BusinessLogicError("Order not found.")

    if order.status != Order.Status.DELIVERED:
        raise BusinessLogicError(
            "You can only review an order after it has been delivered."
        )

    if Review.objects.filter(order=order).exists():
        raise BusinessLogicError("You have already reviewed this order.")

    with transaction.atomic():
        review = Review.objects.create(
            order=order,
            buyer=buyer,
            seller=order.seller,
            rating=rating,
            comment=comment,
        )

        seller_reviews = Review.objects.filter(seller=order.seller)
        total = seller_reviews.count()
        average = sum(r.rating for r in seller_reviews) / total

        SellerProfile.objects.filter(user=order.seller).update(
            rating=Decimal(str(average)).quantize(
                Decimal('0.01'), rounding=ROUND_HALF_UP
            ),
            total_ratings=total,
        )

    return review
