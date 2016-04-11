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

import java.util.ArrayList;

/**
 * Created by YS on 2016-03-28.
 */
public class AlramReceiver extends BroadcastReceiver {
    private String TAG = "RECIEVER";
    private int subConfDegree;

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getAction();
Log.i(TAG, "알람 리시브: "+name);
        DBManager dbManager = new DBManager(context.getApplicationContext(), "Alarm.db", null, 1);

        if(name.equals("org.daelimie.test.daelimie.TEST")){
            // TODO 매너모드일경우 진동 울리기
            // TODO 매너모드 아닐경우 소리울리기
            // TODO 화면 꺼져있을 경우 화면 키기
            Bundle bundle = intent.getExtras();
            int alarm_id = intent.getIntExtra("alarm_id", 0);
            Log.d(TAG, "알람 ID: "+intent.getIntExtra("alarm_id", 0));
            bundle.clear();

            try {
                ArrayList<JSONObject> allData = dbManager.getAllData();
                for (int i=0; i<allData.size(); i++)
                    Log.d(TAG, allData.get(i).toString());
                JSONObject data = dbManager.getData(alarm_id);
                Log.d(TAG, data.toString());
                JSONArray routes = dbManager.getRoute(data.getInt("_id"));
                Log.d(TAG, routes.getJSONObject(0).toString());
                JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                Log.d(TAG, routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getString("text"));
                long timer = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getLong("value");

                Log.d(TAG, "실제등록 시간: "+timer);
                Log.d(TAG, "저장될 시간: " + (timer * 1000));

                // DB에 현재 인덱스 저장
                Log.d(TAG, "저장하기전 인덱스: "+dbManager.getIndex(alarm_id));
                dbManager.setIndex(alarm_id, 0);
                Log.d(TAG, "저장후 인덱스: " + dbManager.getIndex(alarm_id));
                // 인스턴스 알림 등록
                AlarmHandler.alarmHandler.setInstanceAlarm(context, timer * 1000, steps.toString(), alarm_id);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            Intent popupIntent = new Intent(context.getApplicationContext(), InstantAlram.class);
            popupIntent.putExtra("alarm_id", alarm_id);
            popupIntent.putExtra("title", "출발 알림");
            popupIntent.putExtra("message", "지금 출발해야 늦지 않아요!");
            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popupIntent);


        } else if(name.equals("org.daelimie.test.daelimie.ALARMING")) {
            Bundle bundle = intent.getExtras();
            //Log.d(TAG, intent.getIntExtra("instanceNumber", 0)+"");
            int alarm_id = intent.getIntExtra("alarm_id", 0);
            Log.d(TAG, "알람 ID: "+alarm_id);
            // DB에 현재 인덱스 가져옴
            int step_index = dbManager.getIndex(alarm_id);

            Log.d(TAG, "ALARMING");
            Log.d(TAG, "step_index: "+step_index);
            try {
                JSONArray steps = new JSONArray(intent.getStringExtra("steps"));
                // 마지막 단계가 아닐경우만 실행
                if (steps.length()-1 > step_index) {
                    Log.d(TAG, "ALARMING, 다음 알림 실행");
                    // 다음 알림 실행
                    JSONObject eachStep = steps.getJSONObject(step_index);
                    // 차량 탑승 알림 등록
                    long timer = eachStep.getJSONObject("duration").getInt("value");
                    AlarmHandler.alarmHandler.setInstanceAlarm(context, System.currentTimeMillis() + (timer * 1000), steps.toString(), alarm_id);
                    // DB에 현재 인덱스 저장
                    dbManager.setIndex(alarm_id, step_index+1);
                    Log.d(TAG, "step 다음 인덱스: "+ (step_index+1));

                    // TODO 수단별 분기 나눠야함
                    switch (eachStep.getString("travel_mode")) {
                        case "WALKING": // 걸어갈 경우
                            // 다음에 교통수단 있는가
                            Log.d(TAG, "WALKING 다음 스텝: "+steps.getJSONObject(step_index+1).getString("travel_mode"));
                            JSONObject nextStep = steps.getJSONObject(step_index+1);
                            if (nextStep != null && nextStep.getString("travel_mode").equals("TRANSIT")) {
                                // TODO 교통수단 정보 조회
                                pollingTransit(context, nextStep, alarm_id, System.currentTimeMillis() + (timer * 1000));
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
                                    pollingTransit(context, eachStep, alarm_id, System.currentTimeMillis() + (timer * 1000));
                            break;
                    }
                } else {
                    Log.d(TAG, "ALARMING 종료");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            bundle.clear();
        }
    }


    protected void pollingTransit(final Context context, JSONObject eachStep, final int alarm_id, final long tempTime) {
        final String PTAG = "POLLING";
        try {
            // polling
            Log.d(PTAG, "eachStep: "+eachStep.toString());
            final JSONObject detailTransit = eachStep.getJSONObject("transit_details");
            final LatLng departureStop = new LatLng(detailTransit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lat"), detailTransit.getJSONObject("departure_stop").getJSONObject("location").getDouble("lng"));
            final Context mContext = context;
            final Handler repeatHandler = new Handler();
            Runnable reapetRun = new Runnable() {
                @Override
                public void run() {
                    Log.d(PTAG, "반복 실행");
                    try {
                        // TODO 교통수단 역정보 요청
                        boolean previousStop = false;
                        // 임시 도착 정보 제공
                        Log.d(PTAG, "현재시간: " + System.currentTimeMillis() + ", 비교시간: "+tempTime);
                        Log.d(PTAG, "시간 비교값: " + Long.compare(System.currentTimeMillis(), tempTime));

                        switch (detailTransit.getJSONObject("line").getJSONObject("vehicle").getString("type")) {
                            case "BUS": // 버스인 경우
                                // TODO 버스
                                // TODO 도착지까지 시간 계산
                                Log.d(PTAG, "타야할 차량: " + detailTransit.getJSONObject("line").getString("short_name") + " 번");
                                Log.d(PTAG, "타야할 정거장 이름: " + detailTransit.getJSONObject("departure_stop").getString("name"));
                                // TODO 도착지 전전역 버스 도착 계산
                                previousStop = busArrivalCheck("버스정보 입력할 것", tempTime);

                                // TODO 버스 전전역일 경우
                                if (previousStop) {
                                    // 버스 전전역 알림
                                    Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                    popupIntent.putExtra("alarm_id", alarm_id);
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
                                previousStop = subwayArrivalCheck(context, "지하철정보 입력할 것", tempTime);

                                // TODO 지하철 전역일 경우
                                if (previousStop) {
                                    // 지하철 전역 알림
                                    Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                    popupIntent.putExtra("alarm_id", alarm_id);
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
    protected boolean busArrivalCheck(String busInfo, long tempTime) {
        // TODO 도착했는지 안했는지로변경
        if(Long.compare(System.currentTimeMillis(), tempTime) >= 0)
            return true;
        else
            return false;
    }

    // TODO
    protected boolean subwayArrivalCheck(Context context, String subwayInfo, long tempTime) {
        String seoulKey = context.getString(R.string.SEOUL_API_KEY);
        SubwayInfo sbInfo = new SubwayInfo();

        sbInfo.confusionDegreeWithName(seoulKey, "경부선", "노량진", new SubwayInfoCallback() {
            @Override
            public void subwayDegree(int confDegree) {
                subConfDegree = confDegree;
                Log.d(TAG, "지하철 혼잡도: "+subConfDegree);
            }
        });

        // TODO 도착했는지 안했는지로변경
        if(Long.compare(System.currentTimeMillis(), tempTime) >= 0)
            return true;
        else
            return false;
    }


}
