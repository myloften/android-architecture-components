package com.loften.pagingsample.repository.inMemory.byItem

import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.repository.GankPostRepository
import com.loften.pagingsample.repository.Listing
import java.util.concurrent.Executor

class InMemoryByItemRepository(
        private val gankApi: GankApi,
        private val networkExecutor: Executor): GankPostRepository {

    override fun postsOfSubCategory(category: String, pageSize: Int): Listing<SubCategoryResult> {
        val sourceFactory = SubCategoryDataSourceFactory(gankApi, category, networkExecutor)
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(pageSize*2)
                .setPageSize(pageSize)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .setFetchExecutor(networkExecutor)
                .build()

        val refreshState = Transformations.switchMap(sourceFactory.sourceLiveDate){
            it.networkState
        }

        return Listing(
                pagedList = pagedList,
                networkState = Transformations.switchMap(sourceFactory.sourceLiveDate){
                    it.networkState
                },
                retry = {
                    sourceFactory.sourceLiveDate.value?.retryAllFailed()
                },
                refresh = {
                    sourceFactory.sourceLiveDate.value?.invalidate()
                },
                refreshState = refreshState
        )
    }

}