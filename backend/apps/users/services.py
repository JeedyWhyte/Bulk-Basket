from django.contrib.auth import get_user_model
from apps.common.exceptions import BusinessLogicError

User = get_user_model()


def register_user(data):
    """Register a new user. Raises BusinessLogicError on failure."""
    if User.objects.filter(email=data.get('email')).exists():
        raise BusinessLogicError("A user with this email already exists.")
    # Serializer handles creation — this service adds extra logic
    return data


def update_fcm_token(user, token):
    """Store or refresh the user's FCM device token."""
    user.fcm_token = token
    user.save(update_fields=['fcm_token'])