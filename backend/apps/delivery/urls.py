from django.urls import path
from . import views

urlpatterns = [
    # Rider profile
    path('profile/', views.RiderProfileCreateView.as_view(), name='rider-profile-create'),
    path('profile/me/', views.RiderProfileDetailView.as_view(), name='rider-profile-detail'),

    # Deliveries
    path('available/', views.AvailableDeliveryListView.as_view(), name='available-deliveries'),
    path('active/', views.RiderActiveDeliveryListView.as_view(), name='active-deliveries'),
    path('<int:id>/', views.DeliveryDetailView.as_view(), name='delivery-detail'),
    path('<int:id>/accept/', views.accept_delivery, name='accept-delivery'),
    path('<int:id>/status/', views.update_delivery_status, name='update-delivery-status'),
    path('location/', views.update_location, name='update-location'),
]