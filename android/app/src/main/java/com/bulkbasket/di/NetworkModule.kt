package com.bulkbasket.di

import com.bulkbasket.data.remote.interceptors.AuthInterceptor
import com.bulkbasket.utils.Constants
import com.bulkbasket.data.remote.api.AuthApi
import com.bulkbasket.data.remote.api.DeliveryApi
import com.bulkbasket.data.remote.api.NotificationsApi
import com.bulkbasket.data.remote.api.OrdersApi
import com.bulkbasket.data.remote.api.ProductsApi
import com.bulkbasket.data.remote.api.SellersApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideProductsApi(retrofit: Retrofit): ProductsApi {
        return retrofit.create(ProductsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideOrdersApi(retrofit: Retrofit): OrdersApi {
        return retrofit.create(OrdersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideSellersApi(retrofit: Retrofit): SellersApi {
        return retrofit.create(SellersApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNotificationsApi(retrofit: Retrofit): NotificationsApi {
        return retrofit.create(NotificationsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDeliveryApi(retrofit: Retrofit): DeliveryApi {
        return retrofit.create(DeliveryApi::class.java)
    }
}