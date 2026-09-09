package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.PaymentDto
import com.bulkbasket.data.remote.dto.PaymentMethodDto
import com.bulkbasket.domain.model.Payment
import com.bulkbasket.domain.model.PaymentMethod

fun PaymentMethodDto.toPaymentMethod() = PaymentMethod(
    id = id,
    brand = brand,
    last4 = last4,
    expiryMonth = expiry_month,
    expiryYear = expiry_year,
    isDefault = is_default,
)

fun PaymentDto.toPayment() = Payment(
    id = id,
    orderId = order,
    method = method,
    status = status,
    amount = amount,
    reference = reference,
    failureReason = failure_reason,
    createdAt = created_at,
)
