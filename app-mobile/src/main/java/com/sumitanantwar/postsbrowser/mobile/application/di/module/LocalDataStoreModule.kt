package com.sumitanantwar.postsbrowser.mobile.application.di.module


import com.sumitanantwar.repository.base.LocalDataStore
import com.sumitanantwar.repository.local.LocalDataStoreImpl
import dagger.Binds
import dagger.Module

@Module
abstract class LocalDataStoreModule {

    @Binds
    abstract fun providesLocalDataStore(postsDataStore: LocalDataStoreImpl): LocalDataStore
}