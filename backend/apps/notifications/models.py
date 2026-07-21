from django.db import models


class Notification(models.Model):

    class Type(models.TextChoices):
        ORDER_PLACED = 'order_placed', 'Order Placed'
        ORDER_CONFIRMED = 'order_confirmed', 'Order Confirmed'
        ORDER_PREPARING = 'order_preparing', 'Order Being Prepared'
        ORDER_READY = 'order_ready', 'Order Ready for Pickup'
        ORDER_IN_TRANSIT = 'order_in_transit', 'Order In Transit'
        ORDER_DELIVERED = 'order_delivered', 'Order Delivered'
        ORDER_CANCELLED = 'order_cancelled', 'Order Cancelled'
        DELIVERY_ASSIGNED = 'delivery_assigned', 'Delivery Assigned'
        GENERAL = 'general', 'General'

    recipient = models.ForeignKey(
        'users.User',
        on_delete=models.CASCADE,
        related_name='notifications',
    )
    title = models.CharField(max_length=255)
    body = models.TextField()
    notification_type = models.CharField(
        max_length=30,
        choices=Type.choices,
        default=Type.GENERAL,
    )
    data = models.JSONField(default=dict, blank=True)
    is_read = models.BooleanField(default=False)
    created_at = models.DateTimeField(auto_now_add=True)

    class Meta:
        ordering = ['-created_at']

    def __str__(self):
        return f"{self.notification_type} → {self.recipient.username}"