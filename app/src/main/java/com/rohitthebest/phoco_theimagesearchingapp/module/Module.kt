package com.rohitthebest.phoco_theimagesearchingapp.module

import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class UnsplashImageAPI

@Module
@InstallIn(ApplicationComponent::class)
object Module {

    val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

    @Provides
    @Singleton
    @UnsplashImageAPI
    fun provideUnsplashOkHttpClient(): OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

    @Provides
    @Singleton
    @UnsplashImageAPI
    fun provideUnsplashRetrofit(
            okHttpClient: OkHttpClient
    ) = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @UnsplashImageAPI
    fun provideUnsplashAPI(
            retrofit: Retrofit
    ) = retrofit.create(UnsplashAPI::class.java)

}