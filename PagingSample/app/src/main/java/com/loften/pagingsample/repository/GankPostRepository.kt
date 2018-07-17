package com.loften.pagingsample.repository

interface GankPostRepository {

    enum class Type{
        IN_MEMORY_BY_ITEM,
        IN_MEMORY_BY_PAGE,
        DB
    }
}