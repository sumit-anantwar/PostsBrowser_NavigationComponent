package com.sumitanantwar.postsbrowser.mobile.application.di.module

import com.sumitanantwar.postsbrowser.mobile.ui.postdetails.PostDetailsFragment
import com.sumitanantwar.postsbrowser.mobile.ui.postlist.PostsListFragment
import com.sumitanantwar.postsbrowser.mobile.ui.splash.SplashFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributesSplashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun contributesPostsListFragment(): PostsListFragment

    @ContributesAndroidInjector
    abstract fun contributesPostsDetailsFragment(): PostDetailsFragment

}