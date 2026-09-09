from rest_framework import generics, permissions, status
from rest_framework.response import Response
from apps.common.permissions import IsBuyer, IsSeller
from apps.common.exceptions import BusinessLogicError
from .models import Review
from .serializers import ReviewSerializer, ReviewCreateSerializer
from .services import create_review


class BuyerReviewListCreateView(generics.ListCreateAPIView):
    """Buyer views the reviews they've written, or rates a delivered order."""
    permission_classes = [IsBuyer]

    def get_serializer_class(self):
        if self.request.method == 'POST':
            return ReviewCreateSerializer
        return ReviewSerializer

    def get_queryset(self):
        return Review.objects.filter(
            buyer=self.request.user
        ).select_related('seller__seller_profile', 'order')

    def create(self, request, *args, **kwargs):
        serializer = ReviewCreateSerializer(data=request.data)
        serializer.is_valid(raise_exception=True)
        data = serializer.validated_data

        try:
            review = create_review(
                buyer=request.user,
                order_id=data['order_id'],
                rating=data['rating'],
                comment=data.get('comment', ''),
            )
        except BusinessLogicError as e:
            return Response(
                {"status": "error", "message": str(e)},
                status=status.HTTP_400_BAD_REQUEST,
            )

        return Response(
            {
                "status": "success",
                "message": "Review submitted.",
                "data": ReviewSerializer(review).data,
            },
            status=status.HTTP_201_CREATED,
        )


class SellerReviewListView(generics.ListAPIView):
    """Seller views the reviews they've received."""
    serializer_class = ReviewSerializer
    permission_classes = [IsSeller]

    def get_queryset(self):
        return Review.objects.filter(
            seller=self.request.user
        ).select_related('seller__seller_profile', 'order')
