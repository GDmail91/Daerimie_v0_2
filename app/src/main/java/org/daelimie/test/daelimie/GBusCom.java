package org.daelimie.test.daelimie;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by YS on 2016-04-12.
 */
public interface GBusCom {
    @GET("bus/arrival/{busNum}/{stationName}")
    Call<LinkedHashMap> getBusStationID(
            @Path("busNum") String busNum,
            @Path("stationName") String stationName,
            @Query("bus_agency") String busAgency
    );

    @GET("ws/rest/busarrivalservice/station")
    Call<LinkedHashMap> busArrival(
            @Query("serviceKey") String serviceKey,
            @Path("stationId") String stationId
    );
}
