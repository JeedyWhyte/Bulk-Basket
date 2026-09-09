import pytest
from decimal import Decimal
from django.contrib.auth import get_user_model
from rest_framework.test import APIRequestFactory, force_authenticate
from apps.products.models import Product
from apps.products.views import ProductViewSet

User = get_user_model()


def _seller(username):
    return User.objects.create_user(
        username=username,
        email=f'{username}@test.com',
        password='pass123',
        role='seller',
    )


def _product(seller, is_available):
    return Product.objects.create(
        seller=seller,
        name='Tomatoes',
        price=Decimal('1000.00'),
        unit='basket',
        stock_quantity=10,
        min_order_qty=1,
        is_available=is_available,
    )


def _retrieve(product, user=None):
    factory = APIRequestFactory()
    view = ProductViewSet.as_view({'get': 'retrieve'})
    request = factory.get(f'/api/v1/products/{product.id}/')
    if user is not None:
        force_authenticate(request, user=user)
    return view(request, pk=str(product.id))


@pytest.mark.django_db
class TestProductRetrieveScoping:

    def test_owner_can_retrieve_own_unavailable_product(self):
        # Regression test: perform_destroy soft-deletes (is_available=False)
        # instead of removing the row, so the owning seller must still be
        # able to fetch it (e.g. to edit it) even without ?seller=me.
        seller = _seller('seller_owner')
        product = _product(seller, is_available=False)

        response = _retrieve(product, user=seller)

        assert response.status_code == 200

    def test_other_seller_cannot_retrieve_unavailable_product(self):
        seller = _seller('seller_owner2')
        other = _seller('seller_other')
        product = _product(seller, is_available=False)

        response = _retrieve(product, user=other)

        assert response.status_code == 404

    def test_anonymous_cannot_retrieve_unavailable_product(self):
        seller = _seller('seller_owner3')
        product = _product(seller, is_available=False)

        response = _retrieve(product)

        assert response.status_code == 404

    def test_anyone_can_retrieve_available_product(self):
        seller = _seller('seller_owner4')
        product = _product(seller, is_available=True)

        response = _retrieve(product)

        assert response.status_code == 200
