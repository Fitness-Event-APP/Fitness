package com.example.fitnessevent;

import android.app.Application;

import com.example.fitnessevent.api.EventbriteApi;
import com.example.fitnessevent.api.contract.EventbriteApiContract;
import com.example.fitnessevent.db.CachingDbHelper;
import com.example.fitnessevent.helper.Constants;

public class FitnessEventApplication extends Application {
    private static FitnessEventApplication instance;
    private CachingDbHelper cachingDbHelper;
    private EventbriteApi apiEventbrite;

    public static FitnessEventApplication getApplication(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.instance = this;
        this.apiEventbrite = (EventbriteApi) new EventbriteApi.Builder()
                .baseUrl(Constants.EventbriteApi.URL)
                .contract(EventbriteApiContract.class)
                .build();
        this.cachingDbHelper = new CachingDbHelper(getApplicationContext());
    }

    public CachingDbHelper getCachingDbHelper() {
        return cachingDbHelper;
    }

    public EventbriteApi getApiEventbrite() {
        return apiEventbrite;
    }
}
