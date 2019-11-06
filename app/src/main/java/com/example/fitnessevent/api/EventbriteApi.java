package com.example.fitnessevent.api;



import com.example.fitnessevent.api.baseapi.Api;
import com.example.fitnessevent.api.baseapi.BaseApiCall;
import com.example.fitnessevent.api.baseapi.Cache;
import com.example.fitnessevent.api.baseapi.CallId;
import com.example.fitnessevent.api.contract.EventbriteApiContract;
import com.example.fitnessevent.api.model.PaginatedEvents;

import java.io.IOException;

import retrofit2.Callback;

public class EventbriteApi extends Api<EventbriteApiContract> {

    public EventbriteApi(Builder builder) {
        super(builder);
    }

    public void getEvents(String query, double lat, double lon, PaginatedEvents lastPageLoaded, CallId callId, Callback<PaginatedEvents> callback) throws IOException {
        int pageNumber = lastPageLoaded != null ? lastPageLoaded.getPagination().getPageNumber() + 1 : 1;
        Cache cache = new Cache.Builder()
                .policy(Cache.Policy.CACHE_ELSE_NETWORK)
                .ttl(Cache.Time.ONE_MINUTE)
                .key(String.format("get_events_%1$s_%2$s_%3$s_%4$s", query, lat, lon, pageNumber))
                .build();

        BaseApiCall<PaginatedEvents> apiCall = registerCall(callId, cache, callback, PaginatedEvents.class);

        if (apiCall != null && apiCall.requiresNetworkCall()) {
            getService().getEvents(query, lat, lon, pageNumber).enqueue(apiCall);
        }
    }

    public static class Builder extends Api.Builder {
        @Override
        public EventbriteApi build() {
            super.validate();
            return new EventbriteApi(this);
        }
    }
}
