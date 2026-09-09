def update_fcm_token(user, token):
    """Store or refresh the user's FCM device token."""
    user.fcm_token = token
    user.save(update_fields=['fcm_token'])


def close_account(user):
    """Deactivate the account (soft delete).

    Deactivating rather than hard-deleting preserves order/review history
    that other users (sellers, riders) still legitimately reference, while
    fully preventing the account from logging in again.
    """
    user.is_active = False
    user.save(update_fields=['is_active'])
