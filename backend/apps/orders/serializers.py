from rest_framework import serializers
from .models import Order, OrderItem
from apps.products.serializers import ProductSerializer


class OrderItemSerializer(serializers.ModelSerializer):
    product_name = serializers.CharField(
        source='product.name', read_only=True
    )
    product_unit = serializers.CharField(
        source='product.unit', read_only=True
    )

    class Meta:
        model = OrderItem
        fields = [
            'id', 'product', 'product_name', 'product_unit',
            'quantity', 'unit_price', 'total_price',
        ]
        read_only_fields = ['unit_price', 'total_price']


class OrderItemCreateSerializer(serializers.Serializer):
    product_id = serializers.IntegerField()
    quantity = serializers.IntegerField(min_value=1)


class OrderCreateSerializer(serializers.Serializer):
    seller_id = serializers.IntegerField()
    delivery_address_id = serializers.IntegerField()
    items = OrderItemCreateSerializer(many=True, min_length=1)
    notes = serializers.CharField(required=False, allow_blank=True)


class OrderSerializer(serializers.ModelSerializer):
    items = OrderItemSerializer(many=True, read_only=True)
    buyer_name = serializers.CharField(
        source='buyer.username', read_only=True
    )
    seller_name = serializers.CharField(
        source='seller.username', read_only=True
    )

    class Meta:
        model = Order
        fields = [
            'id', 'buyer', 'buyer_name', 'seller', 'seller_name',
            'status', 'subtotal', 'delivery_fee', 'total',
            'notes', 'items', 'created_at', 'updated_at',
        ]
        read_only_fields = [
            'id', 'buyer', 'subtotal', 'delivery_fee',
            'total', 'created_at', 'updated_at',
        ]


class OrderStatusUpdateSerializer(serializers.Serializer):
    status = serializers.ChoiceField(choices=Order.Status.choices)