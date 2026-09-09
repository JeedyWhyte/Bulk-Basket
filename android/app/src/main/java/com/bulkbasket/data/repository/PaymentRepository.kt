package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toPayment
import com.bulkbasket.data.mappers.toPaymentMethod
import com.bulkbasket.data.remote.api.PaymentsApi
import com.bulkbasket.data.remote.dto.ChargeOrderRequest
import com.bulkbasket.data.remote.dto.SavePaymentMethodRequest
import com.bulkbasket.domain.model.Payment
import com.bulkbasket.domain.model.PaymentMethod
import com.bulkbasket.domain.repository.IPaymentRepository
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.utils.errorMessage
import javax.inject.Inject

class PaymentRepository @Inject constructor(
    private val api: PaymentsApi,
) : IPaymentRepository {

    override suspend fun getPaymentMethods(): NetworkResult<List<PaymentMethod>> {
        return try {
            val response = api.getPaymentMethods()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toPaymentMethod() }
                )
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to load payment methods"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun addPaymentMethod(
        brand: String,
        last4: String,
        expiryMonth: Int,
        expiryYear: Int,
    ): NetworkResult<PaymentMethod> {
        return try {
            val response = api.addPaymentMethod(
                SavePaymentMethodRequest(
                    brand = brand,
                    last4 = last4,
                    expiry_month = expiryMonth,
                    expiry_year = expiryYear,
                )
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toPaymentMethod())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to save payment method"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deletePaymentMethod(id: Int): NetworkResult<Unit> {
        return try {
            val response = api.deletePaymentMethod(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error(
                    response.errorMessage("Failed to remove payment method"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun chargeOrder(
        orderId: String,
        method: String,
        cardNumber: String?,
        expiryMonth: Int?,
        expiryYear: Int?,
        cvv: String?,
        saveCard: Boolean,
    ): NetworkResult<Payment> {
        return try {
            val response = api.chargeOrder(
                ChargeOrderRequest(
                    order_id = orderId,
                    method = method,
                    card_number = cardNumber,
                    expiry_month = expiryMonth,
                    expiry_year = expiryYear,
                    cvv = cvv,
                    save_card = saveCard,
                )
            )
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.data!!.toPayment())
            } else {
                NetworkResult.Error(
                    response.errorMessage("Payment failed"),
                    response.code(),
                )
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}
