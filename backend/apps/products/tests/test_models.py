import pytest
from django.contrib.auth import get_user_model
from apps.products.models import Category, Product

User = get_user_model()


@pytest.mark.django_db
class TestProductModel:

    def test_create_category(self):
        category = Category.objects.create(
            name='Vegetables',
            slug='vegetables',
        )
        assert category.name == 'Vegetables'
        assert str(category) == 'Vegetables'

    def test_create_product(self):
        seller = User.objects.create_user(
            username='seller1',
            email='seller1@test.com',
            password='pass123',
            role='seller',
        )
        product = Product.objects.create(
            seller=seller,
            name='Tomatoes',
            price=1500.00,
            unit='basket',
            stock_quantity=50,
            min_order_qty=1,
        )
        assert product.name == 'Tomatoes'
        assert product.in_stock is True

    def test_product_out_of_stock(self):
        seller = User.objects.create_user(
            username='seller2',
            email='seller2@test.com',
            password='pass123',
            role='seller',
        )
        product = Product.objects.create(
            seller=seller,
            name='Rice',
            price=5000.00,
            unit='bag',
            stock_quantity=0,
            min_order_qty=1,
        )
        assert product.in_stock is False