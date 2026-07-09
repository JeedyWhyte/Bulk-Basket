import requests
from django.conf import settings


def send_push_notification(fcm_token, title, body, data=None):
    """Send a push notification via FCM HTTP v1 API."""
    headers = {
        'Authorization': f'key={settings.FCM_SERVER_KEY}',
        'Content-Type': 'application/json',
    }
    payload = {
        'to': fcm_token,
        'notification': {'title': title, 'body': body},
        'data': data or {},
    }
    response = requests.post(
        'https://fcm.googleapis.com/fcm/send',
        json=payload,
        headers=headers,
    )
    return response.json()