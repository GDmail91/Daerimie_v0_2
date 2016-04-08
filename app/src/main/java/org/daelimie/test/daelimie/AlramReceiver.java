package org.daelimie.test.daelimie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YS on 2016-03-28.
 */
public class AlramReceiver extends BroadcastReceiver {
    private String TAG = "RECIEVER";

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getAction();
Log.i(TAG, "알람 리시브: "+name);
        if(name.equals("org.daelimie.test.daelimie.TEST")){
            // TODO 매너모드일경우 진동 울리기
            // TODO 매너모드 아닐경우 소리울리기
            // TODO 화면 꺼져있을 경우 화면 키기
            Bundle bundle = intent.getExtras();
            String _TAG = bundle.getString("tag");

            DBManager dbManager = new DBManager(context.getApplicationContext(), "Alarm.db", null, 1);
            try {
                JSONObject data = dbManager.getData(_TAG);
                JSONArray routes = dbManager.getRoute(data.getInt("_id"));
                Log.d(TAG, routes.getJSONArray(0).toString());
                JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                Log.d(TAG, routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getString("text"));
                long timer = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getLong("value");

                Log.d(TAG, "실제등록 시간: "+timer);
                Log.d(TAG, "저장될 시간: "+(timer * 1000));

                // 인스턴스 알림 등록
                AlarmHandler.alarmHandler.setInstanceAlarm(context, timer * 1000, steps.toString(), 0);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent popupIntent = new Intent(context.getApplicationContext(), InstantAlram.class);
            popupIntent.putExtra("tag", _TAG);
            popupIntent.putExtra("title", "출발 알림");
            popupIntent.putExtra("message", "지금 출발해야 늦지 않아요!");
            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popupIntent);
        } else if(name.equals("org.daelimie.test.daelimie.ALARMING")) {
            Bundle bundle = intent.getExtras();
            final String _TAG = bundle.getString("tag");
            int step_index = bundle.getInt("step_index");

            Log.d(TAG, "ALARMING");
            Log.d(TAG, "step_index: "+step_index);
            try {
                JSONArray steps = new JSONArray(bundle.getString("steps"));
                // 마지막 단계가 아닐경우만 실행
                Log.d(TAG, "steps total length: " + steps.length());
                if (steps.length() > step_index) {
                    Log.d(TAG, "ALARMING, 다음 알림 실행");
                    JSONObject eachStep = steps.getJSONObject(step_index);

                    // TODO 수단별 분기 나눠야함
                    switch (eachStep.getString("travel_mode")) {
                        case "WALKING": // 걸어갈 경우
                            // 다음에 교통수단 있는가
                            Log.d(TAG, "WALKING 다음 스텝: "+steps.getJSONObject(step_index+1).getString("travel_mode"));
                            JSONObject nextStep = steps.getJSONObject(step_index + 1);
                            if (nextStep != null && nextStep.getString("travel_mode").equals("TRANSIT")) {
                                // TODO 교통수단 정보 조회
                                pollingTransit(context, nextStep, _TAG);
                                /*switch (nextStep.getJSONObject("transit_detail").getJSONObject("line").getJSONObject("vehicle").getString("type")) {
                                    case "BUS": // 버스인 경우
                                        Log.d(TAG, "타야할 차량: "+nextStep.getJSONObject("transit_detail").getJSONObject("line").getString("short_name")+" 번");
                                        Log.d(TAG, "타야할 정거장 이름: "+nextStep.getJSONObject("transit_detail").getJSONObject("departure_stop").getString("name"));
                                        busArrivalCheck("버스정보 넣어야함");
                                        // TODO 버스 API로 도착 시간 조회
                                        break;
                                    case "SUBWAY": // 지하철인 경우
                                        subwayArrivalCheck("지하철정보 넣어야함");
                                        break;
                                }*/
                            }
                            break;
                        case "TRANSIT": // 교통수단 탈 경우
                                    pollingTransit(context, eachStep, _TAG);
                            break;
                    }

                    // 차량 탑승 알림 등록
                    long timer = eachStep.getJSONObject("duration").getInt("value");
                    AlarmHandler.alarmHandler.setInstanceAlarm(context, System.currentTimeMillis() + (timer * 1000), steps.toString(), step_index);

                } else {
                    Log.d(TAG, "ALARMING 종료");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    protected void pollingTransit(Context context, JSONObject eachStep, final String _TAG) {
        try {
            // polling
            Log.d(TAG, "eachStep: "+eachStep.toString());
            final JSONObject detailTransit = eachStep.getJSONObject("transit_details");
            final LatLng departureStop = new LatLng(detailTransit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lat"), detailTransit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"));
            final Context mContext = context;
            final Handler repeatHandler = new Handler();
            Runnable reapetRun = new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "반복 실행");
                    try {
                        // TODO 교통수단 역정보 요청
                        boolean previousStop = false;

                        switch (detailTransit.getJSONObject("line").getJSONObject("vehicle").getString("type")) {
                            case "BUS": // 버스인 경우
                                // TODO 버스
                                // TODO 도착지까지 시간 계산
                                Log.d(TAG, "타야할 차량: " + detailTransit.getJSONObject("line").getString("short_name") + " 번");
                                Log.d(TAG, "타야할 정거장 이름: " + detailTransit.getJSONObject("departure_stop").getString("name"));
                                // TODO 도착지 전전역 버스 도착 계산
                                previousStop = busArrivalCheck("버스정보 입력할 것");

                                // TODO 버스 전전역일 경우
                                if (previousStop) {
                                    // 버스 전전역 알림
                                    Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                    popupIntent.putExtra("tag", _TAG);
                                    popupIntent.putExtra("title", "전전역 도착 알림");
                                    popupIntent.putExtra("message", "승차 하실건가요?");
                                    popupIntent.putExtra("transit", "BUS");
                                    popupIntent.putExtra("departure_stop_lat", departureStop.latitude);
                                    popupIntent.putExtra("departure_stop_lng", departureStop.longitude);
                                    popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(popupIntent);
                                } else {
                                    // 30초마다 반복
                                    repeatHandler.postDelayed(this, 30 * 1000);
                                }
                                break;
                            case "SUBWAY": // 지하철인 경우
                                // TODO 지하철
                                // TODO 도착지까지 시간 계산
                                // TODO 도착지 전역 지하철 도착 계산
                                previousStop = subwayArrivalCheck("버스정보 입력할 것");

                                // TODO 지하철 전역일 경우
                                if (previousStop) {
                                    // 지하철 전역 알림
                                    Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                    popupIntent.putExtra("tag", _TAG);
                                    popupIntent.putExtra("title", "전역 도착 알림");
                                    popupIntent.putExtra("message", "승차 하실건가요?");
                                    popupIntent.putExtra("transit", "SUBWAY");
                                    popupIntent.putExtra("departure_stop_lat", departureStop.latitude);
                                    popupIntent.putExtra("departure_stop_lng", departureStop.longitude);
                                    popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    mContext.startActivity(popupIntent);
                                } else {
                                    // 30초마다 반복
                                    repeatHandler.postDelayed(this, 30 * 1000);
                                }
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            repeatHandler.postDelayed(reapetRun, 30 * 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // TODO
    protected boolean busArrivalCheck(String busInfo) {
        return true;
    }

    // TODO
    protected boolean subwayArrivalCheck(String subwayInfo) {
        return true;
    }
}
