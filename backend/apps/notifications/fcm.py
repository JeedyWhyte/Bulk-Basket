import requests  # type: ignore
from django.conf import settings


def send_push_notification(fcm_token, title, body, data=None):
    """
    Send a push notification via Firebase Cloud Messaging.
    Returns the FCM response or None if no token provided.
    """
    if not fcm_token:
        return None

    headers = {
        'Authorization': f'key={settings.FCM_SERVER_KEY}',
        'Content-Type': 'application/json',
    }
    payload = {
        'to': fcm_token,
        'notification': {
            'title': title,
            'body': body,
        },
        'data': data or {},
    }

    try:
        response = requests.post(
            'https://fcm.googleapis.com/fcm/send',
            json=payload,
            headers=headers,
            timeout=10,
        )
        return response.json()
    except requests.RequestException:
        return None