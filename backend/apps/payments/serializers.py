from rest_framework import serializers
from .models import Payment, PaymentMethod


class PaymentMethodSerializer(serializers.ModelSerializer):
    class Meta:
        model = PaymentMethod
        fields = [
            'id', 'brand', 'last4', 'expiry_month',
            'expiry_year', 'is_default',
        ]
        read_only_fields = ['id']


class PaymentSerializer(serializers.ModelSerializer):
    class Meta:
        model = Payment
        fields = [
            'id', 'order', 'method', 'status',
            'amount', 'reference', 'failure_reason', 'created_at',
        ]
        read_only_fields = fields


class ChargeOrderSerializer(serializers.Serializer):
    order_id = serializers.UUIDField()
    method = serializers.ChoiceField(choices=Payment.Method.choices)
    card_number = serializers.CharField(required=False, allow_blank=True)
    expiry_month = serializers.IntegerField(required=False, min_value=1, max_value=12)
    expiry_year = serializers.IntegerField(required=False, min_value=2024, max_value=2100)
    cvv = serializers.CharField(required=False, allow_blank=True, write_only=True)
    save_card = serializers.BooleanField(required=False, default=False)
