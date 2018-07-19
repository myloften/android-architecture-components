package com.loften.pagingsample.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * 给UI提供的数据类
 */
data class Listing<T> (
        //用于观察UI的分页列表的LiveData
        val pagedList: LiveData<PagedList<T>>,
        //网络请求状态
        val networkState: LiveData<NetworkState>,
        //列表刷新状态
        val refreshState: LiveData<NetworkState>,
        //刷新列表
        val refresh: () -> Unit,
        //重试失败的请求
        val retry: () -> Unit
)