package com.smoothsys.qonsume_pos.Retrofit;

import android.content.SharedPreferences;

import com.smoothsys.qonsume_pos.Utilities.Config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Pontus on 2017-09-17.
 */

public class RetrofitManager {

    private RetrofitManager() {}

    private static Retrofit mRetrofit = null;

    public static String getDevToken() {
        return DEV_TOKEN;
    }

    private static String DEV_TOKEN = "cG9udHVzTWVyY2hAZW1haWwuY29tOkFiQDEyMw";

    public static ISnabbOrderAPI SetupInterfaceFromCredentials(final SharedPreferences prefs) {
        //if(snabbPayAPIInterfaceOld == null) {
        ISnabbOrderAPI i = setupRetrofit();
        //}
        return i;
    }

    private static ISnabbOrderAPI setupRetrofit() {

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();
        okhttpBuilder.addInterceptor(chain -> {

            Request request = chain.request();
            Request.Builder newRequest = request.newBuilder().header("Token", DEV_TOKEN);
            return chain.proceed(newRequest.build());
        });

        mRetrofit = new Retrofit.Builder().baseUrl(Config.getBaseUrl())
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        return mRetrofit.create(ISnabbOrderAPI.class);
    }

    public static ISnabbOrderAPI setupInterfaceFromParameters(final String name, final String password) {

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder();

        okhttpBuilder.addInterceptor(chain -> {

            Request request = chain.request();
            Request.Builder newRequest = request.newBuilder().header("Token", DEV_TOKEN);

            return chain.proceed(newRequest.build());
        });

        mRetrofit = new Retrofit.Builder().baseUrl(Config.getBaseUrl())
                .client(okhttpBuilder.build())
                .addConverterFactory(GsonConverterFactory.create()).build();

        return mRetrofit.create(ISnabbOrderAPI.class);
    }
}