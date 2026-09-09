import uuid
from apps.common.exceptions import BusinessLogicError
from apps.orders.models import Order
from .models import Payment, PaymentMethod

# Demo-gateway test cards, mirroring the convention real sandboxes (e.g.
# Stripe) use: a fixed PAN that always declines, so the "unhappy path" is
# reachable without a live processor.
DECLINED_TEST_CARD = '4000000000000002'


def _luhn_is_valid(card_number: str) -> bool:
    digits = [int(d) for d in card_number if d.isdigit()]
    if len(digits) < 12:
        return False
    checksum = 0
    for i, digit in enumerate(reversed(digits)):
        if i % 2 == 1:
            digit *= 2
            if digit > 9:
                digit -= 9
        checksum += digit
    return checksum % 10 == 0


def _detect_brand(card_number: str) -> str:
    if card_number.startswith('4'):
        return PaymentMethod.Brand.VISA
    if card_number.startswith('5'):
        return PaymentMethod.Brand.MASTERCARD
    return PaymentMethod.Brand.VERVE


def charge_order(user, order_id, method, card_number=None,
                  expiry_month=None, expiry_year=None, save_card=False):
    """Simulate authorizing payment for one of the buyer's own orders.

    This never contacts a real payment processor — it validates the
    card with a Luhn checksum, declines the well-known test PAN, and
    otherwise "authorizes" the payment, persisting a `Payment` record
    either way so the flow has a real audit trail.
    """
    try:
        order = Order.objects.get(id=order_id, buyer=user)
    except Order.DoesNotExist:
        raise BusinessLogicError("Order not found.")

    if hasattr(order, 'payment'):
        raise BusinessLogicError("This order has already been charged.")

    if method == Payment.Method.CASH_ON_DELIVERY:
        payment = Payment.objects.create(
            order=order,
            user=user,
            method=method,
            status=Payment.Status.AUTHORIZED,
            amount=order.total,
            reference=f"COD-{uuid.uuid4().hex[:12].upper()}",
        )
        return payment

    # Demo card path
    if not card_number or not expiry_month or not expiry_year:
        raise BusinessLogicError(
            "Card number, expiry month, and expiry year are required."
        )

    reference = f"DEMO-{uuid.uuid4().hex[:12].upper()}"

    if not _luhn_is_valid(card_number):
        return Payment.objects.create(
            order=order,
            user=user,
            method=method,
            status=Payment.Status.DECLINED,
            amount=order.total,
            reference=reference,
            failure_reason="Invalid card number.",
        )

    if card_number == DECLINED_TEST_CARD:
        return Payment.objects.create(
            order=order,
            user=user,
            method=method,
            status=Payment.Status.DECLINED,
            amount=order.total,
            reference=reference,
            failure_reason="The card was declined by the issuing bank.",
        )

    payment = Payment.objects.create(
        order=order,
        user=user,
        method=method,
        status=Payment.Status.AUTHORIZED,
        amount=order.total,
        reference=reference,
    )

    if save_card:
        last4 = card_number[-4:]
        already_saved = PaymentMethod.objects.filter(
            user=user, last4=last4, expiry_month=expiry_month,
            expiry_year=expiry_year,
        ).exists()
        if not already_saved:
            PaymentMethod.objects.create(
                user=user,
                brand=_detect_brand(card_number),
                last4=last4,
                expiry_month=expiry_month,
                expiry_year=expiry_year,
                is_default=not PaymentMethod.objects.filter(user=user).exists(),
            )

    return payment
