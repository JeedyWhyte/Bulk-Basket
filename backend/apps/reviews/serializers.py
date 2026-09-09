from rest_framework import serializers
from .models import Review


class ReviewSerializer(serializers.ModelSerializer):
    seller_name = serializers.CharField(
        source='seller.seller_profile.business_name',
        default='', read_only=True,
    )
    order_id = serializers.UUIDField(source='order.id', read_only=True)

    class Meta:
        model = Review
        fields = [
            'id', 'order_id', 'seller', 'seller_name',
            'rating', 'comment', 'created_at',
        ]
        read_only_fields = ['id', 'seller', 'seller_name', 'created_at']


class ReviewCreateSerializer(serializers.Serializer):
    order_id = serializers.UUIDField()
    rating = serializers.IntegerField(min_value=1, max_value=5)
    comment = serializers.CharField(required=False, allow_blank=True, default='')
