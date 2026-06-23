from django.contrib.auth.models import AbstractUser
from django.db import models


class User(AbstractUser):
    """Custom user supporting three roles: buyer, seller, rider."""

    class Role(models.TextChoices):
        BUYER = 'buyer', 'Buyer'
        SELLER = 'seller', 'Seller'
        RIDER = 'rider', 'Rider'

    role = models.CharField(
        max_length=10,
        choices=Role.choices,
        default=Role.BUYER,
    )
    phone_number = models.CharField(max_length=15, blank=True)
    avatar_url = models.URLField(blank=True)
    is_verified = models.BooleanField(default=False)
    fcm_token = models.CharField(max_length=255, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)
    updated_at = models.DateTimeField(auto_now=True)

    def __str__(self):
        return f"{self.username} ({self.role})"


class Address(models.Model):
    """Delivery addresses for buyers."""

    user = models.ForeignKey(
        User,
        on_delete=models.CASCADE,
        related_name='addresses',
    )
    label = models.CharField(max_length=50)  # "Home", "Office"
    street = models.CharField(max_length=255)
    city = models.CharField(max_length=100)
    state = models.CharField(max_length=100)
    latitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True, blank=True
    )
    longitude = models.DecimalField(
        max_digits=9, decimal_places=6, null=True, blank=True
    )
    is_default = models.BooleanField(default=False)

    class Meta:
        verbose_name_plural = 'addresses'

    def __str__(self):
        return f"{self.label} - {self.user.username}"