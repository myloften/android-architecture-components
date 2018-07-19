package com.loften.pagingsample.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.loften.pagingsample.vo.GankPost

@Dao
interface GankPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts: List<GankPost>)

    @Query("SELECT * FROM posts WHERE category = :category")
    fun postsByCategory(category: String) : DataSource.Factory<Int, GankPost>

    @Query("DELETE FROM posts WHERE category = :category")
    fun deleteByCategory(category: String)


}