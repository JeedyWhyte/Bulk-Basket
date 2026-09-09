package com.bulkbasket.domain.model

data class PaymentMethod(
    val id: Int,
    val brand: String,
    val last4: String,
    val expiryMonth: Int,
    val expiryYear: Int,
    val isDefault: Boolean,
)

data class Payment(
    val id: String,
    val orderId: String,
    val method: String,
    val status: String,
    val amount: String,
    val reference: String,
    val failureReason: String,
    val createdAt: String,
)
