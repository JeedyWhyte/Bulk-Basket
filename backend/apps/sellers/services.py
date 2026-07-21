from django.db.models import F, FloatField, ExpressionWrapper
from django.db.models.functions import ACos, Cos, Radians, Sin
from .models import SellerProfile

EARTH_RADIUS_KM = 6371.0


def get_nearby_sellers(lat, lng, radius_km=10):
    lat = float(lat)
    lng = float(lng)

    import math
    lat_rad = math.radians(lat)
    lng_rad = math.radians(lng)

    return (
        SellerProfile.objects
        .filter(is_open=True, latitude__isnull=False, longitude__isnull=False)
        .annotate(
            lat_rad=ExpressionWrapper(
                Radians(F('latitude')), output_field=FloatField()
            ),
            lng_rad=ExpressionWrapper(
                Radians(F('longitude')), output_field=FloatField()
            ),
        )
        .annotate(
            distance=ExpressionWrapper(
                EARTH_RADIUS_KM * ACos(
                    ExpressionWrapper(
                        Cos(lat_rad) * Cos(F('lat_rad')) *
                        Cos(F('lng_rad') - lng_rad) +
                        Sin(lat_rad) * Sin(F('lat_rad')),
                        output_field=FloatField()
                    )
                ),
                output_field=FloatField()
            )
        )
        .filter(distance__lte=radius_km)
        .order_by('distance')
    )


def update_seller_rating(seller_profile, new_rating):
    total = seller_profile.rating * seller_profile.total_ratings
    seller_profile.total_ratings += 1
    seller_profile.rating = (total + new_rating) / seller_profile.total_ratings
    seller_profile.save(update_fields=['rating', 'total_ratings'])
    return seller_profile