package com.loften.pagingsample.repository

import com.loften.pagingsample.bean.SubCategoryResult

interface GankPostRepository {

    fun postsOfSubCategory(category: String, pageSize: Int): Listing<SubCategoryResult>

    enum class Type{
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }
}