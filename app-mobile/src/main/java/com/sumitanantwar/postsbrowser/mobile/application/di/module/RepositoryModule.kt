package com.sumitanantwar.postsbrowser.mobile.application.di.module

import com.sumitanantwar.postsbrowser.mobile.scheduler.RegularSchedulerProvider
import com.sumitanantwar.repository.MainRepositoryImpl
import com.sumitanantwar.repository.base.MainRepository
import com.sumitanantwar.repository.scheduler.SchedulerProvider
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindsUiSchedulerProvider(regularSchedulerProvider: RegularSchedulerProvider): SchedulerProvider

    @Binds
    abstract fun bindsMainRepository(mainRepository: MainRepositoryImpl): MainRepository
}