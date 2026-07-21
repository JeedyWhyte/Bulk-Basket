from decimal import Decimal
from django.db import models
from apps.common.exceptions import BusinessLogicError
from apps.products.models import Product
from .models import Order, OrderItem


DELIVERY_FEE = Decimal('500.00')


def create_order(buyer, seller_id, items_data, address):
    """
    Create an order from a list of {product_id, quantity} dicts.
    Validates stock and calculates totals.
    """
    subtotal = Decimal('0.00')
    order_items = []

    for item in items_data:
        try:
            product = Product.objects.get(id=item['product_id'])
        except Product.DoesNotExist:
            raise BusinessLogicError(
                f"Product with id '{item['product_id']}' does not exist."
            )

        if str(product.seller_id) != str(seller_id):
            raise BusinessLogicError(
                f"Product '{product.name}' does not belong to this seller."
            )
        if not product.is_available:
            raise BusinessLogicError(
                f"Product '{product.name}' is not available."
            )
        if product.stock_quantity < item['quantity']:
            raise BusinessLogicError(
                f"Insufficient stock for '{product.name}'. "
                f"Available: {product.stock_quantity}."
            )
        if item['quantity'] < product.min_order_qty:
            raise BusinessLogicError(
                f"Minimum order for '{product.name}' is "
                f"{product.min_order_qty} {product.unit}."
            )

        line_total = product.price * item['quantity']
        subtotal += line_total
        order_items.append({
            'product': product,
            'quantity': item['quantity'],
            'unit_price': product.price,
            'total_price': line_total,
        })

    total = subtotal + DELIVERY_FEE

    order = Order.objects.create(
        buyer=buyer,
        seller_id=seller_id,
        delivery_address=address,
        subtotal=subtotal,
        delivery_fee=DELIVERY_FEE,
        total=total,
    )

    OrderItem.objects.bulk_create([
        OrderItem(
            order=order,
            product=item['product'],
            quantity=item['quantity'],
            unit_price=item['unit_price'],
            total_price=item['total_price'],
        )
        for item in order_items
    ])

    # Deduct stock
    for item in order_items:
        Product.objects.filter(id=item['product'].id).update(
            stock_quantity=models.F('stock_quantity') - item['quantity']
        )

    return order