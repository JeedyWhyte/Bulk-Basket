from .models import Notification
from .fcm import send_push_notification


def create_notification(recipient, title, body, notification_type, data=None):
    """
    Save a notification to the database and send a push notification
    to the recipient's device if they have an FCM token.
    """
    notification = Notification.objects.create(
        recipient=recipient,
        title=title,
        body=body,
        notification_type=notification_type,
        data=data or {},
    )

    # Send push if the user has an FCM token registered
    if recipient.fcm_token:
        send_push_notification(
            fcm_token=recipient.fcm_token,
            title=title,
            body=body,
            data=data or {},
        )

    return notification


def notify_order_placed(order):
    """Notify seller when a buyer places an order."""
    create_notification(
        recipient=order.seller,
        title='New Order Received',
        body=f'You have a new order worth ₦{order.total}.',
        notification_type='order_placed',
        data={'order_id': str(order.id)},
    )


def notify_order_status_change(order):
    """Notify buyer when seller updates their order status."""
    status_messages = {
        'confirmed': 'Your order has been confirmed.',
        'preparing': 'Your order is being prepared.',
        'ready': 'Your order is ready for pickup.',
        'in_transit': 'Your order is on the way.',
        'delivered': 'Your order has been delivered.',
        'cancelled': 'Your order has been cancelled.',
    }
    message = status_messages.get(order.status, f'Your order status is now: {order.status}.')
    create_notification(
        recipient=order.buyer,
        title='Order Update',
        body=message,
        notification_type=f'order_{order.status}',
        data={'order_id': str(order.id)},
    )


def notify_delivery_assigned(delivery):
    """Notify buyer when a rider is assigned to their delivery."""
    create_notification(
        recipient=delivery.order.buyer,
        title='Rider Assigned',
        body=f'A rider has been assigned to your delivery.',
        notification_type='delivery_assigned',
        data={
            'order_id': str(delivery.order.id),
            'delivery_id': str(delivery.id),
        },
    )