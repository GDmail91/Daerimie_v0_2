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
    private String arrivalTime; // 도착하고 싶은 시간

    // 뷰
    private TextView departureNameView;
    private TextView destinationNameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_alram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        departureNameView = (TextView) findViewById(R.id.departureNameView);
        destinationNameView = (TextView) findViewById(R.id.destinationNameView);

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

        // 경로추가 버튼정의
        Button addRouteButton = (Button) findViewById(R.id.addRouteButton);
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddAlram.this, RoutePicker.class);
                Double depLocateLat = departureLocate.latitude;
                Double depLocateLng = departureLocate.longitude;
                Double desLocateLat = destinationLocate.latitude;
                Double desLocateLng = destinationLocate.longitude;
                intent.putExtra("departureLocateLat", depLocateLat);
                intent.putExtra("departureLocateLng", depLocateLng);
                intent.putExtra("departurePlaceId", departurePlaceId);
                intent.putExtra("departureName", departureName);
                intent.putExtra("destinationLocateLat", desLocateLat);
                intent.putExtra("destinationLocateLng", desLocateLng);
                intent.putExtra("destinationPlaceId", destinationPlaceId);
                intent.putExtra("destinationName", destinationName);
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
            }
        }
    }
}
