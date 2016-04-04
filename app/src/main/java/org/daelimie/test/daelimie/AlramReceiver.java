package org.daelimie.test.daelimie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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
            // TODO 알람 팜업창 띄우기
            // TODO 매너모드일경우 진동 울리기
            // TODO 매너모드 아닐경우 소리울리기
            // TODO 화면 꺼져있을 경우 화면 키기
            Bundle bundle = intent.getExtras();
            String _TAG = bundle.getString("tag");

            DBManager dbManager = new DBManager(context.getApplicationContext(), "Alarm.db", null, 1);
            try {
                JSONObject data = dbManager.getData(_TAG);
                JSONArray routes = dbManager.getRoute(data.getInt("_id"));
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
            popupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(popupIntent);
        } else if(name.equals("org.daelimie.test.daelimie.ALARMING")) {
            Bundle bundle = intent.getExtras();
            int step_index = bundle.getInt("step_index");

            Log.d(TAG, "ALARMING");
            Log.d(TAG, "step_index: "+step_index);
            try {
                JSONArray steps = new JSONArray(bundle.getString("steps"));
                // 마지막 단계가 아닐경우만 실행
                if (steps.length() > step_index) {
                    JSONObject eachStep = steps.getJSONObject(step_index);
                    long timer = eachStep.getJSONObject("duration").getInt("value");

                    // 인스턴스 알림 등록
                    AlarmHandler.alarmHandler.setInstanceAlarm(context, System.currentTimeMillis() + (timer * 1000), steps.toString(), step_index);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
