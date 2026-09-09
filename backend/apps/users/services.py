def update_fcm_token(user, token):
    """Store or refresh the user's FCM device token."""
    user.fcm_token = token
    user.save(update_fields=['fcm_token'])
