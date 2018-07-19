package com.loften.pagingsample.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.loften.pagingsample.repository.GankPostRepository

class SubCategoryViewModel(private val repository: GankPostRepository): ViewModel() {

    private val categoryName = MutableLiveData<String>()
    private val repoResult = map(categoryName, {
        repository.postsOfSubCategory(it, 5)
    })
    val posts = switchMap(repoResult, { it.pagedList })
    val refreshState = switchMap(repoResult, { it.refreshState })

    fun showSubCategory(category: String): Boolean{
        if (categoryName.value == category){
            return false
        }
        categoryName.value = category
        return true
    }

    fun refresh() = repoResult.value?.refresh?.invoke()

}