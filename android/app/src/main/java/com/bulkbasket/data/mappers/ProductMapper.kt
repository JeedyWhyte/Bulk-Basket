package com.bulkbasket.data.mappers

import com.bulkbasket.data.remote.dto.ProductDto
import com.bulkbasket.domain.model.Product

fun ProductDto.toProduct() = Product(
    id = id,
    name = name,
    description = description,
    price = price,
    unit = unit,
    minOrderQty = min_order_qty,
    stockQuantity = stock_quantity,
    imageUrl = image_url,
    isAvailable = is_available,
    inStock = in_stock,
    categoryId = category,
    categoryName = category_name,
    sellerId = seller,
    sellerName = seller_name,
)