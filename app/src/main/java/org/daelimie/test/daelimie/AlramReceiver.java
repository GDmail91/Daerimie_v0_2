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
    private JSONObject arrivalInfo;
    private boolean isRide;

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
            // 탑승 초기화
            isRide = false;
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
            Runnable repeatRun = new Runnable() {
                @Override
                public void run() {
                    Log.d(PTAG, "반복 실행");
                    try {
                        // TODO 교통수단 역정보 요청
                        boolean previousStop = false;
                        // 임시 도착 정보 제공
                        Log.d(PTAG, "현재시간: " + System.currentTimeMillis() + ", 비교시간: "+tempTime);
                        Log.d(PTAG, "시간 비교값: " + Long.compare(System.currentTimeMillis(), tempTime));

                        Log.d(PTAG, "타야할 차량: " + detailTransit.getJSONObject("line").getString("short_name") + " 번");
                        Log.d(PTAG, "타야할 정거장 이름: " + detailTransit.getJSONObject("departure_stop").getString("name"));
                        final String lineNum = detailTransit.getJSONObject("line").getString("short_name");

                        final String agency = detailTransit.getJSONObject("line").getJSONArray("agencies").getJSONObject(0).getString("name");

                        final Runnable mRun = this;
                        Log.d(TAG, "정거장 이름 : "+detailTransit.getJSONObject("departure_stop").getString("name"));
                        switch (detailTransit.getJSONObject("line").getJSONObject("vehicle").getString("type")) {
                            case "BUS": // 버스인 경우
                                // TODO 버스
                                // TODO 도착지까지 시간 계산
                                // TODO 도착지 전전역 버스 도착 계산

                                BusInfo busInfo = new BusInfo();

                                busInfo.getBusArrivalInfo(lineNum, detailTransit.getJSONObject("departure_stop").getString("name"), agency, new BusInfoCallback() {

                                    @Override
                                    public void busInfoCallback(JSONObject busInfo) {
                                        Log.d(TAG, "busInfo");
                                        try {
                                            if (isRide) {
                                                // 탑승 중일경우 내림알림
                                                // TODO 진동 및 화면 켜기

                                                // 탑승 플래그 off
                                                isRide = false;
                                                Intent popupIntent = new Intent(mContext.getApplicationContext(), InstantAlram.class);
                                                popupIntent.putExtra("title", "내림 알림");
                                                popupIntent.putExtra("message", "내릴 준비 하셔야 합니다.");
                                                popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mContext.startActivity(popupIntent);
                                            } else {
                                                if (busInfo.getBoolean("result") && busInfo.getJSONObject("data").getInt("locate_at1") < 2) {
                                                    // 탑승 플래그 on
                                                    isRide = true;
                                                    // 버스 전전역 알림
                                                    Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                                    popupIntent.putExtra("alarm_id", alarm_id);
                                                    popupIntent.putExtra("title", "버스 도착 알림");
                                                    popupIntent.putExtra("message", "버스가 전전역에 도착하였습니다.\n승차 하실건가요?");
                                                    popupIntent.putExtra("busInfo", busInfo.getJSONObject("data").toString());
                                                    popupIntent.putExtra("transit", "BUS");
                                                    popupIntent.putExtra("departure_stop_lat", departureStop.latitude);
                                                    popupIntent.putExtra("departure_stop_lng", departureStop.longitude);
                                                    popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                    mContext.startActivity(popupIntent);
                                                } else {
                                                    Log.d(TAG, "버스도착 시간 안됨");
                                                    // 30초마다 반복
                                                    repeatHandler.postDelayed(mRun, 30 * 1000);
                                                }
                                            }
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
                            case "SUBWAY": // 지하철인 경우
                                // TODO 지하철
                                // TODO 도착지까지 시간 계산
                                // TODO 도착지 전역 지하철 도착 계산
                                final String seoulKey = context.getString(R.string.SEOUL_API_KEY);
                                SubwayInfo sbInfo = new SubwayInfo();

                                // 지하철 일경우 지하철 역 정보 수정(요청 정보에 맞게)
                                final String departureNameForConf = detailTransit.getJSONObject("departure_stop").getString("name");
                                String tempDepartureName = departureNameForConf;
                                if (departureNameForConf.lastIndexOf("역") > 0)
                                    tempDepartureName = departureNameForConf.substring(0, departureNameForConf.lastIndexOf("역"));
                                final String departureName = tempDepartureName;

                                String tempArrivalName = detailTransit.getJSONObject("arrival_stop").getString("name");
                                if (tempArrivalName.equals("서울역"))
                                    tempArrivalName += "역";
                                if (tempArrivalName.lastIndexOf("역") > 0)
                                    tempArrivalName = tempArrivalName.substring(0, tempArrivalName.lastIndexOf("역"));
                                final String arrivalName = tempArrivalName;

                                sbInfo.getSubwayArrivalList(context.getString(R.string.SEOUL_REALTIME_API_KEY), lineNum, departureName, arrivalName, new SubwayInfoCallback() {
                                    @Override
                                    public void subwayDegree(int confDegree) {
                                        // NOTHING TODO
                                    }

                                    @Override
                                    public void subwayArrivalList(final JSONArray subArrivalInfo, final JSONObject targetSubInfo) {
                                        // TODO 지하철 전역일 경우
                                        try {
                                            String tempLineNum = "";
                                            if (lineNum.equals("1호선") && subArrivalInfo.getJSONObject(0).getInt("statnId") > 1001000133) {
                                                tempLineNum = "경부선";
                                            } else {
                                                tempLineNum = lineNum;
                                            }
                                            Log.d(PTAG, "혼잡도 계산: 시작 " + tempLineNum + " / "+departureName+" / "+subArrivalInfo.getJSONObject(0).toString());
                                            new SubwayInfo().confusionDegreeWithName(seoulKey, tempLineNum, departureName, new SubwayInfoCallback() {
                                                @Override
                                                public void subwayDegree(final int confDegree) {
                                                    subConfDegree = confDegree;
                                                    Log.d(TAG, "지하철 혼잡도: " + subConfDegree);

                                                    try {
                                                        if (isRide) {
                                                            // 탑승 중일경우 내림알림
                                                            // TODO 진동 및 화면 켜기

                                                            // 탑승 플래그 off
                                                            isRide = false;
                                                            Intent popupIntent = new Intent(mContext.getApplicationContext(), InstantAlram.class);
                                                            popupIntent.putExtra("title", "내림 알림");
                                                            popupIntent.putExtra("message", "내릴 준비 하셔야 합니다.");
                                                            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            mContext.startActivity(popupIntent);
                                                        } else {
                                                            if (targetSubInfo.getInt("arvlCd") == 0
                                                                    || targetSubInfo.getInt("arvlCd") == 1
                                                                    || targetSubInfo.getInt("arvlCd") == 3
                                                                    || targetSubInfo.getInt("arvlCd") == 4
                                                                    || targetSubInfo.getInt("arvlCd") == 5) {
                                                                // 탑승 플래그 on
                                                                isRide = true;
                                                                // 지하철 전역 알림
                                                                Intent popupIntent = new Intent(mContext.getApplicationContext(), SelectableAlarm.class);
                                                                popupIntent.putExtra("alarm_id", alarm_id);
                                                                popupIntent.putExtra("title", "지하철 도착 알림");
                                                                popupIntent.putExtra("message", "지하철이 전역에 도착하였습니다.\n승차 하실건가요?\n현재 차량 복잡도: " + confDegree + "%");
                                                                popupIntent.putExtra("subwayInfo", subArrivalInfo.toString());
                                                                popupIntent.putExtra("transit", "SUBWAY");
                                                                //popupIntent.putExtra("confDegree", confDegree);
                                                                popupIntent.putExtra("departure_stop_lat", departureStop.latitude);
                                                                popupIntent.putExtra("departure_stop_lng", departureStop.longitude);
                                                                popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                                mContext.startActivity(popupIntent);
                                                            } else {
                                                                // 30초마다 반복
                                                                repeatHandler.postDelayed(mRun, 30 * 1000);
                                                            }
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }

                                                @Override
                                                public void subwayArrivalList(JSONArray subArrivalInfo, JSONObject targetSubInfo) {
                                                    // NOTHING TODO
                                                }
                                            });
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                break;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            repeatHandler.postDelayed(repeatRun, 30 * 1000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
/*
    // TODO
    protected boolean busArrivalCheck(Context context, String busNum, String stationName, long tempTime) {

        BusInfo busInfo = new BusInfo();

        busInfo.getBusArrivalInfo("504", "신림사거리.신원시장", "서울특별시버스운송사업조합",new BusInfoCallback() {

            @Override
            public void busInfoCallback(JSONObject busInfo) {
                arrivalInfo = busInfo;
            }
        });

        // TODO 도착했는지 안했는지로변경
        if(Long.compare(System.currentTimeMillis(), tempTime) >= 0)
            return true;
        else
            return false;
    }*/
/*
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

            @Override
            public void subwayArrivalList(JSONArray upSubInfo, JSONArray downSubInfo) {
                // NOTHING TODO
            }
        });

        sbInfo.getSubwayArrivalList(context.getString(R.string.SEOUL_REALTIME_API_KEY), "1호선", "서울", new SubwayInfoCallback() {
            @Override
            public void subwayDegree(int confDegree) {
                // NOTHING TODO
            }

            @Override
            public void subwayArrivalList(JSONArray upSubInfo, JSONArray downSubInfo) {
                context.
            }
        });

        // TODO 도착했는지 안했는지로변경
        if(Long.compare(System.currentTimeMillis(), tempTime) >= 0)
            return true;
        else
            return false;
    }*/


}
