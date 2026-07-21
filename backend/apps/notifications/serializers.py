from rest_framework import serializers
from .models import Notification


class NotificationSerializer(serializers.ModelSerializer):
    class Meta:
        model = Notification
        fields = [
            'id', 'title', 'body', 'notification_type',
            'data', 'is_read', 'created_at',
        ]
        read_only_fields = [
            'id', 'title', 'body', 'notification_type',
            'data', 'created_at',
        ]