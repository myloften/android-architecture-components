package com.loften.pagingsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.loften.pagingsample.repository.GankPostRepository
import com.loften.pagingsample.ui.DatasActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initEvents()
    }

    private fun initEvents() {
        bt1.setOnClickListener{
            DatasActivity.startActivity(this, GankPostRepository.Type.DB)
        }

        bt2.setOnClickListener {
            DatasActivity.startActivity(this, GankPostRepository.Type.IN_MEMORY_BY_ITEM)
        }

    }
}
