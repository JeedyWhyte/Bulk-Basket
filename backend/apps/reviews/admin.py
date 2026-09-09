from django.contrib import admin
from .models import Review


@admin.register(Review)
class ReviewAdmin(admin.ModelAdmin):
    list_display = ['id', 'buyer', 'seller', 'rating', 'created_at']
    list_filter = ['rating']
    search_fields = ['buyer__username', 'seller__username']
