package com.loften.pagingsample.bean

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName


data class SubCategoryResult(
    val _id: String,
    val content: String,
    val cover: String,
    val crawled: Long,
    val created_at: String,
    val deleted: Boolean,
    val published_at: String,
    val raw: String,
    val site: Site?,
    val title: String,
    val uid: String,
    val url: String
)

data class Site(
    val cat_cn: String,
    val cat_en: String,
    val desc: String,
    val feed_id: String,
    val icon: String,
    val id: String,
    val name: String,
    val subscribers: Int,
    val type: String,
    val url: String
)