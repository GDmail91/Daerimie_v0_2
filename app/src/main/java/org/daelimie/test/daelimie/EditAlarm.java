package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YS on 2016-04-01.
 */
public class EditAlarm extends AddAlram {
    private static final String TAG = "EditAlarm";
    private int ids;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        departureNameView = (TextView) findViewById(R.id.departureNameView);
        destinationNameView = (TextView) findViewById(R.id.destinationNameView);
        arrivalTimeHourView = (TextView) findViewById(R.id.arrival_time_hour);
        arrivalTimeMinuteView = (TextView) findViewById(R.id.arrival_time_minute);
        ampm = (TextView) findViewById(R.id.ampm);

        // 전달받은 데이터 받음
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        ids = bundle.getInt("ids");
        Log.d(TAG, "ids : "+ids);
        DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

        try {
            JSONObject data = dbManager.getData(ids);
            Log.d(TAG, data.toString());

            mAlarmValues.setDeparture(
                    new LatLng(data.getDouble("departureLocateLat"), data.getDouble("departureLocateLng")),
                    data.getString("departurePlaceId"),
                    data.getString("departureName"));
            mAlarmValues.setDestination(
                    new LatLng(data.getDouble("destinationLocateLat"), data.getDouble("destinationLocateLng")),
                    data.getString("destinationPlaceId"),
                    data.getString("destinationName"));
            mAlarmValues.setArrivalTime(
                    data.getInt("arrivalTimeHour"),
                    data.getInt("arrivalTimeMinute"));
            mAlarmValues.setDepartureTime(
                    data.getInt("departureTimeHour"),
                    data.getInt("departureTimeMinute"));
            mAlarmValues.setAlarmInfo(
                    data.getInt("preAlarm"));

            departureNameView.setText(mAlarmValues.getDepartureName());
            destinationNameView.setText(mAlarmValues.getDestinationName());

            // 시간 셋팅
            if (mAlarmValues.getArrivalTimeHour() > 12) {
                arrivalTimeHourView.setText(String.valueOf(mAlarmValues.getArrivalTimeHour()-12));
                ampm.setText("pm");
            } else if (mAlarmValues.getArrivalTimeHour() == 0){
                arrivalTimeHourView.setText("12");
                ampm.setText("am");
            } else {
                arrivalTimeHourView.setText(String.valueOf(mAlarmValues.getArrivalTimeHour()));
                ampm.setText("am");
            }
            // 분 셋팅
            if (mAlarmValues.getArrivalTimeMinute() < 10) {
                arrivalTimeMinuteView.setText("0"+String.valueOf(mAlarmValues.getArrivalTimeMinute()));
            } else {
                arrivalTimeMinuteView.setText(String.valueOf(mAlarmValues.getArrivalTimeMinute()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 출발지 버튼정의
        Button setDeparture = (Button) findViewById(R.id.setDeparture);
        setDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAlarm.this, LocatePicker.class);
                intent.putExtra("BUTTON_FLAG", "DEPARTURE");
                startActivityForResult(intent, 0);
            }
        });

        // 도착지 버튼정의
        Button setDestination = (Button) findViewById(R.id.setDestination);
        setDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAlarm.this, LocatePicker.class);
                intent.putExtra("BUTTON_FLAG", "DESTINATION");
                startActivityForResult(intent, 0);
            }
        });

        // 시간/날짜 설정
        Button setTime = (Button) findViewById(R.id.setTime);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditAlarm.this, SetTimePicker.class);
                intent.putExtra("BUTTON_FLAG", "TIMER");
                startActivityForResult(intent, 0);
            }
        });

        // 경로추가 버튼정의
        Button addRouteButton = (Button) findViewById(R.id.addRouteButton);
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAlarm.this, RoutePicker.class);
                // 인텐트 추가
                intent.putExtra("mAlarmValues", mAlarmValues);
                intent.putExtra("alramDay", alramDay);
                intent.putExtra("ids", ids);
                startActivity(intent);
            }
        });

        // 경로삭제 버튼정의
        Button deleteRouteButton = (Button) findViewById(R.id.deleteRouteButton);
        deleteRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 알람 제거
                AlarmHandler.alarmHandler.releaseAlarm(EditAlarm.this, "org.daelimie.test.daelimie.TEST");

                // DB에서 제거
                DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);
                dbManager.delete(ids);

                Intent intent = new Intent(EditAlarm.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }
}
