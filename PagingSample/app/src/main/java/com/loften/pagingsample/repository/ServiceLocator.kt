package com.loften.pagingsample.repository

import android.app.Application
import android.content.Context
import com.loften.pagingsample.db.GankDb
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.net.RetrofitFactory
import com.loften.pagingsample.repository.inDb.DbGankPostRepository
import com.loften.pagingsample.repository.inMemory.byItem.InMemoryByItemRepository
import java.util.concurrent.Executor
import java.util.concurrent.Executors

interface ServiceLocator  {
    companion object {
        private val LOCK = Any()
        private var instance: ServiceLocator? = null
        fun instance(context: Context): ServiceLocator{
            synchronized(LOCK){
                if(instance == null){
                    instance = DefaultServiceLocator(
                            app = context.applicationContext as Application,
                            useInMemoryDb = false)
                }
                return instance!!
            }
        }
    }

    fun getRepository(type: GankPostRepository.Type): GankPostRepository

    fun getNetworkExecutor(): Executor

    fun getDiskIOExecutor(): Executor

    fun getGankApi(): GankApi
}

open class DefaultServiceLocator(val app: Application, val useInMemoryDb: Boolean): ServiceLocator{

    private val DISK_IO = Executors.newSingleThreadExecutor()

    private val NETWORK_IO = Executors.newFixedThreadPool(5)

    private val db by lazy {
        GankDb.create(app, useInMemoryDb)
    }

    private val api by lazy {
        RetrofitFactory.getInstance().create(GankApi::class.java)
    }

    override fun getRepository(type: GankPostRepository.Type): GankPostRepository {
        return when(type){
            GankPostRepository.Type.IN_MEMORY_BY_ITEM -> InMemoryByItemRepository(getGankApi(),getNetworkExecutor())
            GankPostRepository.Type.IN_MEMORY_BY_PAGE -> InMemoryByItemRepository(getGankApi(),getNetworkExecutor())
            GankPostRepository.Type.DB -> DbGankPostRepository(
                    db = db,
                    gankApi = getGankApi(),
                    ioExecutor = getDiskIOExecutor())
        }
    }

    override fun getNetworkExecutor(): Executor = NETWORK_IO

    override fun getDiskIOExecutor(): Executor = DISK_IO

    override fun getGankApi(): GankApi = api

}