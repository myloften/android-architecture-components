package com.loften.pagingsample.ui

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.tabs.TabLayout
import com.loften.pagingsample.R
import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.CategoryResult
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.net.RetrofitFactory
import com.loften.pagingsample.repository.GankPostRepository
import kotlinx.android.synthetic.main.activity_datas.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatasActivity : AppCompatActivity() {

    var titles: MutableList<CategoryResult> = arrayListOf()

    companion object {
        const val KEY_REPOSITORY_TYPE = "repository_type"
        fun startActivity(context: Context, type: GankPostRepository.Type){
            val i = Intent(context, DatasActivity::class.java)
            i.putExtra(KEY_REPOSITORY_TYPE, type.ordinal)
            context.startActivity(i)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datas)

        initTabs()
        initAdapter()
        initSwipeToRefresh()
    }

    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayout.VERTICAL
        list.layoutManager = mLayoutManager
    }

    private fun initSwipeToRefresh() {
        swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        swipe_refresh.setOnRefreshListener {

        }
    }

    private fun initTabs() {

        RetrofitFactory.getInstance().create(GankApi::class.java).getCategory()
                .enqueue(object : Callback<BaseResp<List<CategoryResult>>>{
                    override fun onFailure(call: Call<BaseResp<List<CategoryResult>>>?, t: Throwable?) {
                        showToast("出错啦")
                    }

                    override fun onResponse(call: Call<BaseResp<List<CategoryResult>>>?, response: Response<BaseResp<List<CategoryResult>>>?) {
                        if (response!=null && !response.body()!!.error){
                            for (item in response.body()!!.results){
                                titles.add(item)
                                tabs.addTab(tabs.newTab().setText(item.title))
                            }
                        }
                    }
                })

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabReselected(p0: TabLayout.Tab?) {

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {

            }

            override fun onTabSelected(p0: TabLayout.Tab?) {
                tabSelect(p0!!.position)
            }

        })
    }

    private fun tabSelect(position: Int){
        RetrofitFactory.getInstance().create(GankApi::class.java).getSubCategoryDatas(titles[position].id, 1)
                .enqueue(object : Callback<BaseResp<List<SubCategoryResult>>>{
                    override fun onFailure(call: Call<BaseResp<List<SubCategoryResult>>>?, t: Throwable?) {
                        showToast("出错啦")
                    }

                    override fun onResponse(call: Call<BaseResp<List<SubCategoryResult>>>?, response: Response<BaseResp<List<SubCategoryResult>>>?) {

                    }
                })
    }

    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
