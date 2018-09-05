package com.nr_yper.lockscreen.data.service;

import com.LockApp;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    /*
     * Buid Api Service
     * */

    private static Retrofit retrofit = null;


    public static Retrofit getLocckService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(LockApp.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }


}
