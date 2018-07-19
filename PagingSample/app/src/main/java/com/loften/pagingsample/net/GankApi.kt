package com.loften.pagingsample.net

import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.CategoryResult
import com.loften.pagingsample.bean.SubCategoryResult
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface GankApi {

    @GET("api/xiandu/category/wow")
    fun getCategory(): Call<BaseResp<List<CategoryResult>>>

    @GET("api/xiandu/data/id/{type}/count/{count}/page/{page}")
    fun getSubCategoryDatas(@Path("type") type: String, @Path("count") count:Int, @Path("page") page:Int): Call<BaseResp<List<SubCategoryResult>>>


}