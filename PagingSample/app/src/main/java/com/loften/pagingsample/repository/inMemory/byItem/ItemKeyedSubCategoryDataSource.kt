package com.loften.pagingsample.repository.inMemory.byItem

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.repository.NetworkState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class ItemKeyedSubCategoryDataSource(
        private val gankApi: GankApi,
        private val category: String,
        private val retryExecutor: Executor)
    :ItemKeyedDataSource<String, SubCategoryResult>(){

    private var page: Int = 1

    private var retry:(()->Any)? = null

    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()
    fun retryAllFailed(){
        val prevRetry = retry
        retry = null
        prevRetry?.let {
            retryExecutor.execute {
                it.invoke()
            }
        }
    }

    //接收初始加载的数据，在这里需要将获取到的数据通过LoadInitialCallback的onResult进行回调，用于初始化PagedList，并对加载的项目进行计数
    override fun loadInitial(params: LoadInitialParams<String>, callback: LoadInitialCallback<SubCategoryResult>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        gankApi.getSubCategoryDatas(category, params.requestedLoadSize, page).enqueue(
                object : Callback<BaseResp<List<SubCategoryResult>>>{
                    override fun onFailure(call: Call<BaseResp<List<SubCategoryResult>>>, t: Throwable) {
                        retry = {
                            loadInitial(params, callback)
                        }
                        val error = NetworkState.error(t.message ?: "unknown err")
                        initialLoad.postValue(error)
                        networkState.postValue(error)
                    }

                    override fun onResponse(call: Call<BaseResp<List<SubCategoryResult>>>, response: Response<BaseResp<List<SubCategoryResult>>>) {
                        if (response.isSuccessful){
                            val items = response.body()?.results ?: emptyList()
                            retry = null
                            callback.onResult(items)
                            networkState.postValue(NetworkState.LOADED)
                            initialLoad.postValue(NetworkState.LOADED)
                            page++

                        }else{
                            retry = {
                                loadInitial(params, callback)
                            }
                            val error = NetworkState.error("error code: ${response.code()}")
                            initialLoad.postValue(error)
                            networkState.postValue(error)
                        }
                    }

                }
        )
    }

    override fun loadAfter(params: LoadParams<String>, callback: LoadCallback<SubCategoryResult>) {
        networkState.postValue(NetworkState.LOADING)
        gankApi.getSubCategoryDatas(category, params.requestedLoadSize, page).enqueue(
                object : Callback<BaseResp<List<SubCategoryResult>>>{
                    override fun onFailure(call: Call<BaseResp<List<SubCategoryResult>>>, t: Throwable) {
                        retry = {
                            loadAfter(params, callback)
                        }
                        networkState.postValue(NetworkState.error(t.message ?: "unknown err"))
                    }

                    override fun onResponse(call: Call<BaseResp<List<SubCategoryResult>>>, response: Response<BaseResp<List<SubCategoryResult>>>) {
                        if (response.isSuccessful){
                            val items = response.body()?.results ?: emptyList()
                            retry = null
                            callback.onResult(items)
                            networkState.postValue(NetworkState.LOADED)
                            page++

                        }else{
                            retry = {
                                loadAfter(params, callback)
                            }
                            networkState.postValue( NetworkState.error("error code: ${response.code()}"))
                        }
                    }

                }
        )
    }

    override fun loadBefore(params: LoadParams<String>, callback: LoadCallback<SubCategoryResult>) {
    }

    override fun getKey(item: SubCategoryResult): String = item.site!!.id

}