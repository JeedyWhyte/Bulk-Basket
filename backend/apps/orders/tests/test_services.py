import pytest
from decimal import Decimal
from django.contrib.auth import get_user_model
from django.db import connection
from django.test.utils import CaptureQueriesContext
from apps.common.exceptions import BusinessLogicError
from apps.orders.services import create_order, DELIVERY_FEE
from apps.orders.models import OrderItem
from apps.products.models import Product
from apps.users.models import Address

User = get_user_model()


def _user(username, role):
    return User.objects.create_user(
        username=username,
        email=f'{username}@test.com',
        password='pass123',
        role=role,
    )


def _product(seller, **overrides):
    defaults = dict(
        name='Tomatoes',
        price=Decimal('1000.00'),
        unit='basket',
        stock_quantity=20,
        min_order_qty=1,
        is_available=True,
    )
    defaults.update(overrides)
    return Product.objects.create(seller=seller, **defaults)


def _address(buyer):
    return Address.objects.create(
        user=buyer,
        label='Home',
        street='1 Market Rd',
        city='Lagos',
        state='Lagos',
    )


@pytest.mark.django_db
class TestCreateOrder:

    def test_creates_order_with_correct_totals_and_deducts_stock(self):
        buyer = _user('buyer_totals', 'buyer')
        seller = _user('seller_totals', 'seller')
        product = _product(seller, price=Decimal('1000.00'), stock_quantity=20)
        address = _address(buyer)

        order = create_order(
            buyer=buyer,
            seller_id=seller.id,
            items_data=[{'product_id': product.id, 'quantity': 3}],
            address=address,
        )

        product.refresh_from_db()
        assert order.subtotal == Decimal('3000.00')
        assert order.delivery_fee == DELIVERY_FEE
        assert order.total == Decimal('3000.00') + DELIVERY_FEE
        assert OrderItem.objects.filter(order=order).count() == 1
        assert product.stock_quantity == 17

    def test_locks_all_line_item_products_in_a_single_query(self):
        # Regression test: create_order() used to select_for_update().get()
        # each product one at a time inside the loop (N queries + locks
        # held across the whole loop). It should now fetch every needed
        # row with one locked query, regardless of item count.
        buyer = _user('buyer_lock', 'buyer')
        seller = _user('seller_lock', 'seller')
        products = [
            _product(seller, name=f'Item {i}', stock_quantity=20)
            for i in range(3)
        ]
        address = _address(buyer)
        items = [{'product_id': p.id, 'quantity': 1} for p in products]

        with CaptureQueriesContext(connection) as ctx:
            create_order(
                buyer=buyer,
                seller_id=seller.id,
                items_data=items,
                address=address,
            )

        select_queries = [
            q for q in ctx.captured_queries
            if 'SELECT' in q['sql'].upper() and 'products_product' in q['sql']
        ]
        assert len(select_queries) == 1

    def test_raises_when_product_does_not_exist(self):
        buyer = _user('buyer_missing', 'buyer')
        seller = _user('seller_missing', 'seller')
        address = _address(buyer)

        with pytest.raises(BusinessLogicError):
            create_order(
                buyer=buyer,
                seller_id=seller.id,
                items_data=[{'product_id': 999999, 'quantity': 1}],
                address=address,
            )

    def test_raises_when_product_belongs_to_different_seller(self):
        buyer = _user('buyer_wrongseller', 'buyer')
        seller = _user('seller_wrongseller_a', 'seller')
        other_seller = _user('seller_wrongseller_b', 'seller')
        product = _product(other_seller)
        address = _address(buyer)

        with pytest.raises(BusinessLogicError):
            create_order(
                buyer=buyer,
                seller_id=seller.id,
                items_data=[{'product_id': product.id, 'quantity': 1}],
                address=address,
            )

    def test_raises_when_insufficient_stock(self):
        buyer = _user('buyer_stock', 'buyer')
        seller = _user('seller_stock', 'seller')
        product = _product(seller, stock_quantity=2)
        address = _address(buyer)

        with pytest.raises(BusinessLogicError):
            create_order(
                buyer=buyer,
                seller_id=seller.id,
                items_data=[{'product_id': product.id, 'quantity': 5}],
                address=address,
            )

    def test_raises_when_below_min_order_qty(self):
        buyer = _user('buyer_minqty', 'buyer')
        seller = _user('seller_minqty', 'seller')
        product = _product(seller, min_order_qty=5, stock_quantity=20)
        address = _address(buyer)

        with pytest.raises(BusinessLogicError):
            create_order(
                buyer=buyer,
                seller_id=seller.id,
                items_data=[{'product_id': product.id, 'quantity': 1}],
                address=address,
            )
