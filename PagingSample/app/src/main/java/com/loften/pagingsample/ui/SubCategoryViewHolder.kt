package com.loften.pagingsample.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.loften.pagingsample.R
import com.loften.pagingsample.bean.SubCategoryResult

class SubCategoryViewHolder(itemView: View)
    : RecyclerView.ViewHolder(itemView) {
    private val cover: ImageView = itemView.findViewById(R.id.img_cover)
    private val title: TextView = itemView.findViewById(R.id.tv_title)
    private val time: TextView = itemView.findViewById(R.id.tv_time)
    private var post: SubCategoryResult? = null
    init {
        itemView.setOnClickListener {
            post?.url?.let {url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(intent)
            }
        }
    }

    fun bind(post: SubCategoryResult?){
        this.post = post
        Glide.with(itemView).load(post?.cover).into(cover)
        title.text = post?.title ?: "加载中...."
        time.text = post?.created_at
    }

    companion object {
        fun create(parent: ViewGroup): SubCategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_sub_category_detail, parent, false)
            return SubCategoryViewHolder(view)
        }
    }
}