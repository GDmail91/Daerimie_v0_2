package org.daelimie.test.daelimie;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by YS on 2016-04-09.
 */
public interface SeoulSubwayCom {
    @GET("{apikey}/json/CardSubwayTime/1/5/201603")
    Call<LinkedHashMap> subwayConfusion(
            @Path("apikey") String apikey
    );

    @GET("{apikey}/json/CardSubwayTime/1/5/201603/{line_name}/{station_name}")
    Call<LinkedHashMap> subwayConfusionWithName(
            @Path("apikey") String apikey,
            @Path("line_name") String lineName,
            @Path("station_name") String stationName
    );
}
