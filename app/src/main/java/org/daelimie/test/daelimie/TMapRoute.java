package org.daelimie.test.daelimie;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YS on 2016-03-20.
 */
public class TMapRoute {
    private String TAG = "TMapRoute";
    private MyCallback mCallback;

    public static TMapRoute mTMapRoute = new TMapRoute();

    private TMapRoute() {}

    protected void searchRoute(String appKey, String departureName, LatLng departureLocate, String destinationName, LatLng destinationLocate, MyCallback callback) {

        this.mCallback = callback;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://apis.skplanetx.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        TMapCom service = retrofit.create(TMapCom.class);

        try {
            Call<LinkedHashMap> res = service.routeSearch(
                    appKey,
                    1,
                    departureLocate.longitude,
                    departureLocate.latitude,
                    destinationLocate.longitude,
                    destinationLocate.latitude,
                    URLEncoder.encode(departureName, "UTF-8"),
                    URLEncoder.encode(destinationName, "UTF-8"),
                    "WGS84GEO",
                    "WGS84GEO"
            );

            res.enqueue(new Callback<LinkedHashMap>() {

                @Override
                public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                    Log.d(TAG, "응답 받음");
                    try {
                        // 받은 데이터
                        LinkedHashMap temp = response.body();

                        JSONObject responseData = new JSONObject(temp);
                        String type = responseData.getString("type");
                        Log.d(TAG, type);
                        if (type.equals("FeatureCollection")) {
                            // TODO 종료
                            mCallback.httpProcessing(responseData);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public JSONObject searchRoute(String departure, String destination) {
        return new JSONObject();
    }

}
