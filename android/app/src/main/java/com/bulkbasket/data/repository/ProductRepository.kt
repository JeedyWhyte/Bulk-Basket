package com.bulkbasket.data.repository

import com.bulkbasket.data.mappers.toProduct
import com.bulkbasket.data.mappers.toSeller
import com.bulkbasket.data.remote.api.ProductsApi
import com.bulkbasket.data.remote.api.SellersApi
import com.bulkbasket.domain.model.Product
import com.bulkbasket.domain.model.Seller
import com.bulkbasket.domain.repository.IProductRepository
import com.bulkbasket.data.remote.dto.ProductCreateRequest
import com.bulkbasket.data.remote.dto.SellerProfileCreateRequest
import com.bulkbasket.utils.NetworkResult
import javax.inject.Inject

class ProductRepository @Inject constructor(
    private val productsApi: ProductsApi,
    private val sellersApi: SellersApi,
) : IProductRepository {

    override suspend fun getProducts(
        category: String?,
        search: String?,
        minPrice: Double?,
        maxPrice: Double?,
        page: Int,
    ): NetworkResult<List<Product>> {
        return try {
            val response = productsApi.getProducts(
                category = category,
                search = search,
                minPrice = minPrice,
                maxPrice = maxPrice,
                page = page,
            )
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toProduct() }
                )
            } else {
                NetworkResult.Error("Failed to load products", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getProduct(id: Int): NetworkResult<Product> {
        return try {
            val response = productsApi.getProduct(id)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toProduct())
            } else {
                NetworkResult.Error("Product not found", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getNearbySellers(
        lat: Double,
        lng: Double,
        radius: Double,
    ): NetworkResult<List<Seller>> {
        return try {
            val response = sellersApi.getNearbySellers(lat, lng, radius)
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.map { it.toSeller() }
                )
            } else {
                NetworkResult.Error("Failed to load sellers", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getSellerDetail(id: Int): NetworkResult<Seller> {
        return try {
            val response = sellersApi.getSellerDetail(id)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toSeller())
            } else {
                NetworkResult.Error("Seller not found", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createProduct(
        request: ProductCreateRequest
    ): NetworkResult<Product> {
        return try {
            val response = productsApi.createProduct(request)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toProduct())
            } else {
                NetworkResult.Error("Failed to create product", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateProduct(
        id: Int,
        request: ProductCreateRequest,
    ): NetworkResult<Product> {
        return try {
            val response = productsApi.updateProduct(id, request)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toProduct())
            } else {
                NetworkResult.Error("Failed to update product", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun deleteProduct(id: Int): NetworkResult<Unit> {
        return try {
            val response = productsApi.deleteProduct(id)
            if (response.isSuccessful) {
                NetworkResult.Success(Unit)
            } else {
                NetworkResult.Error("Failed to delete product", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getMyProducts(): NetworkResult<List<Product>> {
        return try {
            val response = productsApi.getMyProducts()
            if (response.isSuccessful) {
                NetworkResult.Success(
                    response.body()!!.results.map { it.toProduct() }
                )
            } else {
                NetworkResult.Error("Failed to load products", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun getMySellerProfile(): NetworkResult<Seller> {
        return try {
            val response = sellersApi.getMySellerProfile()
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toSeller())
            } else {
                NetworkResult.Error("Profile not found", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun createSellerProfile(
        request: SellerProfileCreateRequest
    ): NetworkResult<Seller> {
        return try {
            val response = sellersApi.createSellerProfile(request)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toSeller())
            } else {
                NetworkResult.Error("Failed to create profile", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }

    override suspend fun updateSellerProfile(
        request: SellerProfileCreateRequest
    ): NetworkResult<Seller> {
        return try {
            val response = sellersApi.updateSellerProfile(request)
            if (response.isSuccessful) {
                NetworkResult.Success(response.body()!!.toSeller())
            } else {
                NetworkResult.Error("Failed to update profile", response.code())
            }
        } catch (e: Exception) {
            NetworkResult.Error(e.message ?: "Network error")
        }
    }
}