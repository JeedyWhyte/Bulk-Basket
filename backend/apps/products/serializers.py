from rest_framework import serializers
from .models import Product, Category


class CategorySerializer(serializers.ModelSerializer):
    class Meta:
        model = Category
        fields = ['id', 'name', 'slug', 'icon_url']


class ProductSerializer(serializers.ModelSerializer):
    category_name = serializers.CharField(
        source='category.name', read_only=True
    )
    seller_name = serializers.CharField(
        source='seller.username', read_only=True
    )
    in_stock = serializers.BooleanField(read_only=True)

    class Meta:
        model = Product
        fields = [
            'id', 'name', 'description', 'price', 'unit',
            'min_order_qty', 'stock_quantity', 'image_url',
            'is_available', 'in_stock', 'category', 'category_name',
            'seller', 'seller_name', 'created_at',
        ]
        read_only_fields = ['seller', 'created_at']