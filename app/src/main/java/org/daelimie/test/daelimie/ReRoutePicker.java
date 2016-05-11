package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YS on 2016-04-06.
 */
public class ReRoutePicker extends RoutePicker {
    private static final String TAG = "ReRoutePicker";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_picker);

        addRouteButton = (Button) findViewById(R.id.addRouteButton);

        // 전달받은 데이터 받음 (출발지, 도착지, 도착시간)
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String alarmTAG = bundle.getString("tag");
        LatLng departureStop = bundle.getParcelable("departureStop");

        DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

        try {
            JSONObject data = dbManager.getData(alarmTAG);
            Log.d(TAG, data.toString());

            mAlarmValue.setDeparture(
                    departureStop,
                    "",
                    "");
            mAlarmValue.setDestination(
                    new LatLng(data.getDouble("destinationLocateLat"), data.getDouble("destinationLocateLng")),
                    data.getString("destinationPlaceId"),
                    data.getString("destinationName"));
            mAlarmValue.setArrivalTime(
                    data.getInt("arrivalTimeHour"),
                    data.getInt("arrivalTimeMinute"));
            mAlarmValue.setDepartureTime(
                    data.getInt("departureTimeHour"),
                    data.getInt("departureTimeMinute"));
            mAlarmValue.setAlarmInfo(
                    data.getInt("preAlarm"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 구글 맵 생성
        initGoogleMap();

        top_route_name = (TextView) findViewById(R.id.top_route_name);
        top_route_address = (TextView) findViewById(R.id.top_route_address);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openAutocompleteActivity();
            }
        });

        // 경로 선택 완료
        addRouteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setMyButtonDisable(addRouteButton);

                Intent intent = new Intent(ReRoutePicker.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                try {
                    JSONArray routes = new JSONArray(routeInfo);
                    JSONArray steps = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
                    long timer = routes.getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getLong("value");

                    // 차량 탑승 알림 등록
                    AlarmHandler.alarmHandler.setInstanceAlarm(ReRoutePicker.this, System.currentTimeMillis() + (timer * 1000), steps.toString(), 0);
                    // DB에 현재 인덱스 저장
                    DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);
                    //dbManager.setIndex(alarm_id, 0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                setMyButtonEnable(addRouteButton);
            }
        });

        /**************
         * 리사이클러 뷰
         **************/
        try {
            // TODO RouteAdapter 생성후 바꿔야함
            String testData = "[{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }]";
            JSONArray data = new JSONArray(testData);
            Log.d("JSON: ", data.toString());
            ArrayList<String> tmp_locate_name = new ArrayList<String>();
            ArrayList<String> tmp_locate_address = new ArrayList<String>();

            top_route_name.setText(data.getJSONObject(0).getString("locate_name"));
            top_route_address.setText(data.getJSONObject(0).getString("locate_address"));

            for (int i = 1; i < data.length(); i++) {
                // List 어댑터에 전달할 값들
                tmp_locate_name.add(data.getJSONObject(i).getString("locate_name"));
                tmp_locate_address.add(data.getJSONObject(i).getString("locate_address"));
            }

            // 가장 상위(도로) 다음 장소 item 셋팅
            top_route_name.setText(data.getJSONObject(0).getString("locate_name"));
            top_route_address.setText(data.getJSONObject(0).getString("locate_address"));

            // ListView 생성하면서 작성할 값 초기화
            LocateAdapter m_ListAdapter = new LocateAdapter(tmp_locate_name, tmp_locate_address, new LocateItemCallback() {
                @Override
                public void itemChange(int position) {

                }

                @Override
                public void markingLocate(LatLng latLng) {

                }
            });

            // ListView 어댑터 연결
            ListView m_ListView = (ListView) findViewById(R.id.route_list);
            m_ListView.setAdapter(m_ListAdapter);

            m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(ReRoutePicker.this, "onItemClick", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        // Sliding panel
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
                if (slideOffset >= 0) {
                    mLayout.bringToFront();
                }
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i(TAG, "onPanelStateChanged " + newState);
                if (newState.toString().equals("EXPANDED")) {
                    ImageView pannel_arrow = (ImageView) findViewById(R.id.pannel_arrow);
                    pannel_arrow.setImageResource(R.drawable.down);
                } else if (newState.toString().equals("COLLAPSED")) {
                    LinearLayout searchLayout = (LinearLayout) findViewById(R.id.search_bar);
                    searchLayout.bringToFront();
                    ImageView pannel_arrow = (ImageView) findViewById(R.id.pannel_arrow);
                    pannel_arrow.setImageResource(R.drawable.up);
                }
            }
        });
        mLayout.setFadeOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

    }

    // 길정보 생성
    @Override
    protected void route_info(String departurePlaceId, String destinationPlaceId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsCom service = retrofit.create(GoogleMapsCom.class);


        Call<LinkedHashMap> res = service.getDirections(
                getString(R.string.WEB_API_KEY),
                departurePlaceId,
                destinationPlaceId,
                "transit",
                "true",
                "ko",
                "fewer_transfers",
                "subway|bus|train",
                String.valueOf(System.currentTimeMillis()/1000)); // 밀리 초는 포함하지 않음

        // 구글 Direction 루트 정보 받아옴
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    Log.d(TAG, responseData.toString());
                    String status = responseData.getString("status");
                    if (status.equals("OK")) {
                        JSONArray routes = responseData.getJSONArray("routes");
                        routeInfo = routes.toString();

                        routeGenerate(routes);
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
}
