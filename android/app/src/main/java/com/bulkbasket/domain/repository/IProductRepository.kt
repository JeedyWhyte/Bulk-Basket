package com.bulkbasket.domain.repository

import com.bulkbasket.data.remote.dto.ProductCreateRequest
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.utils.NetworkResult
import com.bulkbasket.data.remote.dto.SellerProfileCreateRequest
import com.bulkbasket.domain.model.Category

interface IProductRepository {
    suspend fun getProducts(
        category: String? = null,
        search: String? = null,
        minPrice: Double? = null,
        maxPrice: Double? = null,
        page: Int = 1,
        pageSize: Int? = null,
    ): NetworkResult<List<Product>>

    suspend fun getProduct(id: Int): NetworkResult<Product>

    suspend fun getNearbySellers(
        lat: Double,
        lng: Double,
        radius: Double = 10.0,
    ): NetworkResult<List<Seller>>

    suspend fun getSellerDetail(id: Int): NetworkResult<Seller>

    suspend fun createProduct(request: ProductCreateRequest): NetworkResult<Product>

    suspend fun updateProduct(id: Int, request: ProductCreateRequest): NetworkResult<Product>

    suspend fun deleteProduct(id: Int): NetworkResult<Unit>

    suspend fun getMyProducts(): NetworkResult<List<Product>>

    suspend fun getMySellerProfile(): NetworkResult<Seller>

    suspend fun createSellerProfile(
        request: SellerProfileCreateRequest
    ): NetworkResult<Seller>

    suspend fun updateSellerProfile(
        request: SellerProfileCreateRequest
    ): NetworkResult<Seller>

    suspend fun getCategories(): NetworkResult<List<Category>>
}
