package org.daelimie.test.daelimie;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by YS on 2016-03-20.
 */
public interface TMapCom {
    @FormUrlEncoded
    @POST("tmap/routes/pedestrian")
    Call<LinkedHashMap> routeSearch(
            @Header("AppKey") String AppKey,
            @Query("version") int version,
            @Field("startX") double startX,
            @Field("startY") double startY,
            @Field("endX") double endX,
            @Field("endY") double endY,
            @Field("startName") String startName,
            @Field("endName") String endName,
            @Field("reqCoordType") String reqCoordType,
            @Field("resCoordType") String resCoordType
    );

}
