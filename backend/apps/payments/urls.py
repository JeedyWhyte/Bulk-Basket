from django.urls import path
from . import views

urlpatterns = [
    path('methods/', views.PaymentMethodListCreateView.as_view(), name='payment-methods'),
    path('methods/<int:pk>/', views.PaymentMethodDeleteView.as_view(), name='payment-method-delete'),
    path('charge/', views.ChargeOrderView.as_view(), name='payment-charge'),
]
