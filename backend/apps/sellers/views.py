from rest_framework import generics, permissions, status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.response import Response
from apps.common.permissions import IsSeller
from apps.common.responses import success_response, error_response
from .models import SellerProfile
from .serializers import SellerProfileSerializer, SellerProfileCreateSerializer
from .services import get_nearby_sellers


class SellerProfileCreateView(generics.CreateAPIView):
    """Seller sets up their profile after registration."""
    serializer_class = SellerProfileCreateSerializer
    permission_classes = [IsSeller]

    def create(self, request, *args, **kwargs):
        if hasattr(request.user, 'seller_profile'):
            return error_response(
                message="Seller profile already exists.",
                status=400,
            )
        serializer = self.get_serializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        serializer.save(user=request.user)
        return success_response(
            data=serializer.data,
            message="Seller profile created.",
            status=status.HTTP_201_CREATED,
        )


class SellerProfileDetailView(generics.RetrieveUpdateAPIView):
    """Get or update a seller's own profile."""
    serializer_class = SellerProfileSerializer
    permission_classes = [IsSeller]

    def get_object(self):
        return self.request.user.seller_profile


class SellerPublicDetailView(generics.RetrieveAPIView):
    """Any user can view a seller's public profile."""
    serializer_class = SellerProfileSerializer
    permission_classes = [permissions.AllowAny]
    queryset = SellerProfile.objects.select_related('user')
    lookup_field = 'id'


@api_view(['GET'])
@permission_classes([permissions.AllowAny])
def nearby_sellers(request):
    lat = request.GET.get('lat')
    lng = request.GET.get('lng')
    radius = request.GET.get('radius', 10)

    if not lat or not lng:
        return Response({
            "status": "error",
            "message": "Both 'lat' and 'lng' query parameters are required.",
        }, status=400)

    try:
        sellers = get_nearby_sellers(lat, lng, float(radius))
        serializer = SellerProfileSerializer(sellers, many=True)
        return Response({
            "status": "success",
            "message": "Success",
            "data": serializer.data,
        }, status=200)
    except Exception as e:
        return Response({
            "status": "error",
            "message": str(e),
        }, status=400)