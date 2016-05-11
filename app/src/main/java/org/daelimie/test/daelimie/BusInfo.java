package org.daelimie.test.daelimie;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YS on 2016-04-12.
 */
public class BusInfo {
    private String TAG = "BusInfo";

    public void getBusArrivalInfo(String busNum, String stationName, String busAgency, final BusInfoCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://daerimie.mooo.com:3000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GBusCom service = retrofit.create(GBusCom.class);

        Log.d(TAG, busNum + " / "+stationName +" / "+busAgency);
        // 경기버스정보의 기반정보로 도착정보 가져옴
        Call<LinkedHashMap> res = service.getBusStationID(busNum, stationName, busAgency);

        // ID값을 가져온경우 도착정보 루틴 실행
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    Log.d(TAG, responseData.getString("msg"));
                    callback.busInfoCallback(responseData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

            }
        });
    }
/*
    private void getArrivalListBuStation(long stationId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.gbis.go.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GBusCom service = retrofit.create(GBusCom.class);

        // 경기버스정보에서 도착정보 가져옴
        Call<LinkedHashMap> res = service.busArrival("1234567890", stationId);

        // 서울 열린데이터 승하차 정보 받아옴
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    Log.d(TAG, responseData.getString("msg"));
                    if (responseData.getBoolean("result") == true) {
                        JSONArray data = responseData.getJSONArray("data");

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

            }
        });
    }

    @Root
    private class gbusData {

    }*/
}
