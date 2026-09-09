from django.urls import path
from . import views

urlpatterns = [
    path('', views.BuyerReviewListCreateView.as_view(), name='buyer-reviews'),
    path('seller/', views.SellerReviewListView.as_view(), name='seller-reviews'),
]
