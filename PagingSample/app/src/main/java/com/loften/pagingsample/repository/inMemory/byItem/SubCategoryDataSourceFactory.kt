package com.loften.pagingsample.repository.inMemory.byItem

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import java.util.concurrent.Executor

class SubCategoryDataSourceFactory (
        private val gankApi: GankApi,
        private val category: String,
        private val retryExecutor: Executor): DataSource.Factory<String, SubCategoryResult>(){
    val sourceLiveDate = MutableLiveData<ItemKeyedSubCategoryDataSource>()
    override fun create(): DataSource<String, SubCategoryResult> {
        val source = ItemKeyedSubCategoryDataSource(gankApi, category, retryExecutor)
        sourceLiveDate.postValue(source)
        return source
    }
}