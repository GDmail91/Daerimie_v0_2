package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    private LatLng departureLocate; // 출발지 위도,경도
    private String departurePlaceId; // 출발지 지역 정보
    private String departureName; // 출발지 이름
    private LatLng destinationLocate; //도착지 위도, 경도
    private String destinationPlaceId; //도착지 지역 정보
    private String destinationName; // 도착지 이름
    private int arrivalTimeHour; // 도착하고 싶은 시간
    private int arrivalTimeMinute; // 도착하고 싶은 분
    private boolean[] alramDay; // 알림 받을 요일
    private int preAlram; // 출발전 미리알림 시간

    // 뷰
    private TextView departureNameView;
    private TextView destinationNameView;
    private TextView arrivalTimeHourView;
    private TextView arrivalTimeMinuteView;
    private TextView ampm;

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
                Intent intent = new Intent(AddAlram.this, RoutePicker.class);
                Double depLocateLat = departureLocate.latitude;     // 출발지 위도
                Double depLocateLng = departureLocate.longitude;    // 출발지 경도
                Double desLocateLat = destinationLocate.latitude;   // 도착지 위도
                Double desLocateLng = destinationLocate.longitude;  // 도착지 경도
                // 인텐트 추가
                intent.putExtra("departureLocateLat", depLocateLat);
                intent.putExtra("departureLocateLng", depLocateLng);
                intent.putExtra("departurePlaceId", departurePlaceId);  // 출발지 plcae_id
                intent.putExtra("departureName", departureName);        // 출발지 이름
                intent.putExtra("destinationLocateLat", desLocateLat);
                intent.putExtra("destinationLocateLng", desLocateLng);
                intent.putExtra("destinationPlaceId", destinationPlaceId);  // 도착지 plcae_id
                intent.putExtra("destinationName", destinationName);        // 도착지 이름
                intent.putExtra("arrivalTimeHour", arrivalTimeHour);        // 도착 시간
                intent.putExtra("arrivalTimeMinute", arrivalTimeMinute);    // 도착 분
                intent.putExtra("alramDay", alramDay);      // 알람 요일
                intent.putExtra("preAlram", preAlram);      // 미리 알림 시간
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if(resultCode == RESULT_OK) {
            Log.d(TAG, ""+resultCode);
            switch (intent.getStringExtra("BUTTON_FLAG")) {
                case "DEPARTURE":
                    departureLocate = new LatLng(intent.getDoubleExtra("selectedLocateLat", 0.0), intent.getDoubleExtra("selectedLocateLng", 0.0));
                    departurePlaceId = intent.getStringExtra("selectedPlaceId");
                    departureName = intent.getStringExtra("selectedName");
                    departureNameView.setText(departureName);
                    break;

                case "DESTINATION":
                    destinationLocate = new LatLng(intent.getDoubleExtra("selectedLocateLat", 0.0), intent.getDoubleExtra("selectedLocateLng", 0.0));
                    destinationPlaceId = intent.getStringExtra("selectedPlaceId");
                    destinationName = intent.getStringExtra("selectedName");
                    destinationNameView.setText(destinationName);
                    break;

                case "TIMER":
                    // TODO 알람 시간 설정 후
                    arrivalTimeHour = intent.getIntExtra("arrivalTimeHour", 0);
                    arrivalTimeMinute = intent.getIntExtra("arrivalTimeMinute", 0);
                    alramDay = intent.getBooleanArrayExtra("alramDay");
                    preAlram = intent.getIntExtra("preAlram", 0);

                    Log.d(TAG, "" + arrivalTimeHour + "" + arrivalTimeMinute);
                    Log.d(TAG, alramDay.toString());
                    Log.d(TAG, ""+preAlram);

                    if (arrivalTimeHour > 12) {
                        arrivalTimeHourView.setText(String.valueOf(arrivalTimeHour-12));
                        ampm.setText("pm");
                    } else if (arrivalTimeHour == 0){
                        arrivalTimeHourView.setText("12");
                        ampm.setText("am");
                    } else {
                        arrivalTimeHourView.setText(String.valueOf(arrivalTimeHour));
                        ampm.setText("am");
                    }

                    if (arrivalTimeMinute < 10) {
                        arrivalTimeMinuteView.setText("0"+String.valueOf(arrivalTimeMinute));
                    } else {
                        arrivalTimeMinuteView.setText(String.valueOf(arrivalTimeMinute));
                    }
                    break;
            }
        }
    }
}
