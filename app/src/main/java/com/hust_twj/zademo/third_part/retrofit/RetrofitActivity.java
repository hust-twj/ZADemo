package com.hust_twj.zademo.third_part.retrofit;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.hust_twj.zademo.R;
import com.hust_twj.zademo.utils.LogUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Description ：
 * Created by Wenjing.Tang on 2020-01-04.
 */
public class RetrofitActivity extends Activity {


    private TextView mTvResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_retrofit);

        mTvResult = findViewById(R.id.tv_result);

        // 在创建Retrofit实例时通过.baseUrl()设置
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://fanyi.youdao.com/") //设置网络请求的Url地址
                // .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .build();

        // 创建 网络请求接口 的实例
        ServiceInterface service = retrofit.create(ServiceInterface.class);

        //对 发送请求 进行封装
        Call<Translation> call = service.getCall();

        call.enqueue(new Callback<Translation>() {
            @Override
            public void onResponse(Call<Translation> call, Response<Translation> response) {
                String result = response.toString();
                LogUtils.e("twj124", result);

            }

            @Override
            public void onFailure(Call<Translation> call, Throwable t) {
                LogUtils.e("twj124", "onFailure");

            }
        });

    }

}

