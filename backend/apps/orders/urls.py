from django.urls import path
from . import views

urlpatterns = [
    # Buyer endpoints
    path('', views.BuyerOrderListCreateView.as_view(), name='order-list-create'),
    path('<uuid:id>/', views.BuyerOrderDetailView.as_view(), name='order-detail'),

    # Seller endpoints
    path('seller/', views.SellerOrderListView.as_view(), name='seller-order-list'),
    path('seller/<uuid:id>/status/', views.SellerOrderStatusUpdateView.as_view(), name='seller-order-status'),
]   