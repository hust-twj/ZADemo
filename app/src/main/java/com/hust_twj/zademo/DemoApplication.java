package com.hust_twj.zademo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.didichuxing.doraemonkit.DoraemonKit;
import com.didichuxing.doraemonkit.kit.webdoor.WebDoorManager;
import com.hust_twj.zademo.utils.CrashHandler;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by Wenjing.Tang
 * on 2018/12/20
 */
public class DemoApplication extends Application {

    public static DemoApplication app;

    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler.getInstance().init(this);

        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }

        LeakCanary.install(this);

        registerLifeCallback();

        //集成滴滴的哆啦A梦
        DoraemonKit.install(this);
        // H5任意门功能需要，非必须
        DoraemonKit.setWebDoorCallback(new WebDoorManager.WebDoorCallback() {
            @Override
            public void overrideUrlLoading(Context context, String s) {
                // 使用自己的H5容器打开这个链接
            }
      });
    }

    public static DemoApplication getApp() {
        return app;
    }

    public static Context getContext() {
        return app.getApplicationContext();
    }

    private void registerLifeCallback() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                //LogUtils.e("tw234", "onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                // LogUtils.e("tw234", "onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                // LogUtils.e("tw234", "onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                //LogUtils.e("tw234", "onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                //LogUtils.e("tw234", "onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
                //LogUtils.e("tw234", "onActivitySaveInstanceState");
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                //LogUtils.e("tw234", "onActivityDestroyed");

            }
        });

    }

}
