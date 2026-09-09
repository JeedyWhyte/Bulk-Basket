from rest_framework import generics, permissions, status
from rest_framework.decorators import api_view, permission_classes
from rest_framework_simplejwt.views import TokenObtainPairView
from apps.common.responses import success_response, error_response
from .models import Address
from .serializers import (
    UserRegistrationSerializer,
    UserProfileSerializer,
    AddressSerializer,
)
from .services import update_fcm_token, close_account


class RegisterView(generics.CreateAPIView):
    serializer_class = UserRegistrationSerializer
    permission_classes = [permissions.AllowAny]

    def create(self, request, *args, **kwargs):
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        user = serializer.save()
        return success_response(
            data=UserProfileSerializer(user).data,
            message="Registration successful",
            status=status.HTTP_201_CREATED,
        )


class ProfileView(generics.RetrieveUpdateAPIView):
    serializer_class = UserProfileSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_object(self):
        return self.request.user


class AddressListCreateView(generics.ListCreateAPIView):
    serializer_class = AddressSerializer
    permission_classes = [permissions.IsAuthenticated]

    def get_queryset(self):
        return Address.objects.filter(user=self.request.user)

    def perform_create(self, serializer):
        serializer.save(user=self.request.user)


@api_view(['POST'])
@permission_classes([permissions.IsAuthenticated])
def close_account_view(request):
    """Deactivate the caller's own account. Irreversible from the app —
    re-enabling a closed account is an admin action."""
    close_account(request.user)
    return success_response(message="Your account has been deactivated.")


@api_view(['POST'])
@permission_classes([permissions.IsAuthenticated])
def register_fcm_token(request):
    """Store or refresh the caller's FCM device token for push delivery."""
    token = request.data.get('fcm_token', '')
    if not token:
        return error_response(
            message="'fcm_token' is required.",
            status=400,
        )
    update_fcm_token(request.user, token)
    return success_response(message="Device registered for notifications.")