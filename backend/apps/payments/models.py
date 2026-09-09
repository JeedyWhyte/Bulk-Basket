import uuid
from django.db import models


class PaymentMethod(models.Model):
    """A saved *demo* card. No real card data is ever stored — only the
    brand and last 4 digits, exactly like a real gateway's tokenized
    representation would look once the PAN itself has been discarded.
    """

    class Brand(models.TextChoices):
        VISA = 'visa', 'Visa'
        MASTERCARD = 'mastercard', 'Mastercard'
        VERVE = 'verve', 'Verve'

    user = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='payment_methods',
    )
    brand = models.CharField(max_length=20, choices=Brand.choices)
    last4 = models.CharField(max_length=4)
    expiry_month = models.PositiveSmallIntegerField()
    expiry_year = models.PositiveSmallIntegerField()
    is_default = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-is_default', '-created_at']

    def __str__(self):
        return f"{self.brand} •••• {self.last4}"


class Payment(models.Model):
    """A demo-gateway transaction attempt against one order.

    This is a simulated payment processor: no money moves and no real
    card data touches this table. It exists so the checkout flow has a
    real request/response cycle (and a persisted audit trail) to
    demonstrate a payment integration without depending on a live
    third-party gateway account.
    """

    class Method(models.TextChoices):
        CASH_ON_DELIVERY = 'cash_on_delivery', 'Cash on Delivery'
        DEMO_CARD = 'demo_card', 'Card (Demo)'

    class Status(models.TextChoices):
        AUTHORIZED = 'authorized', 'Authorized'
        DECLINED = 'declined', 'Declined'

    id = models.UUIDField(primary_key=True, default=uuid.uuid4, editable=False)
    order = models.OneToOneField(
        'orders.Order',
        on_delete=models.CASCADE,
        related_name='payment',
    )
    user = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='payments',
    )
    method = models.CharField(max_length=20, choices=Method.choices)
    status = models.CharField(max_length=20, choices=Status.choices)
    amount = models.DecimalField(max_digits=12, decimal_places=2)
    reference = models.CharField(max_length=40, unique=True)
    failure_reason = models.CharField(max_length=255, blank=True)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.reference} — {self.status}"
