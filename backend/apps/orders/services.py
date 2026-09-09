from decimal import Decimal
from django.db import models, transaction
from apps.common.exceptions import BusinessLogicError
from apps.products.models import Product
from .models import Order, OrderItem


DELIVERY_FEE = Decimal('500.00')


def create_order(buyer, seller_id, items_data, address, notes=''):
    """
    Create an order from a list of {product_id, quantity} dicts.
    Validates stock and calculates totals.

    Runs inside a transaction with the product rows locked, so a failure
    part-way leaves nothing behind and concurrent orders cannot oversell
    the same stock.
    """
    subtotal = Decimal('0.00')
    order_items = []

    with transaction.atomic():
        products_by_id = {
            product.id: product
            for product in (
                Product.objects
                .select_for_update()
                .filter(id__in=[item['product_id'] for item in items_data])
                .order_by('id')
            )
        }

        for item in items_data:
            product = products_by_id.get(item['product_id'])
            if product is None:
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
            notes=notes,
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

        # Deduct stock — safe because the rows are locked and re-validated
        # above within this same transaction.
        for item in order_items:
            Product.objects.filter(id=item['product'].id).update(
                stock_quantity=models.F('stock_quantity') - item['quantity']
            )

    return order
