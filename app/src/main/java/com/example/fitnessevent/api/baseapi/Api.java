package com.example.fitnessevent.api.baseapi;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.example.fitnessevent.FitnessEventApplication;
import com.example.fitnessevent.api.model.Pagination;
import com.example.fitnessevent.api.model.Text;
import com.example.fitnessevent.helper.Constants;
import com.example.fitnessevent.ui.home.EventActivity;
import com.example.fitnessevent.ui.home.HomeFragment;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class Api<T> {

    private static final long DEFAULT_TIMEOUT = 10;
    private static final long DEFAULT_CACHE_DIR_SIZE = Constants.Size.TWO_MEBIBYTE;
    private T service;
    private Map<CallId, BaseApiCall> ongoingCalls = new HashMap<>();

    public Api(Builder builder) {
        service = buildApiService((T)builder.contract, builder.baseUrl, builder.timeOut, builder.cacheSize);

    }
    private T buildApiService(T contract, String baseUrl, long timeOut, long cacheSize) {
        //An OkHttp interceptor which logs HTTP request and response data.
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(timeOut, TimeUnit.SECONDS)
                .readTimeout(timeOut, TimeUnit.SECONDS)
                .writeTimeout(timeOut, TimeUnit.SECONDS)
                .addInterceptor(logging)
                .addInterceptor(generateDefaultInterceptor())
                .cache(createHttpCache(cacheSize))
                .build();
        // the instance of api using retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create((Class<T>) contract);
    }

    private okhttp3.Cache createHttpCache(long dirSize) {
        String cacheDirectoryName = this.getClass().getSimpleName() + Constants.CACHE;
        File cacheDirectory = new File(FitnessEventApplication.getApplication().getCacheDir(), cacheDirectoryName);
        return new okhttp3.Cache(cacheDirectory, dirSize);
    }

    @NonNull
    private Interceptor generateDefaultInterceptor() {
        return new Interceptor() {
            @NotNull
            @Override
            public Response intercept(@NotNull Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();
                // Add query parameters to every request
                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("token", Constants.EventbriteApi.TOKEN)
                        .build();
                // Request customization: add request url
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();

                return chain.proceed(request);
            }
        };
    }

    protected T getService() {
        return service;
    }

    /**
     * synchronized :Only one thread can execute at a time.
     **/
    protected synchronized <CT> BaseApiCall<CT> registerCall(CallId callId, Cache cachePolicy,
                                                             Callback<CT> callback, Type responseType) {
        if (ongoingCalls.containsKey(callId)) {
            cancelCall(callId);
        }
        // a new call
        BaseApiCall<CT>  newCall = new BaseApiCall<>(this, callId, cachePolicy, callback, responseType);
        // If callback == null, on register then ignore the response
        if (callback == null) {
            newCall.cancelCall();
        }
        ongoingCalls.put(callId, newCall);
        return newCall;
    }

    synchronized void cancelCall(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.cancelCall();
            ongoingCalls.remove(callId);
        }
    }

    synchronized void removeCall(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCalls.remove(callId);
        }
    }

    public synchronized boolean registerCallback(CallId callId, Callback callback) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.updateCallback(callback);
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean unregisterCallback(CallId callId) {
        BaseApiCall ongoingCall = ongoingCalls.get(callId);
        if (ongoingCall != null) {
            ongoingCall.removeCallback();
            return true;
        } else {
            return false;
        }
    }



    public abstract static class Builder<T extends Builder, A extends Api, C> {
        private String baseUrl;
        private C contract;
        private long timeOut;
        private long cacheSize;

        public T baseUrl(String url) {
            baseUrl = url;
            return (T) this;
        }

        public T contract(C Contract) {
            contract = Contract;
            return (T) this;
        }

        public T timeout(long timeout) {
            timeOut = timeout;
            return (T) this;
        }

        public T cacheSize(long size) {
            cacheSize = size;
            return (T) this;
        }

        public abstract A build();

        public void validate() {
            if (TextUtils.isEmpty(baseUrl)) {
                throw new IllegalStateException("baseUrl required!");
            }

            if (contract == null) {
                throw new IllegalStateException("contract required!");
            }

            if (cacheSize == 0) {
                cacheSize = DEFAULT_CACHE_DIR_SIZE;
            }

            if (timeOut == 0) {
                timeOut = DEFAULT_TIMEOUT;
            }
        }
    }
}
