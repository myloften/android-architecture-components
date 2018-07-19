package com.loften.pagingsample.vo

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "posts",//表名
        indices = [(Index(value = ["category"], unique = false))])//索引
data class GankPost (
    @PrimaryKey
    val _id: String,
    val content: String,
    val cover: String,
    val crawled: Long,
    val created_at: String,
    val deleted: Boolean,
    val published_at: String,
    val raw: String,
    val title: String,
    val uid: String,
    val url: String,
    val category: String
)