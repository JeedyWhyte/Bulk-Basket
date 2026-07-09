package com.bulkbasket.di

import com.bulkbasket.data.repository.AuthRepository
import com.bulkbasket.data.repository.ProductRepository
import com.bulkbasket.domain.repository.IAuthRepository
import com.bulkbasket.domain.repository.IProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindAuthRepository(
        impl: AuthRepository
    ): IAuthRepository

    @Binds
    abstract fun bindProductRepository(
        impl: ProductRepository
    ): IProductRepository
}