from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('api/v1/users/', include('apps.users.urls')),
    # Will add as apps are built:
    # path('api/v1/products/', include('apps.products.urls')),
    # path('api/v1/sellers/', include('apps.sellers.urls')),
    # path('api/v1/orders/', include('apps.orders.urls')),
    # path('api/v1/delivery/', include('apps.delivery.urls')),
    # path('api/v1/notifications/', include('apps.notifications.urls')),
]