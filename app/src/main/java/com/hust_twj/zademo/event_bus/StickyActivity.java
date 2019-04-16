package com.hust_twj.zademo.event_bus;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.hust_twj.zademo.R;
import com.hust_twj.zademo.event_bus.event.StickyEvent;
import com.hust_twj.zademo.utils.LogUtils;
import com.hust_twj.zademo.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by hust_twj
 * on 2019/2/26
 *
 */
public class StickyActivity extends Activity {

    private TextView mTvSticky;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.builder().build().register(this);

        setContentView(R.layout.activity_sticky);

        mTvSticky = findViewById(R.id.tv_sticky);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.builder().build().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public void getSticky(StickyEvent event){
        LogUtils.e("twj124","StickyEvent receive: " +  event.msg);

        ToastUtils.toast(this, "已接收  " + event.msg);
        mTvSticky.setText(event.msg);
    }

}
