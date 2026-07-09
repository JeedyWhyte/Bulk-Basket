from django.db.models import F
from django.db.models.functions import ACos, Cos, Radians, Sin
from .models import SellerProfile

# Haversine approximation in Django ORM
EARTH_RADIUS_KM = 6371


def get_nearby_sellers(lat, lng, radius_km=10):
    """
    Return sellers within `radius_km` of the given coordinates.
    Uses the Haversine formula via Django ORM annotations.
    """
    lat_rad = Radians(F('latitude'))
    lng_rad = Radians(F('longitude'))
    ref_lat_rad = Radians(lat)
    ref_lng_rad = Radians(lng)

    return (
        SellerProfile.objects
        .filter(is_open=True, latitude__isnull=False)
        .annotate(
            distance=EARTH_RADIUS_KM * ACos(
                Cos(ref_lat_rad) * Cos(lat_rad) *
                Cos(lng_rad - ref_lng_rad) +
                Sin(ref_lat_rad) * Sin(lat_rad)
            )
        )
        .filter(distance__lte=radius_km)
        .order_by('distance')
    )