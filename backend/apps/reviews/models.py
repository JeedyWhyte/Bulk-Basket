from django.core.validators import MaxValueValidator, MinValueValidator
from django.db import models


class Review(models.Model):
    """A buyer's rating + comment for one delivered order.

    One review per order — a buyer rates the seller who fulfilled that
    specific order, not the seller in the abstract.
    """

    order = models.OneToOneField(
        'orders.Order',
        on_delete=models.CASCADE,
        related_name='review',
    )
    buyer = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='reviews_written',
        limit_choices_to={'role': 'buyer'},
    )
    seller = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='reviews_received',
        limit_choices_to={'role': 'seller'},
    )
    rating = models.PositiveSmallIntegerField(
        validators=[MinValueValidator(1), MaxValueValidator(5)],
    )
    comment = models.TextField(blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.rating}★ from {self.buyer.username} for {self.seller.username}"
