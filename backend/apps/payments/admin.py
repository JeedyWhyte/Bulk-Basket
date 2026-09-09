from django.contrib import admin
from .models import Payment, PaymentMethod


@admin.register(PaymentMethod)
class PaymentMethodAdmin(admin.ModelAdmin):
    list_display = ['id', 'user', 'brand', 'last4', 'is_default']


@admin.register(Payment)
class PaymentAdmin(admin.ModelAdmin):
    list_display = ['reference', 'order', 'method', 'status', 'amount', 'created_at']
    list_filter = ['method', 'status']
