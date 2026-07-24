package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.DeliveryDto
import com.bulkbasket.domain.model.Delivery

fun DeliveryDto.toDelivery() = Delivery(
    id = id,
    orderId = order,
    orderTotal = order_total,
    riderId = rider,
    riderName = rider_name,
    status = status,
    currentLatitude = current_latitude,
    currentLongitude = current_longitude,
    street = delivery_address?.street,
    city = delivery_address?.city,
    state = delivery_address?.state,
    assignedAt = assigned_at,
    pickedUpAt = picked_up_at,
    deliveredAt = delivered_at,
    createdAt = created_at,
)