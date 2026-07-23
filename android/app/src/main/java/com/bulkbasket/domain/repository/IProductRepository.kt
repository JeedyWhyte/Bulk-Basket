package com.bulkbasket.domain.repository

import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.utils.NetworkResult

interface IProductRepository {
    suspend fun getProducts(
        category: String? = null,
        search: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        page: Int = 1,
    ): NetworkResult<List<Product>>

    suspend fun getProduct(id: Int): NetworkResult<Product>

    suspend fun getNearbySellers(
        lat: Double,
        lng: Double,
        radius: Double = 10.0,
    ): NetworkResult<List<Seller>>
}