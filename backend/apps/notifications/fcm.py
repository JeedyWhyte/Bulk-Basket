import json
import requests
import google.auth.transport.requests
from google.oauth2 import service_account
from django.conf import settings


def get_access_token_and_project_id():
    """Get OAuth2 access token and project ID from the service account file."""
    with open(settings.FCM_CREDENTIALS_PATH) as f:
        info = json.load(f)

    credentials = service_account.Credentials.from_service_account_info(
        info,
        scopes=['https://www.googleapis.com/auth/firebase.messaging'],
    )
    request = google.auth.transport.requests.Request()
    credentials.refresh(request)
    return credentials.token, info['project_id']


def send_push_notification(fcm_token, title, body, data=None):
    """Send push notification via FCM HTTP V1 API."""
    if not fcm_token:
        return None

    try:
        access_token, project_id = get_access_token_and_project_id()

        url = f'https://fcm.googleapis.com/v1/projects/{project_id}/messages:send'

        headers = {
            'Authorization': f'Bearer {access_token}',
            'Content-Type': 'application/json',
        }

        payload = {
            'message': {
                'token': fcm_token,
                'notification': {
                    'title': title,
                    'body': body,
                },
                'data': {k: str(v) for k, v in (data or {}).items()},
            }
        }

        response = requests.post(
            url,
            json=payload,
            headers=headers,
            timeout=10,
        )
        return response.json()

    except Exception as e:
        return None