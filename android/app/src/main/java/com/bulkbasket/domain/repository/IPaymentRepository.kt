package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Payment
import com.bulkbasket.domain.model.PaymentMethod
import com.bulkbasket.utils.NetworkResult

interface IPaymentRepository {
    suspend fun getPaymentMethods(): NetworkResult<List<PaymentMethod>>
    suspend fun addPaymentMethod(
        brand: String,
        last4: String,
        expiryMonth: Int,
        expiryYear: Int,
    ): NetworkResult<PaymentMethod>
    suspend fun deletePaymentMethod(id: Int): NetworkResult<Unit>
    suspend fun chargeOrder(
        orderId: String,
        method: String,
        cardNumber: String? = null,
        expiryMonth: Int? = null,
        expiryYear: Int? = null,
        cvv: String? = null,
        saveCard: Boolean = false,
    ): NetworkResult<Payment>
}
