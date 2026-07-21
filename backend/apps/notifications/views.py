from rest_framework import generics, permissions
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from .models import Notification
from .serializers import NotificationSerializer


class NotificationListView(generics.ListAPIView):
    """User views all their notifications."""
    serializer_class = NotificationSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return Notification.objects.filter(
            recipient=self.request.user
        )


@api_view(['PATCH'])
@permission_classes([permissions.IsAuthenticated])
def mark_as_read(request, id):
    """Mark a single notification as read."""
    try:
        notification = Notification.objects.get(
            id=id,
            recipient=request.user,
        )
    except Notification.DoesNotExist:
        return Response(
            {"status": "error", "message": "Notification not found."},
            status=404,
        )

    notification.is_read = True
    notification.save(update_fields=['is_read'])
    return Response({
        "status": "success",
        "message": "Notification marked as read.",
    })


@api_view(['PATCH'])
@permission_classes([permissions.IsAuthenticated])
def mark_all_as_read(request):
    """Mark all of the user's notifications as read."""
    Notification.objects.filter(
        recipient=request.user,
        is_read=False,
    ).update(is_read=True)

    return Response({
        "status": "success",
        "message": "All notifications marked as read.",
    })


@api_view(['GET'])
@permission_classes([permissions.IsAuthenticated])
def unread_count(request):
    """Return count of unread notifications."""
    count = Notification.objects.filter(
        recipient=request.user,
        is_read=False,
    ).count()

    return Response({
        "status": "success",
        "data": {"unread_count": count},
    })