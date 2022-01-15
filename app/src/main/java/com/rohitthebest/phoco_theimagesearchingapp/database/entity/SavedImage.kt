package com.rohitthebest.phoco_theimagesearchingapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.APIsInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.ImageDownloadLinksAndInfo

@Entity(tableName = "saved_image_table")
data class SavedImage(
    @PrimaryKey(autoGenerate = false) var key: String,
    var collectionKey: String = "",
    val timeStamp: Long = System.currentTimeMillis(),
    var apiInfo: APIsInfo,
    var imageName: String,
    var imageId: String = "",  //this is the image id given by the API,
    var width: Int,
    var height: Int,
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
        0,
        0,
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