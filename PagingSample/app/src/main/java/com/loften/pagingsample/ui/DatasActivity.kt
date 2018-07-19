package com.loften.pagingsample.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.loften.pagingsample.R
import com.loften.pagingsample.bean.BaseResp
import com.loften.pagingsample.bean.CategoryResult
import com.loften.pagingsample.bean.SubCategoryResult
import com.loften.pagingsample.net.GankApi
import com.loften.pagingsample.net.RetrofitFactory
import com.loften.pagingsample.repository.GankPostRepository
import com.loften.pagingsample.repository.NetworkState
import com.loften.pagingsample.repository.ServiceLocator
import kotlinx.android.synthetic.main.activity_datas.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DatasActivity : AppCompatActivity() {

    companion object {
        const val KEY_REPOSITORY_TYPE = "repository_type"
        fun startActivity(context: Context, type: GankPostRepository.Type){
            val i = Intent(context, DatasActivity::class.java)
            i.putExtra(KEY_REPOSITORY_TYPE, type.ordinal)
            context.startActivity(i)
        }
    }

    private var titles: MutableList<CategoryResult> = arrayListOf()
    private lateinit var model: SubCategoryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_datas)
        initTabs()
        model = getViewModel()
        initAdapter()
        initSwipeToRefresh()

    }

    private fun getViewModel(): SubCategoryViewModel {
        return ViewModelProviders.of(this, object: ViewModelProvider.Factory{
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                val repoTypeParam = intent.getIntExtra(KEY_REPOSITORY_TYPE, 0)
                val repoType = GankPostRepository.Type.values()[repoTypeParam]
                val repo = ServiceLocator.instance(this@DatasActivity)
                        .getRepository(repoType)
                return SubCategoryViewModel(repo) as T
            }
        })[SubCategoryViewModel::class.java]
    }

    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayout.VERTICAL
        list.layoutManager = mLayoutManager
        val adapter = PostsAdapter()
        list.adapter = adapter
        model.posts.observe(this, Observer<PagedList<SubCategoryResult>>{
            adapter.submitList(it)
        })
    }

    private fun initSwipeToRefresh() {
        swipe_refresh.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        model.refreshState.observe(this, Observer {
            swipe_refresh.isRefreshing = it == NetworkState.LOADING
        })
        swipe_refresh.setOnRefreshListener {
            model.refresh()
        }
    }

    private fun initTabs() {

        RetrofitFactory.getInstance().create(GankApi::class.java).getCategory()
                .enqueue(object : Callback<BaseResp<List<CategoryResult>>>{
                    override fun onFailure(call: Call<BaseResp<List<CategoryResult>>>?, t: Throwable?) {
                        showToast("出错啦")
                        //设置默认
                        val categoryResult = CategoryResult("57c83792421aa97cada9b79d",
                        "2016-09-01T22:13:38.420Z",
                        "http://ww2.sinaimg.cn/large/610dc034gw1f9sg2pq9ufj202s02s0sj.jpg",
                        "qdaily",
                       "好奇心日报")
                        titles.add(categoryResult)
                        tabs.addTab(tabs.newTab().setText(categoryResult.title))
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
                model.showSubCategory(titles.get(p0!!.position).id)
            }

        })
    }


    private fun showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}
