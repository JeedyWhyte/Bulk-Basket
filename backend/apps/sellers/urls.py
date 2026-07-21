from django.urls import path
from . import views

urlpatterns = [
    path('profile/', views.SellerProfileCreateView.as_view(), name='seller-profile-create'),
    path('profile/me/', views.SellerProfileDetailView.as_view(), name='seller-profile-detail'),
    path('<int:id>/', views.SellerPublicDetailView.as_view(), name='seller-public-detail'),
    path('nearby/', views.nearby_sellers, name='nearby-sellers'),
]