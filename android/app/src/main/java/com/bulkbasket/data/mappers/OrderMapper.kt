package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.OrderDto
import com.bulkbasket.data.remote.dto.OrderItemDto
import com.bulkbasket.domain.model.Order
import com.bulkbasket.domain.model.OrderItem

fun OrderItemDto.toOrderItem() = OrderItem(
    id = id,
    productId = product,
    productName = product_name,
    productUnit = product_unit,
    quantity = quantity,
    unitPrice = unit_price,
    totalPrice = total_price,
)

fun OrderDto.toOrder() = Order(
    id = id,
    buyerId = buyer,
    buyerName = buyer_name,
    sellerId = seller,
    sellerName = seller_name,
    status = status,
    subtotal = subtotal,
    deliveryFee = delivery_fee,
    total = total,
    notes = notes,
    items = items.map { it.toOrderItem() },
    createdAt = created_at,
    updatedAt = updated_at,
)