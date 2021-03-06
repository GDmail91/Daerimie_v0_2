package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by YS on 2016-03-09.
 */
public class AddAlram extends AppCompatActivity {

    private static final String TAG = "AddAlram";

    // 선택된 지역 정보
    protected DTOAlarmValues mAlarmValues = new DTOAlarmValues();
    protected boolean[] alramDay = new boolean[] {false, false, false, false, false, false, false}; // 알림 받을 요일 [0:월, 1:화, 2:수, 3:목, 4:금, 5:토, 6:일]

    // 뷰
    protected TextView departureNameView;
    protected TextView destinationNameView;
    protected TextView arrivalTimeHourView;
    protected TextView arrivalTimeMinuteView;
    protected TextView ampm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        departureNameView = (TextView) findViewById(R.id.departureNameView);
        destinationNameView = (TextView) findViewById(R.id.destinationNameView);
        arrivalTimeHourView = (TextView) findViewById(R.id.arrival_time_hour);
        arrivalTimeMinuteView = (TextView) findViewById(R.id.arrival_time_minute);
        ampm = (TextView) findViewById(R.id.ampm);

        // 출발지 버튼정의
        Button setDeparture = (Button) findViewById(R.id.setDeparture);
        setDeparture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAlram.this, LocatePicker.class);
                intent.putExtra("BUTTON_FLAG", "DEPARTURE");
                startActivityForResult(intent, 0);
            }
        });

        // 도착지 버튼정의
        Button setDestination = (Button) findViewById(R.id.setDestination);
        setDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAlram.this, LocatePicker.class);
                intent.putExtra("BUTTON_FLAG", "DESTINATION");
                startActivityForResult(intent, 0);
            }
        });

        // 시간/날짜 설정
        Button setTime = (Button) findViewById(R.id.setTime);
        setTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddAlram.this, SetTimePicker.class);
                intent.putExtra("BUTTON_FLAG", "TIMER");
                startActivityForResult(intent, 0);
            }
        });

        // 경로추가 버튼정의
        Button addRouteButton = (Button) findViewById(R.id.addRouteButton);
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 값을 다 넣었을 경우만 실행
                if (mAlarmValues.getDepartureName() != null
                || mAlarmValues.getDestinationName() != null){
                    Intent intent = new Intent(AddAlram.this, RoutePicker.class);
                    // 인텐트 추가
                    intent.putExtra("mAlarmValues", mAlarmValues);
                    intent.putExtra("alramDay", alramDay);
                    intent.putExtra("ACTION_FLAG", 1);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            Log.d(TAG, ""+resultCode);
            switch (intent.getStringExtra("BUTTON_FLAG")) {
                case "DEPARTURE":
                    mAlarmValues.setDeparture(
                            new LatLng(intent.getDoubleExtra("selectedLocateLat", 0.0), intent.getDoubleExtra("selectedLocateLng", 0.0)),
                            intent.getStringExtra("selectedPlaceId"),
                            intent.getStringExtra("selectedName"));
                    departureNameView.setText(Html.fromHtml(mAlarmValues.getDepartureName()));
                    break;

                case "DESTINATION":
                    mAlarmValues.setDestination(
                            new LatLng(intent.getDoubleExtra("selectedLocateLat", 0.0), intent.getDoubleExtra("selectedLocateLng", 0.0)),
                            intent.getStringExtra("selectedPlaceId"),
                            intent.getStringExtra("selectedName"));
                    destinationNameView.setText(Html.fromHtml(mAlarmValues.getDestinationName()));
                    break;

                case "TIMER":
                    mAlarmValues.setArrivalTime(
                            intent.getIntExtra("arrivalTimeHour", 0),
                            intent.getIntExtra("arrivalTimeMinute", 0));
                    mAlarmValues.setAlarmInfo(
                            intent.getIntExtra("preAlram", 10));
                    alramDay = intent.getBooleanArrayExtra("alramDay");

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
                    break;
            }
        }
    }
}
