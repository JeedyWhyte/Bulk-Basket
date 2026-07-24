package com.bulkbasket.di

import com.bulkbasket.data.repository.AuthRepository
import com.bulkbasket.data.repository.DeliveryRepository
import com.bulkbasket.data.repository.NotificationRepository
import com.bulkbasket.data.repository.OrderRepository
import com.bulkbasket.data.repository.ProductRepository
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IDeliveryRepository
import com.bulkbasket.domain.repository.INotificationRepository
import com.bulkbasket.domain.repository.IOrderRepository
import com.bulkbasket.domain.repository.IProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        impl: AuthRepository
    ): IAuthRepository

    @Binds
    @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepository
    ): IProductRepository

    @Binds
    @Singleton
    abstract fun bindOrderRepository(
        impl: OrderRepository
    ): IOrderRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(
        impl: NotificationRepository
    ): INotificationRepository

    @Binds
    @Singleton
    abstract fun bindDeliveryRepository(
        impl: DeliveryRepository
    ): IDeliveryRepository
}