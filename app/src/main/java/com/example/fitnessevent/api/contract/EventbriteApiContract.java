package com.example.fitnessevent.api.contract;

import com.example.fitnessevent.api.model.PaginatedEvents;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EventbriteApiContract {
    @GET("/v3/events/search/")
    Call<PaginatedEvents> getEvents(
            @Query("q") String query,
            @Query("location.latitude") double latitude,
            @Query("location.longitude") double longitude,
            @Query("page") int pageNumber);
}
