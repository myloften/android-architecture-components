package com.loften.pagingsample.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.loften.pagingsample.vo.GankPost

@Database(
        entities = arrayOf(GankPost::class),
        version = 1,
        exportSchema = false
)
abstract class GankDb : RoomDatabase(){
    companion object {
        fun create(context: Context, useInMemory: Boolean): GankDb{
            val databseBuilder = if(useInMemory){
                Room.inMemoryDatabaseBuilder(context, GankDb::class.java)
            }else{
                Room.databaseBuilder(context, GankDb::class.java, "gank.db")
            }
            return databseBuilder
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    abstract fun posts(): GankPostDao
}