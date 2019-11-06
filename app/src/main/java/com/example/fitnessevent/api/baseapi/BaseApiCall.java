package com.example.fitnessevent.api.baseapi;

import android.text.TextUtils;

import com.example.fitnessevent.FitnessEventApplication;
import com.example.fitnessevent.db.CachingDbHelper;
import com.example.fitnessevent.db.DbItem;

import java.io.Serializable;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseApiCall<T> implements Callback<T> {

    private CachingDbHelper cachingDb;
    private Api api;
    private CallId callId;
    private Cache cache;
    private Callback<T> callback;
    private Type responseType;
    private boolean isCancelled = false;
    private Response<T> pendingResponse = null;
    private Call<T> pendingCall = null;
    private Throwable pendingError = null;

    BaseApiCall(Api api, CallId callId, Cache cache, Callback<T> callback, Type responseType) {
        this.cachingDb = FitnessEventApplication.getApplication().getCachingDbHelper();
        this.api = api;
        this.callId = callId;
        this.cache = cache;
        this.callback = callback;
        this.responseType = responseType;
    }

    public boolean requiresNetworkCall() {
        if (cache.policy() == Cache.Policy.NETWORK_ONLY || cache.policy() == Cache.Policy.NETWORK_ELSE_ANY_CACHE) {
            return true;
        }

        DbItem<T> cachedResponse = cachingDb.getDbItem(cache.key(), responseType);
        if (cachedResponse == null) {
            return true;
        }

        if (!cachedResponse.isExpired(cache.ttl())) {
            onResponse(null, Response.success(cachedResponse.getObject()));
            return false;
        } else if (cache.policy() == Cache.Policy.ANY_CACHE_THEN_NETWORK) {
            onResponse(null, Response.success(cachedResponse.getObject()));
        }
        return true;
    }

    @Override
    public void onResponse(Call<T> call, Response<T> response) {
        if (response != null) {
            if (!TextUtils.isEmpty(cache.key()) && response.body() instanceof Serializable) {
                cachingDb.insert(cache.key(), response.body(), cache.ttl());
            }
        }

        if (!isCancelled) {
            if (callback != null) {
                callback.onResponse(call, response);
                api.removeCall(callId);
            } else {
                pendingResponse = response;
                pendingCall = call;
            }
        }
    }

    @Override
    public void onFailure(Call<T> call, Throwable t) {
        if (!isCancelled) {
            if (callback != null) {
                if (cache.policy() == Cache.Policy.CACHE_ELSE_NETWORK_ELSE_ANY_CACHE || cache.policy() == Cache.Policy.NETWORK_ELSE_ANY_CACHE) {
                    DbItem<T> cacheResponse = cachingDb.getDbItem(cache.key(), responseType);
                    if (cacheResponse == null) {
                        callback.onFailure(call, t);
                    } else {
                        callback.onResponse(call, Response.success(cacheResponse.getObject()));
                    }
                } else {
                    callback.onFailure(call, t);
                }
                api.removeCall(callId);
            } else {
                pendingError = t;
            }
        }
    }

    synchronized void cancelCall() {
        removeCallback();
        isCancelled = true;
        api.removeCall(callId);
    }

    synchronized void removeCallback() {
        this.callback = null;
    }

    synchronized void updateCallback(Callback<T> callback) {
        this.callback = callback;
        if (!isCancelled && callback != null) {
            if (pendingResponse != null) {
                onResponse(pendingCall, pendingResponse);
            } else if (pendingError != null) {
                onFailure(pendingCall, pendingError);
            }
        }
    }
}
