package com.rohitthebest.phoco_theimagesearchingapp.module

import android.content.Context
import androidx.room.Room
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATABASE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import com.rohitthebest.phoco_theimagesearchingapp.roomDatabase.database.UnsplashPhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/*@Qualifier
annotation class UnsplashImageAPI*/

@Module
@InstallIn(ApplicationComponent::class)
object Module {


    @Singleton
    @Provides
    fun provideUnsplashOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()
    }

    @Singleton
    @Provides
    fun provideUnsplashRetrofit(
            okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUnsplashAPI(
            retrofit: Retrofit
    ): UnsplashAPI = retrofit.create(UnsplashAPI::class.java)


//======================= Unsplash photo database ======================================

    @Singleton
    @Provides
    fun provideUnsplashPhotoDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            UnsplashPhotoDatabase::class.java,
            UNSPLASH_PHOTO_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providesUnsplashPhotoDao(
            db: UnsplashPhotoDatabase
    ) = db.getUnsplashPhotoDao()
}