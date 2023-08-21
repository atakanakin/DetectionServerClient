package com.atakan.mainclient.di

import com.atakan.mainclient.presentation.view_model.ImageViewModel
import com.atakan.mainclient.presentation.view_model.ServiceViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideViewModel() : ImageViewModel {
        return ImageViewModel()
    }

    @Provides
    @Singleton
    fun provideServiceViewModel() : ServiceViewModel {
        return ServiceViewModel(0)
    }
}