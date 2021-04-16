package com.rohitthebest.phoco_theimagesearchingapp.module

import android.content.Context
import androidx.room.Room
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PIXABAY_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SAVED_IMAGE_DATABASE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATABASE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.api.PixabayAPI
import com.rohitthebest.phoco_theimagesearchingapp.api.UnsplashAPI
import com.rohitthebest.phoco_theimagesearchingapp.database.database.CollectionDatabase
import com.rohitthebest.phoco_theimagesearchingapp.database.database.SavedImagesDatabase
import com.rohitthebest.phoco_theimagesearchingapp.database.database.UnsplashPhotoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class UnsplashImageOkHttpClient

@Qualifier
annotation class UnsplashImageRetrofit

@Qualifier
annotation class PixabayImageOkHttpClient

@Qualifier
annotation class PixabayImageRetrofit

@Module
@InstallIn(SingletonComponent::class)
object Module {


    //================================== Unsplash API ====================================

    @UnsplashImageOkHttpClient
    @Singleton
    @Provides
    fun provideUnsplashOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()
    }

    @UnsplashImageRetrofit
    @Singleton
    @Provides
    fun provideUnsplashRetrofit(
            @UnsplashImageOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(UNSPLASH_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUnsplashAPI(
            @UnsplashImageRetrofit retrofit: Retrofit
    ): UnsplashAPI = retrofit.create(UnsplashAPI::class.java)

    //----------------------------------------------------------------------------------------------


    //================================ Pixabay API ====================================

    @PixabayImageOkHttpClient
    @Provides
    @Singleton
    fun providePixabayOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()
    }

    @PixabayImageRetrofit
    @Singleton
    @Provides
    fun providesPixabayRetrofit(

            @PixabayImageOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(PIXABAY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesPixabayImageAPI(
            @PixabayImageRetrofit retrofit: Retrofit
    ): PixabayAPI = retrofit.create(PixabayAPI::class.java)

    //------------------------------------------------------------------------------------------


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

    //---------------------------------------------------------------------------------------------


    //======================= Saved image database ======================================

    @Singleton
    @Provides
    fun provideSavedImageDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            SavedImagesDatabase::class.java,
            SAVED_IMAGE_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun providesSavedImageDao(
            db: SavedImagesDatabase
    ) = db.getSavedImageDao()

    //================================ Collection database =====================================

    @Singleton
    @Provides
    fun providesCollectionDatabase(
            @ApplicationContext context: Context
    ) = Room.databaseBuilder(
            context,
            CollectionDatabase::class.java,
            Constants.COLLECTION_DATABASE_NAME
    )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providesCollectionDao(
            db: CollectionDatabase
    ) = db.getCollectionDao()

    //---------------------------------------------------------------------------------------------
}