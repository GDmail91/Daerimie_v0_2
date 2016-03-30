package org.daelimie.test.daelimie;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by YS on 2016-03-16.
 */
public interface GoogleMapsCom {
    @GET("/maps/api/place/nearbysearch/json")
    Call<LinkedHashMap> nearbySearch(@Query("key") String key, @Query("location") String location, @Query("radius") String radius, @Query("language") String language);

    @GET("/maps/api/place/details/json")
    Call<LinkedHashMap> detailInfo(@Query("key") String key, @Query("placeid") String placeid, @Query("language") String language);

    @GET("/maps/api/directions/json")
    Call<LinkedHashMap> getDirections(
            @Query("key") String key,
            @Query("origin") String origin,
            @Query("destination") String Destination,
            @Query("mode") String mode,
            @Query("alternatives") String alternatives,
            @Query("language") String language,
            @Query("transit_routing_preference") String transit_routing_preference,
            @Query("transit_mode") String transit_mode,
            @Query("arrival_time") String departure_time);
}
