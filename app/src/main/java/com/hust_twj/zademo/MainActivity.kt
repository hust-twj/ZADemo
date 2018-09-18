package com.hust_twj.zademo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hust_twj.zademo.list.ListActivity
import com.hust_twj.zademo.live_end.LiveEndActivity
import com.hust_twj.zademo.moment_2_0_optima.SpannableActivity
import com.hust_twj.zademo.moments_2_0.hot_topic.MomentTopicDetailActivity
import com.hust_twj.zademo.moments_2_0.hot_topic.PublishActivity
import com.hust_twj.zademo.pic_text.PicTextActivity
import com.hust_twj.zademo.queen_heart.QueenHeartActivity
import com.hust_twj.zademo.span.SpanActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //动态2.0
        moment_2_0.setOnClickListener {
            startActivity(Intent(this, PublishActivity::class.java))
        }

        //动态2.0优化， Spannable测试
        moment_2_0_optima.setOnClickListener {
            startActivity(Intent(this, SpannableActivity::class.java))
        }

        moment_topic_detail.setOnClickListener {
            MomentTopicDetailActivity.start(this, 10)
        }

        pic_text.setOnClickListener {
            startActivity(Intent(this, PicTextActivity::class.java))
        }
        list.setOnClickListener {
            startActivity(Intent(this, ListActivity::class.java))
        }

        queen_heart.setOnClickListener{
            startActivity(Intent(this, QueenHeartActivity::class.java))
        }

        live_end.setOnClickListener{
            startActivity(Intent(this, LiveEndActivity::class.java))
        }
        span.setOnClickListener{
            startActivity(Intent(this, SpanActivity::class.java))
        }

    }
}
