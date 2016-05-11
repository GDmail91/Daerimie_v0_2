package org.daelimie.test.daelimie;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YS on 2016-04-09.
 */
public class SubwayInfo {
    private String TAG = "SuvwayInfo";
    private int confDegree = 0;
    private JSONObject lineInfo;
    private JSONArray upSubInfo = new JSONArray();
    private JSONArray downSubInfo = new JSONArray();
    private JSONObject targetSubInfo = new JSONObject();

    /*public int confusionDegree(String apikey, final String station_name, final SubwayInfoCallback callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SeoulSubwayCom service = retrofit.create(SeoulSubwayCom.class);
        Date date = new Date();
        long interval = System.currentTimeMillis()/1000 - (30 * 24 * 60 * 60);
        date.setTime(interval * 1000);
        int year = date.getYear();
        int month = date.getMonth();

        // 서울 열린데이터 광장에서 가져옴
        Call<LinkedHashMap> res = service.subwayConfusion(apikey);

        // 서울 열린데이터 승하차 정보 받아옴
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String RESULT_CODE = responseData.getJSONObject("CardSubwayTime").getJSONObject("RESULT").getString("CODE");
                    if (RESULT_CODE.equals("INFO-000")) {
                        JSONArray row = responseData.getJSONObject("CardSubwayTime").getJSONArray("row");

                        for (int i=0; i<row.length(); i++) {
                            if (row.getJSONObject(i).getString("SUB_STA_NM").equals(station_name)) {
                                Log.d(TAG, row.getJSONObject(i).getString("SUB_STA_NM"));
                                String RIDE = "_RIDE_NUM";
                                String ALIGHT = "_ALIGHT_NUM";
                                Date date = new Date();
                                int hour = date.getHours();
                                switch (hour) {
                                    case 4:
                                        RIDE += "FOUR";
                                        ALIGHT += "FOUR";
                                        break;
                                    case 5:
                                        RIDE += "FIVE";
                                        ALIGHT += "FIVE";
                                        break;
                                    case 6:
                                        RIDE += "SIX";
                                        ALIGHT += "SIX";
                                        break;
                                    case 7:
                                        RIDE += "SEVEN";
                                        ALIGHT += "SEVEN";
                                        break;
                                    case 8:
                                        RIDE += "EIGHT";
                                        ALIGHT += "EIGHT";
                                        break;
                                    case 9:
                                        RIDE += "NINE";
                                        ALIGHT += "NINE";
                                        break;
                                    case 10:
                                        RIDE += "TEN";
                                        ALIGHT += "TEN";
                                        break;
                                    case 11:
                                        RIDE += "ELEVEN";
                                        ALIGHT += "ELEVEN";
                                        break;
                                    case 12:
                                        RIDE += "TWELVE";
                                        ALIGHT += "TWELVE";
                                        break;
                                    case 13:
                                        RIDE += "THIRTEEN";
                                        ALIGHT += "THIRTEEN";
                                        break;
                                    case 14:
                                        RIDE += "FOURTEEN";
                                        ALIGHT += "FOURTEEN";
                                        break;
                                    case 15:
                                        RIDE += "FIFTEEN";
                                        ALIGHT += "FIFTEEN";
                                        break;
                                    case 16:
                                        RIDE += "SIXTEEN";
                                        ALIGHT += "SIXTEEN";
                                        break;
                                    case 17:
                                        RIDE += "SEVENTEEN";
                                        ALIGHT += "SEVENTEEN";
                                        break;
                                    case 18:
                                        RIDE += "EIGHTEEN";
                                        ALIGHT += "EIGHTEEN";
                                        break;
                                    case 19:
                                        RIDE += "NINETEEN";
                                        ALIGHT += "NINETEEN";
                                        break;
                                    case 20:
                                        RIDE += "TWENTY";
                                        ALIGHT += "TWENTY";
                                        break;
                                    case 21:
                                        RIDE += "TWENTY_ONE";
                                        ALIGHT += "TWENTY_ONE";
                                        break;
                                    case 22:
                                        RIDE += "TWENTY_TWO";
                                        ALIGHT += "TWENTY_TWO";
                                        break;
                                    case 23:
                                        RIDE += "TWENTY_THREE";
                                        ALIGHT += "TWENTY_THREE";
                                        break;
                                    case 24:
                                        RIDE += "MIDNIGHT";
                                        ALIGHT += "MIDNIGHT";
                                        break;
                                }
                                int rideNum = row.getJSONObject(i).getInt(RIDE);
                                int alightNum = row.getJSONObject(i).getInt(ALIGHT);

                                if (rideNum+alightNum > 70000)
                                    confDegree = 80;
                                else if (rideNum+alightNum > 50000)
                                    confDegree = 50;
                                else if (rideNum+alightNum > 30000)
                                    confDegree = 20;
                                else
                                    confDegree = 10;

                                callback.subwayDegree(confDegree);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });
        return confDegree;
    }
*/
    public int confusionDegreeWithName(String apikey, String lineName, String stationName, final SubwayInfoCallback callback) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://openapi.seoul.go.kr:8088")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SeoulSubwayCom service = retrofit.create(SeoulSubwayCom.class);
        Date date = new Date();
        long interval = System.currentTimeMillis()/1000 - (30 * 24 * 60 * 60);
        date.setTime(interval * 1000);
        int year = date.getYear();
        int month = date.getMonth();

        // 서울 열린데이터 광장에서 가져옴
        Call<LinkedHashMap> res = service.subwayConfusionWithName(apikey, lineName, stationName);

        // 서울 열린데이터 승하차 정보 받아옴
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String RESULT_CODE = responseData.getJSONObject("CardSubwayTime").getJSONObject("RESULT").getString("CODE");
                    if (RESULT_CODE.equals("INFO-000")) {
                        JSONArray row = responseData.getJSONObject("CardSubwayTime").getJSONArray("row");

                        String RIDE = "_RIDE_NUM";
                        String ALIGHT = "_ALIGHT_NUM";
                        Date date = new Date();
                        int hour = date.getHours();
                        Log.d(TAG, "현재 시간: "+hour);
                        switch (hour) {
                            case 1:
                                RIDE = "ONE" + RIDE;
                                ALIGHT = "ONE" + ALIGHT;
                                break;
                            case 2:
                                RIDE = "TWO" + RIDE;
                                ALIGHT = "TWO" + ALIGHT;
                                break;
                            case 3:
                                RIDE = "THREE" + RIDE;
                                ALIGHT = "THREE" + ALIGHT;
                                break;
                            case 4:
                                RIDE = "FOUR" + RIDE;
                                ALIGHT = "FOUR" + ALIGHT;
                                break;
                            case 5:
                                RIDE = "FIVE" + RIDE;
                                ALIGHT = "FIVE" + ALIGHT;
                                break;
                            case 6:
                                RIDE = "SIX" + RIDE;
                                ALIGHT = "SIX" + ALIGHT;
                                break;
                            case 7:
                                RIDE = "SEVEN" + RIDE;
                                ALIGHT = "SEVEN" + ALIGHT;
                                break;
                            case 8:
                                RIDE = "EIGHT" + RIDE;
                                ALIGHT = "EIGHT" + ALIGHT;
                                break;
                            case 9:
                                RIDE = "NINE" + RIDE;
                                ALIGHT = "NINE" + ALIGHT;
                                break;
                            case 10:
                                RIDE = "TEN" + RIDE;
                                ALIGHT = "TEN" + ALIGHT;
                                break;
                            case 11:
                                RIDE = "ELEVEN" + RIDE;
                                ALIGHT = "ELEVEN" + ALIGHT;
                                break;
                            case 12:
                                RIDE = "TWELVE" + RIDE;
                                ALIGHT = "TWELVE" + ALIGHT;
                                break;
                            case 13:
                                RIDE = "THIRTEEN" + RIDE;
                                ALIGHT = "THIRTEEN" + ALIGHT;
                                break;
                            case 14:
                                RIDE = "FOURTEEN" + RIDE;
                                ALIGHT = "FOURTEEN" + ALIGHT;
                                break;
                            case 15:
                                RIDE = "FIFTEEN" + RIDE;
                                ALIGHT = "FIFTEEN" + ALIGHT;
                                break;
                            case 16:
                                RIDE = "SIXTEEN" + RIDE;
                                ALIGHT = "SIXTEEN" + ALIGHT;
                                break;
                            case 17:
                                RIDE = "SEVENTEEN" + RIDE;
                                ALIGHT = "SEVENTEEN" + ALIGHT;
                                break;
                            case 18:
                                RIDE = "EIGHTEEN" + RIDE;
                                ALIGHT = "EIGHTEEN" + ALIGHT;
                                break;
                            case 19:
                                RIDE = "NINETEEN" + RIDE;
                                ALIGHT = "NINETEEN" + ALIGHT;
                                break;
                            case 20:
                                RIDE = "TWENTY" + RIDE;
                                ALIGHT = "TWENTY" + ALIGHT;
                                break;
                            case 21:
                                RIDE = "TWENTY_ONE" + RIDE;
                                ALIGHT = "TWENTY_ONE" + ALIGHT;
                                break;
                            case 22:
                                RIDE = "TWENTY_TWO" + RIDE;
                                ALIGHT = "TWENTY_TWO" + ALIGHT;
                                break;
                            case 23:
                                RIDE = "TWENTY_THREE" + RIDE;
                                ALIGHT = "TWENTY_THREE" + ALIGHT;
                                break;
                            case 24:
                            case 0:
                                RIDE = "MIDNIGHT" + RIDE;
                                ALIGHT = "MIDNIGHT" + ALIGHT;
                                break;
                        }
                        int rideNum = row.getJSONObject(0).getInt(RIDE);
                        int alightNum = row.getJSONObject(0).getInt(ALIGHT);

                        // TODO 혼잡도 평가할 기준치 정할 것
                        if (rideNum+alightNum > 70000)
                            confDegree = 80;
                        else if (rideNum+alightNum > 50000)
                            confDegree = 50;
                        else if (rideNum+alightNum > 30000)
                            confDegree = 20;
                        else
                            confDegree = 10;

                        callback.subwayDegree(confDegree);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });


        return 0;
    }

    public void getSubwayArrivalList(final String apikey, final String lineNum, final String stationName, final String arrivalName, final SubwayInfoCallback callback) {
Log.d(TAG, "지하철 도착정보 가져옴");
        Log.d(TAG, "도착정보 가져오는 데이터: "+lineNum + " / "+stationName+" / "+arrivalName);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://swopenAPI.seoul.go.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SeoulSubwayCom service = retrofit.create(SeoulSubwayCom.class);

        // 서울특별시 Topis에서 호선정보 가져옴
        Call<LinkedHashMap> lineRes = service.subwayLineInfo(apikey, lineNum);

        // 가져온 호선정보 응답 처리
        lineRes.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String RESULT_CODE = responseData.getJSONObject("errorMessage").getString("code");
                    if (RESULT_CODE.equals("INFO-000")) {
                        JSONArray row = responseData.getJSONArray("lineList");
                        lineInfo = row.getJSONObject(0);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });


        // 서울특별시 Topis에서 실시간 도착정보 가져옴
        Call<LinkedHashMap> arrivalRes = service.subwayArrivalList(apikey, stationName);

        // 실시간 도착정보 응답 처리
        arrivalRes.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String RESULT_CODE = responseData.getJSONObject("errorMessage").getString("code");
                    if (RESULT_CODE.equals("INFO-000")) {
                        final JSONArray row = responseData.getJSONArray("realtimeArrivalList");

                        for(int i=0; i<row.length(); i++) {

                        }

                        getDestinationInfo(apikey, lineNum, arrivalName, new SeoulDesSubwayCom() {
                            @Override
                            public void destinationInfo(JSONObject stationInfo) {
                                try {
                                    for(int i=0; i<row.length(); i++) {
                                        Log.d(TAG, "row 실행: "+i);
                                        if (row.getJSONObject(i).getString("subwayId").equals(lineInfo.getString("subwayId"))) {
                                            // 각 정보를 상,하행으로 구분
                                            switch (row.getJSONObject(i).getString("updnLine")) {
                                                case "상행":
                                                case "외선":
                                                    upSubInfo.put(row.getJSONObject(i));
                                                    break;

                                                case "하행":
                                                case "내선":
                                                    downSubInfo.put(row.getJSONObject(i));
                                                    break;

                                            }

                                            // target이 셋팅되어 있다면 실행하지 않음
                                            //if (targetSubInfo != null || targetSubInfo.length() != 0) {
                                            // 구분된 정보를 목적지와 비교하여 타야할 차량의 상,하행 구분
                                            if (row.getJSONObject(i).getInt("statnId") < stationInfo.getInt("statnId")) {
                                                if (row.getJSONObject(i).getString("updnLine").equals("상행")
                                                        || row.getJSONObject(i).getString("updnLine").equals("외선")) {
                                                    targetSubInfo = row.getJSONObject(i);
                                                }
                                            } else if (row.getJSONObject(i).getInt("statnId") > stationInfo.getInt("statnId")) {
                                                if (row.getJSONObject(i).getString("updnLine").equals("하행")
                                                        || row.getJSONObject(i).getString("updnLine").equals("내선")) {
                                                    targetSubInfo = row.getJSONObject(i);
                                                }
                                            }

                                        }
                                    }
                                    Log.d(TAG, "상행 정보: "+upSubInfo.toString());
                                    Log.d(TAG, "하행 정보: "+downSubInfo.toString());
                                    // 모든 정보가 들어온 경우 콜백
                                    switch (targetSubInfo.getString("updnLine")) {
                                        case "상행":
                                        case "외선":
                                            callback.subwayArrivalList(upSubInfo, targetSubInfo);
                                            break;

                                        case "하행":
                                        case "내선":
                                            callback.subwayArrivalList(downSubInfo, targetSubInfo);
                                            break;

                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });
    }

    private void getDestinationInfo(String apikey, final String stationLine, String stationName, final SeoulDesSubwayCom callback) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://swopenAPI.seoul.go.kr")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SeoulSubwayCom service = retrofit.create(SeoulSubwayCom.class);

        // 서울특별시 Topis에서 호선정보 가져옴
        Call<LinkedHashMap> lineRes = service.subwayStationInfo(apikey, stationName);

        // 가져온 호선정보 응답 처리
        lineRes.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String RESULT_CODE = responseData.getJSONObject("errorMessage").getString("code");
                    if (RESULT_CODE.equals("INFO-000")) {
                        JSONArray row = responseData.getJSONArray("stationList");

                        for(int i=0; i<row.length(); i++) {
                            if (row.getJSONObject(i).getString("subwayNm").equals(stationLine)) {
                                JSONObject stationInfo = row.getJSONObject(i);

                                callback.destinationInfo(stationInfo);
                            }
                        }
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
}
