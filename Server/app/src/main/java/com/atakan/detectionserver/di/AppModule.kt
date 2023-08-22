package com.atakan.detectionserver.di

import com.atakan.detectionserver.presentation.ImageViewModel
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

}