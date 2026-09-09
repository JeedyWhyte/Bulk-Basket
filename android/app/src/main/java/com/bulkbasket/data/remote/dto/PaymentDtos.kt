package com.bulkbasket.data.remote.dto

data class PaymentMethodDto(
    val id: Int,
    val brand: String,
    val last4: String,
    val expiry_month: Int,
    val expiry_year: Int,
    val is_default: Boolean,
)

data class SavePaymentMethodRequest(
    val brand: String,
    val last4: String,
    val expiry_month: Int,
    val expiry_year: Int,
)

data class ChargeOrderRequest(
    val order_id: String,
    val method: String,
    val card_number: String? = null,
    val expiry_month: Int? = null,
    val expiry_year: Int? = null,
    val cvv: String? = null,
    val save_card: Boolean = false,
)

data class PaymentDto(
    val id: String,
    val order: String,
    val method: String,
    val status: String,
    val amount: String,
    val reference: String,
    val failure_reason: String,
    val created_at: String,
)
