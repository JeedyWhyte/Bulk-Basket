from decimal import Decimal
from apps.common.exceptions import BusinessLogicError
from apps.products.models import Product
from .models import Order, OrderItem


def create_order(buyer, seller_id, items_data, address):
    """
    Create an order from a list of {product_id, quantity} dicts.
    Validates stock and calculates totals.
    """
    subtotal = Decimal('0.00')
    order_items = []

    for item in items_data:
        product = Product.objects.get(id=item['product_id'])

        if product.seller_id != seller_id:
            raise BusinessLogicError(
                f"Product '{product.name}' does not belong to this seller."
            )
        if product.stock_quantity < item['quantity']:
            raise BusinessLogicError(
                f"Insufficient stock for '{product.name}'."
            )
        if item['quantity'] < product.min_order_qty:
            raise BusinessLogicError(
                f"Minimum order for '{product.name}' is "
                f"{product.min_order_qty} {product.unit}."
            )

        line_total = product.price * item['quantity']
        subtotal += line_total
        order_items.append(
            OrderItem(
                product=product,
                quantity=item['quantity'],
                unit_price=product.price,
                total_price=line_total,
            )
        )

    delivery_fee = Decimal('500.00')  # Flat fee — refine later
    total = subtotal + delivery_fee

    order = Order.objects.create(
        buyer=buyer,
        seller_id=seller_id,
        delivery_address=address,
        subtotal=subtotal,
        delivery_fee=delivery_fee,
        total=total,
    )

    for item in order_items:
        item.order = order
    OrderItem.objects.bulk_create(order_items)

    # Deduct stock
    for item_data, item_obj in zip(items_data, order_items):
        Product.objects.filter(id=item_data['product_id']).update(
            stock_quantity=models.F('stock_quantity') - item_data['quantity']
        )

    return order