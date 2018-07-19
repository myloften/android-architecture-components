package com.loften.pagingsample.repository.inDb

import android.arch.paging.PagingRequestHelper
import androidx.paging.PagedList
import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.util.createStatusLiveData
import com.loften.pagingsample.vo.GankPost
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executor

class SubCategoryBoundaryCallback (
        private val category: String,
        private val webservice: GankApi,
        private val handleResponse: (String, List<SubCategoryResult>) -> Unit,
        private val ioExecutor: Executor,
        private val networkPageSize: Int) : PagedList.BoundaryCallback<SubCategoryResult>(){

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL){
            webservice.getSubCategoryDatas(category, networkPageSize, 1)
                    .enqueue(createWebserviceCallback(it))
        }
    }

    private fun insertItemsIntoDb(
            response: Response<BaseResp<List<SubCategoryResult>>>,
            it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(category, response.body()!!.results)
            it.recordSuccess()
        }
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<BaseResp<List<SubCategoryResult>>> {
        return object : Callback<BaseResp<List<SubCategoryResult>>> {
            override fun onFailure(
                    call: Call<BaseResp<List<SubCategoryResult>>>,
                    t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                    call: Call<BaseResp<List<SubCategoryResult>>>,
                    response: Response<BaseResp<List<SubCategoryResult>>>) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}