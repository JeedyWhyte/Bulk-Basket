from celery import shared_task
from apps.notifications.fcm import send_push_notification


@shared_task
def notify_order_status_change(order_id, new_status):
    """Send push notification when order status changes."""
    from apps.orders.models import Order
    order = Order.objects.select_related('buyer').get(id=order_id)
    if order.buyer.fcm_token:
        send_push_notification(
            fcm_token=order.buyer.fcm_token,
            title='Order Update',
            body=f'Your order is now: {new_status}',
            data={'order_id': str(order_id), 'status': new_status},
        )