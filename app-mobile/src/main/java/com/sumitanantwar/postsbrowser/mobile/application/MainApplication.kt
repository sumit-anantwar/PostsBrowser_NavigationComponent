package com.sumitanantwar.postsbrowser.mobile.application

import android.app.Activity
import android.app.Application
import androidx.fragment.app.Fragment
import com.crashlytics.android.Crashlytics

import com.facebook.stetho.Stetho
import com.sumitanantwar.postsbrowser.mobile.application.di.DaggerAppComponent

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.support.HasSupportFragmentInjector
import io.fabric.sdk.android.Fabric

import timber.log.Timber
import javax.inject.Inject


class MainApplication : DaggerApplication() {


    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        val appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()

        appComponent.inject(this)

        return appComponent
    }

    //======= Static Accessor =======
    companion object {
        fun get(activity: Activity) : MainApplication {
            return activity.application as MainApplication
        }
    }


    // ======= Application Lifecycle =======
    override fun onCreate() {
        super.onCreate()


        // Initialize Crashlytics
        Fabric.with(this, Crashlytics())

        // Initialize Stetho
        Stetho.initialize(
            Stetho.newInitializerBuilder(this)
                .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                .build())

        // Plant a Timber Debug Tree
        Timber.plant(Timber.DebugTree())
    }

}