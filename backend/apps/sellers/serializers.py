from rest_framework import serializers
from .models import SellerProfile
from apps.products.serializers import ProductSerializer


class SellerProfileSerializer(serializers.ModelSerializer):
    username = serializers.CharField(source='user.username', read_only=True)
    email = serializers.CharField(source='user.email', read_only=True)
    products = ProductSerializer(
        source='user.products', many=True, read_only=True
    )

    class Meta:
        model = SellerProfile
        fields = [
            'id', 'username', 'email', 'business_name', 'market_name',
            'description', 'latitude', 'longitude', 'rating', 'is_open', 'opening_time', 'closing_time',
            'products', 'created_at',
        ]
        read_only_fields = ['id', 'rating', 'created_at']


class SellerProfileCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = SellerProfile
        fields = [
            'business_name', 'market_name', 'description',
            'latitude', 'longitude', 'opening_time', 'closing_time',
        ]