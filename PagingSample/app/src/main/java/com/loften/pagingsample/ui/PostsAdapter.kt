package com.loften.pagingsample.ui

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import com.loften.pagingsample.bean.SubCategoryResult

class PostsAdapter : PagedListAdapter<SubCategoryResult, SubCategoryViewHolder>(POST_COMPARATOR){

    override fun onBindViewHolder(holder: SubCategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):SubCategoryViewHolder{
        return  SubCategoryViewHolder.create(parent)
    }

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<SubCategoryResult>() {
            override fun areContentsTheSame(oldItem: SubCategoryResult, newItem: SubCategoryResult): Boolean =
                    oldItem == newItem

            override fun areItemsTheSame(oldItem: SubCategoryResult, newItem: SubCategoryResult): Boolean =
                    oldItem._id == newItem._id

        }
    }
}