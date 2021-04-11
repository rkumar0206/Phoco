package com.rohitthebest.phoco_theimagesearchingapp.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collection_table")
data class Collection(
        @PrimaryKey(autoGenerate = false) var key: String,
        val timestamp: Long = System.currentTimeMillis(),
        var collectionName: String,
        var collectionImageUrl: String = "",
        var collectionDescription: String = "",
        var uid: String
) {

    constructor() : this(
            "",
            System.currentTimeMillis(),
            "",
            "",
            "",
            ""
    )
}