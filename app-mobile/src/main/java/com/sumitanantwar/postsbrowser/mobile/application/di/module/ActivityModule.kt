package com.sumitanantwar.postsbrowser.mobile.application.di.module

import com.sumitanantwar.postsbrowser.mobile.ui.main.MainActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector(modules = [FragmentModule::class])
    abstract fun contributesMainActivity(): MainActivity

}