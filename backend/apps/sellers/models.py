from django.db import models


class SellerProfile(models.Model):
    """Extended profile for seller users."""

    user = models.OneToOneField(
        'users.User',
        on_delete=models.CASCADE,
        related_name='seller_profile',
    )
    business_name = models.CharField(max_length=200)
    market_name = models.CharField(max_length=200)
    description = models.TextField(blank=True)
    latitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True
    )
    longitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True
    )
    rating = models.DecimalField(
        max_digits=3, decimal_places=2, default=0.00
    )
    is_open = models.BooleanField(default=True)
    opening_time = models.TimeField(null=True, blank=True)
    closing_time = models.TimeField(null=True, blank=True)

    def __str__(self):
        return self.business_name