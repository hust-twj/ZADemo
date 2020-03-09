package com.hust_twj.zademo.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.hust_twj.zademo.R
import kotlinx.android.synthetic.main.activity_view.*

/**
 * Created by hust_twj
 * on 2019/2/26
 */
class ViewActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view)

        tv_requestLayout.setOnClickListener {
           startActivity(Intent(this@ViewActivity, ViewRequestLayoutActivity::class.java))
        }

        view_dispatch.setOnClickListener {
            startActivity(Intent(this@ViewActivity, ViewDispatchActivity::class.java))

        }
    }


}