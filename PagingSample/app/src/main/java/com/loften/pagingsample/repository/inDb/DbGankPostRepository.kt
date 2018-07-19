package com.loften.pagingsample.repository.inDb

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.LivePagedListBuilder
import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.db.GankDb
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.repository.GankPostRepository
import com.loften.pagingsample.repository.Listing
import com.loften.pagingsample.repository.NetworkState
import com.loften.pagingsample.vo.GankPost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class DbGankPostRepository(
        val db: GankDb,
        private val gankApi: GankApi,
        private val ioExecutor: Executor,
        private val networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE): GankPostRepository {

    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 30
    }

    private var page = 1

    private fun insertResultIntoDb(category: String, body: List<SubCategoryResult>) {
        body!!.let { posts ->
            db.runInTransaction {
                var gankPosts : MutableList<GankPost> = ArrayList()
                for (item in posts){
                    val gankPost: GankPost = GankPost(item._id,
                    item.content,
                    item.cover,
                    item.crawled,
                    item.created_at,
                    item.deleted,
                    item.published_at,
                    item.raw,
                    item.title,
                    item.uid,
                    item.url,
                    category)
                    gankPosts.add(gankPost)
                }
                db.posts().insert(gankPosts)
            }
        }
    }

    private fun refresh(category: String): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        gankApi.getSubCategoryDatas(category, networkPageSize, page).enqueue(
                object : Callback<BaseResp<List<SubCategoryResult>>> {
                    override fun onFailure(call: Call<BaseResp<List<SubCategoryResult>>>, t: Throwable) {
                        networkState.value = NetworkState.error(t.message)
                    }

                    override fun onResponse(
                            call: Call<BaseResp<List<SubCategoryResult>>>,
                            response: Response<BaseResp<List<SubCategoryResult>>>) {
                        ioExecutor.execute {
                            db.runInTransaction {
                                db.posts().deleteByCategory(category)
                                insertResultIntoDb(category, response.body()!!.results)
                            }

                            networkState.postValue(NetworkState.LOADED)
                            page++
                        }
                    }
                }
        )
        return networkState
    }

    override fun postsOfSubCategory(category: String, pageSize: Int): Listing<SubCategoryResult> {
        val boundaryCallback = SubCategoryBoundaryCallback(
                webservice = gankApi,
                category = category,
                handleResponse = this::insertResultIntoDb,
                ioExecutor = ioExecutor,
                networkPageSize = networkPageSize)

        val dataSourceFactory = db.posts().postsByCategory(category).map {
            SubCategoryResult(it._id,it.content,it.cover,it.crawled,it.created_at,it.deleted,
                    it.published_at,it.raw, null,it.title,it.uid,it.url)
        }
        val builder = LivePagedListBuilder(dataSourceFactory, pageSize)
                .setBoundaryCallback(boundaryCallback)

        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger, {
            refresh(category)
        })

        return  Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }
}