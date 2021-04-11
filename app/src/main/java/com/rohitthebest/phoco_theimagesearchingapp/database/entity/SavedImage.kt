package com.rohitthebest.phoco_theimagesearchingapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitthebest.phoco_theimagesearchingapp.utils.APIsInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.ImageDownloadLinksAndInfo

@Entity(tableName = "saved_image_table")
data class SavedImage(
        @PrimaryKey(autoGenerate = false) var key: String,
        var collectionKey: String = "",
        val timeStamp: Long = System.currentTimeMillis(),
        var apiName: APIsInfo,
        var imageName: String,
        var imageId: String = "",  //this is the image id given by the API
        var imageUrls: ImageDownloadLinksAndInfo.ImageUrls,
        var userInfo: UserInfo,
        var uid: String = ""
) {

    constructor() : this(
            "",
            "",
            System.currentTimeMillis(),
            APIsInfo(),
            "",
            "",
            ImageDownloadLinksAndInfo.ImageUrls(),
            UserInfo(),
            ""

    )
}

data class UserInfo(
        var userName: String,  //the actual name
        var userIdOrUserName: String,
        var userImageUrl: String = ""
) {

    constructor() : this(
            "",
            "",
            ""
    )
}