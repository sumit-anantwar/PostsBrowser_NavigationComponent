package com.sumitanantwar.postsbrowser.mobile.application.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.sumitanantwar.postsbrowser.mobile.application.di.ViewModelFactory
import com.sumitanantwar.presentation.postdetails.PostDetailsViewModel
import com.sumitanantwar.presentation.postslist.PostsListViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@Module
abstract class PresentationModule {

    @Binds
    @IntoMap
    @ViewModelKey(PostsListViewModel::class)
    abstract fun bindPostListViewModel(viewModel: PostsListViewModel) : ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PostDetailsViewModel::class)
    abstract fun bindPostDetailsViewModel(viewModel: PostDetailsViewModel) : ViewModel


    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}


@MustBeDocumented
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)