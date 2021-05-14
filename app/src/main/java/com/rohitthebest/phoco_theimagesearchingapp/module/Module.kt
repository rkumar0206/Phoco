package com.rohitthebest.phoco_theimagesearchingapp.module

import android.content.Context
import androidx.room.Room
import com.rohitthebest.phoco_theimagesearchingapp.Constants
import com.rohitthebest.phoco_theimagesearchingapp.Constants.MOHIT_IMAGE_API_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PEXEL_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PHOCO_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.PIXABAY_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.SAVED_IMAGE_DATABASE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_BASE_URL
import com.rohitthebest.phoco_theimagesearchingapp.Constants.UNSPLASH_PHOTO_DATABASE_NAME
import com.rohitthebest.phoco_theimagesearchingapp.api.*
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

@Qualifier
annotation class PexelImageOkHttpClient

@Qualifier
annotation class PexelImageRetrofit

@Qualifier
annotation class WebImageOkHttpClient

@Qualifier
annotation class WebImageRetrofit


@Qualifier
annotation class PhocoOkHttpClient

@Qualifier
annotation class PhocoRetrofit

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


    //============================ Pexel API =========================================


    @PexelImageOkHttpClient
    @Provides
    @Singleton
    fun providePexelOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(25, TimeUnit.SECONDS)
            .build()
    }

    @PexelImageRetrofit
    @Singleton
    @Provides
    fun providesPexelRetrofit(
        @PexelImageOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl(PEXEL_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Provides
    @Singleton
    fun providesPexelImageAPI(
            @PexelImageRetrofit retrofit: Retrofit
    ): PexelAPI = retrofit.create(PexelAPI::class.java)


    //---------------------------------------------------------------------------------------

    //============================ Mohit Image API =========================================

    @WebImageOkHttpClient
    @Provides
    @Singleton
    fun provideWebOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()
    }

    @WebImageRetrofit
    @Singleton
    @Provides
    fun providesWebRetrofit(
            @WebImageOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(MOHIT_IMAGE_API_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun providesWebImageAPI(
            @WebImageRetrofit retrofit: Retrofit
    ): MohitImageAPI = retrofit.create(MohitImageAPI::class.java)


    //---------------------------------------------------------------------------------------


    // --------------------------------- Phoco Image API ----------------------------------------

    @PhocoOkHttpClient
    @Provides
    @Singleton
    fun providesPhocoOkHttpClient(): OkHttpClient {

        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(25, TimeUnit.SECONDS)
                .build()
    }

    @PhocoRetrofit
    @Provides
    @Singleton
    fun providesPhocoRetrofit(
            @PhocoOkHttpClient okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(PHOCO_BASE_URL)
            .build()

    @Provides
    @Singleton
    fun providePhocoAPI(
            @PhocoRetrofit retrofit: Retrofit
    ): PhocoAPI = retrofit.create(PhocoAPI::class.java)

    //---------------------------------------------------------------------------------------


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