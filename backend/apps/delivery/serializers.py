from rest_framework import serializers
from .models import Delivery, RiderProfile


class RiderProfileSerializer(serializers.ModelSerializer):
    username = serializers.CharField(source='user.username', read_only=True)
    phone_number = serializers.CharField(
        source='user.phone_number', read_only=True
    )

    class Meta:
        model = RiderProfile
        fields = [
            'id', 'username', 'phone_number', 'is_available',
            'current_latitude', 'current_longitude',
            'total_deliveries', 'rating', 'created_at',
        ]
        read_only_fields = ['id', 'total_deliveries', 'rating', 'created_at']


class RiderProfileCreateSerializer(serializers.ModelSerializer):
    class Meta:
        model = RiderProfile
        fields = ['is_available', 'current_latitude', 'current_longitude']


class DeliverySerializer(serializers.ModelSerializer):
    rider_name = serializers.CharField(
        source='rider.username', read_only=True
    )
    order_total = serializers.DecimalField(
        source='order.total',
        max_digits=12,
        decimal_places=2,
        read_only=True,
    )
    delivery_address = serializers.SerializerMethodField()

    class Meta:
        model = Delivery
        fields = [
            'id', 'order', 'order_total', 'rider', 'rider_name',
            'status', 'current_latitude', 'current_longitude',
            'delivery_address', 'assigned_at', 'picked_up_at',
            'delivered_at', 'created_at',
        ]
        read_only_fields = [
            'id', 'order', 'rider', 'assigned_at',
            'picked_up_at', 'delivered_at', 'created_at',
        ]

    def get_delivery_address(self, obj):
        address = obj.order.delivery_address
        if not address:
            return None
        return {
            'street': address.street,
            'city': address.city,
            'state': address.state,
        }


class RiderLocationUpdateSerializer(serializers.Serializer):
    latitude = serializers.DecimalField(max_digits=9, decimal_places=6)
    longitude = serializers.DecimalField(max_digits=9, decimal_places=6)


class DeliveryStatusUpdateSerializer(serializers.Serializer):
    status = serializers.ChoiceField(choices=Delivery.Status.choices)