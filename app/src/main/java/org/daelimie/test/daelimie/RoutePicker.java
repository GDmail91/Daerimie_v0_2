package org.daelimie.test.daelimie;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by YS on 2016-03-19.
 */
public class RoutePicker extends AppCompatActivity {

    private static final String TAG = "RoutePicker";

    ListView m_ListView;
    TextView top_route_name;
    TextView top_route_address;

    static final LatLng SEOUL = new LatLng(37.56, 126.97);
    protected GoogleMap map;
    protected Marker marker;
    protected Boolean isSetMarker = false;

    // 선택된 경로 정보
    protected DTOAlarmValues mAlarmValue;
    protected boolean[] alramDay; // 알림 받을 요일
    protected int ids = 0;
    protected String routeInfo;
    protected int selectedRouteIndex = 0;

    protected SlidingUpPanelLayout mLayout;
    protected Button addRouteButton;
    protected Button preButton;
    protected Button nextButton;
    protected TextView startTime;

    // 구글 플레이스 관련
    protected int PLACE_PICKER_REQUEST = 1;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    protected GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.route_picker);

        addRouteButton = (Button) findViewById(R.id.addRouteButton);

        // 전달받은 데이터 받음 (출발지, 도착지, 도착시간)
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        mAlarmValue = (DTOAlarmValues) bundle.getSerializable("mAlarmValues");
        alramDay = bundle.getBooleanArray("alramDay");
        ids = bundle.getInt("ids");
        int ACTION_FLAG = bundle.getInt("ACTION_FLAG");

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 구글 맵 생성
        initGoogleMap();

        m_ListView = (ListView) findViewById(R.id.route_list);
        startTime = (TextView) findViewById(R.id.start_time);
        preButton = (Button) findViewById(R.id.pre_button);
        // 첫번째 부터 시작하기 때문에 이전버튼 비활성화
        preButton.setEnabled(false);
        nextButton = (Button) findViewById(R.id.next_button);

        top_route_name = (TextView) findViewById(R.id.top_route_name);
        top_route_address = (TextView) findViewById(R.id.top_route_address);

        switch (ACTION_FLAG) {
            /** 경로를 직접 찾는 경우 */
            case 1:
                // 길 정보 생성
                route_info(mAlarmValue.getDeparturePlaceId(), mAlarmValue.getDestinationPlaceId());

                // 이전 루트 보기 버튼
                preButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedRouteIndex--;
                        map.clear();
                        try {
                            JSONArray routes = new JSONArray(routeInfo);
                            routeGenerate(routes);

                            // 인덱스가 0 보다 작을경우 비활성화
                            if (selectedRouteIndex <= 0)
                                preButton.setEnabled(false);
                            // 클릭했을경우 다음 버튼이 비활성화인 경우 다시 활성화
                            if (!nextButton.isEnabled())
                                nextButton.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 다음 루트 보기 버튼
                nextButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedRouteIndex++;
                        map.clear();
                        try {
                            JSONArray routes = new JSONArray(routeInfo);
                            routeGenerate(routes);

                            // 인덱스가 최대값보다 클경우 비활성화
                            if (selectedRouteIndex >= routes.length()-1)
                                nextButton.setEnabled(false);
                            // 클릭했을경우 이전전버튼이 비활성화인 경우 다시 활성화
                            if (!preButton.isEnabled())
                                preButton.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                // 경로 선택 완료
                addRouteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        setMyButtonDisable(addRouteButton);

                        // DB에 저장
                        final DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);
                        Log.d(TAG, convertBooleanToString(alramDay));
                        boolean[] test = convertStringToBoolean(convertBooleanToString(alramDay));

                        int alarm_id;
                        try {
                            JSONArray tempRoute = new JSONArray(routeInfo);
                            routeInfo = new JSONArray().put(tempRoute.getJSONObject(selectedRouteIndex)).toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (ids != 0) {
                            Log.d(TAG, "루트 변경");
                            alarm_id = dbManager.update(mAlarmValue, convertBooleanToString(alramDay), routeInfo, ids);
                        } else {
                            Log.d(TAG, "새로저장");
                            alarm_id = dbManager.insert(mAlarmValue, convertBooleanToString(alramDay), routeInfo);
                        }
                        Log.d(TAG, String.valueOf(dbManager.printCountOfData()));


                        Intent intent = new Intent(RoutePicker.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        // 메인 알람 설정
                        AlarmHandler.alarmHandler.setAlarm(RoutePicker.this, mAlarmValue.getDepartureTimeHour(), mAlarmValue.getDepartureTimeMinute(), mAlarmValue.getPreAlram(), alarm_id);

                        setMyButtonEnable(addRouteButton);
                    }
                });

                break;
            /** 경로보기만 하는 경우 */
            case 2:
                preButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                addRouteButton.setText("확인");
                addRouteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(RoutePicker.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                });
                DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

                JSONArray routes = dbManager.getRoute(ids);
                Log.d(TAG, "routes: " + routes.toString());

                routeGenerate(routes);
                break;
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


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    // 구글맵 초기화
    protected void initGoogleMap() {

        /*******************
         * 구글맵 컴포넌트
         *******************/
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.route_map))
                .getMap(); // 맵 가져옴

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(mAlarmValue.getDepartureLocate(), 15)); // Zoom 단계 설정
        String depPlace = "";
        String desPlace = "";

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                // 마커 선택시 해당 마커 정보 하단에도 출력
                top_route_name.setText(marker.getTitle());
                top_route_address.setText(marker.getSnippet());
                return false;
            }
        });
    }

    // 길정보 생성
    protected void route_info(String departurePlaceId, String destinationPlaceId) {
        Log.d(TAG, departurePlaceId + " / " + destinationPlaceId);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsCom service = retrofit.create(GoogleMapsCom.class);

        Calendar settingTime = Calendar.getInstance();
        settingTime.set(Calendar.HOUR_OF_DAY, mAlarmValue.getArrivalTimeHour());
        settingTime.set(Calendar.MINUTE, mAlarmValue.getArrivalTimeMinute());
        Log.d("Time Test", "" + settingTime.getTimeInMillis() / 1000);

        // 출발지/도착지를 매개변수중 원하는 것 골라서 집어넣음 (Overriding 가능)
        Call<LinkedHashMap> res = service.getDirections(
                getString(R.string.WEB_API_KEY),
                departurePlaceId,
                destinationPlaceId,
                "transit",
                "true",
                "ko",
                "fewer_transfers",
                "subway|bus|train",
                String.valueOf(settingTime.getTimeInMillis()/1000)); // 밀리 초는 포함하지 않음

        // 구글 Direction 루트 정보 받아옴
        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
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

    // 도보에 길생성
    protected void routeGenerate(JSONArray routes) {
        try {
            JSONArray eachRoutesSteps = routes.getJSONObject(selectedRouteIndex).getJSONArray("legs").getJSONObject(0).getJSONArray("steps");
Log.d(TAG, "eachRoutesSteps: "+eachRoutesSteps.toString());
            // 출발 시간 저장
            long getTime = routes.getJSONObject(selectedRouteIndex).getJSONArray("legs").getJSONObject(0).getJSONObject("departure_time").getLong("value") * 1000; // 초는 포함되지 않기 때문에 1000 곱함
            Date depDate = new Date(getTime); // 출발 시간

            mAlarmValue.setDepartureTime(depDate.getHours(), depDate.getMinutes());

            String timeText = "출발 시간: ";
            // 시간 셋팅
            if (mAlarmValue.getDepartureTimeHour() > 12) {
                timeText += "오후 "+(mAlarmValue.getDepartureTimeHour()-12);
            } else if (mAlarmValue.getArrivalTimeHour() == 0){
                timeText += "오전 "+12;
            } else {
                timeText += "오전 "+mAlarmValue.getDepartureTimeHour();
            }

            // 분 셋팅
            timeText += "시 "+mAlarmValue.getDepartureTimeMinute()+"분";

            // 시간 출력
            startTime.setText(timeText);

            LatLng firstLocation = new LatLng(eachRoutesSteps.getJSONObject(0).getJSONObject("start_location").getDouble("lat"), eachRoutesSteps.getJSONObject(0).getJSONObject("start_location").getDouble("lng"));
            map.animateCamera(CameraUpdateFactory.newLatLng(firstLocation));   // 마커생성위치로 이동
            top_route_name.setText(eachRoutesSteps.getJSONObject(0).getString("html_instructions"));
            top_route_address.setText("출발");

            // 지도에 그릴 Polyline
            PolylineOptions polylineOptions = new PolylineOptions();
            JSONArray tempRoute = new JSONArray();
            for (int i = 0; i < eachRoutesSteps.length(); i++) {
                try {
                    JSONObject tempStep = new JSONObject();

                    // 교통편 확인
                    switch (eachRoutesSteps.getJSONObject(i).getString("travel_mode")) {
                        case "WALKING": // 걸어갈 경우
                            // 도보 길안내 (Google Map 은 길안내가 자세히 안나와있으므로 T Map 이용)
                            JSONArray walkStep = eachRoutesSteps.getJSONObject(i).getJSONArray("steps");
                            tempStep.put("route_des", eachRoutesSteps.getJSONObject(i).getString("html_instructions"));
                            tempStep.put("route_type", "도보");
                            tempStep.put("route_duration", eachRoutesSteps.getJSONObject(i).getJSONObject("duration").getString("text"));

                            for (int j = 0; j < walkStep.length(); j++) {
                                List<LatLng> poly = PolyUtil.decode(walkStep.getJSONObject(j).getJSONObject("polyline").getString("points"));

                                // T Map 에서 도보 길찾기 정보 가져옴
                                // TODO 길 모양 이쁘게 나오도록 수정은 했으나 블루라인과 레드라인이 안어올림
                                TMapRoute.mTMapRoute.searchRoute(
                                        getString(R.string.T_API_KEY),
                                        "도보로 걷기", // 시작위치 이름
                                        poly.get(0),
                                        eachRoutesSteps.getJSONObject(i).getString("html_instructions"), // 목적지 위치 이름
                                        poly.get(1),
                                        new MyCallback() { // Data 콜백
                                            @Override
                                            public void httpProcessing(JSONObject result) {
                                                PolylineOptions walkPolylineOptions = new PolylineOptions();
                                                markingWalkingMap(result, walkPolylineOptions); // 지도에 마크하기

                                                map.addPolyline(walkPolylineOptions);
                                            }
                                        });
                            }
                            break;
                        case "TRANSIT": // 교통수단 탈 경우
                            JSONObject detailTransit = eachRoutesSteps.getJSONObject(i).getJSONObject("transit_details");
                            tempStep.put("route_des", eachRoutesSteps.getJSONObject(i).getString("html_instructions"));
                            tempStep.put("route_type", detailTransit.getJSONObject("line").getJSONObject("vehicle").getString("name"));
                            tempStep.put("route_duration", eachRoutesSteps.getJSONObject(i).getJSONObject("duration").getString("text"));

                            markingTransitMap(eachRoutesSteps.getJSONObject(i), polylineOptions); // 지도에 마크하기

                            switch (detailTransit.getJSONObject("line").getJSONObject("vehicle").getString("type")) {
                                case "BUS": // 버스인 경우
                                    // TODO 버스
                                    break;
                                case "SUBWAY": // 지하철인 경우
                                    // TODO 지하철
                                    break;
                            }
                            break;
                    }

                    tempRoute.put(i, tempStep);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setRouteList(tempRoute.toString());

            Polyline polyline = map.addPolyline(polylineOptions);
        } catch(JSONException e) {
            e.printStackTrace();
        }
    }


    // 검색된 루트 리스트 출력
    private void setRouteList(String searchData) {
        /**************
         * 리사이클러 뷰
         **************/
        try {
            JSONArray data = new JSONArray(searchData);
            Log.d("JSON: ", data.toString());
            ArrayList<String> tmp_route_type = new ArrayList<String>();
            ArrayList<String> tmp_route_des = new ArrayList<String>();
            ArrayList<String> tmp_route_duration = new ArrayList<String>();

            for (int i = 0; i < data.length(); i++) {
                // List 어댑터에 전달할 값들
                tmp_route_type.add(data.getJSONObject(i).getString("route_type"));
                tmp_route_des.add(data.getJSONObject(i).getString("route_des"));
                tmp_route_duration.add(data.getJSONObject(i).getString("route_duration"));
            }

            // ListView 생성하면서 작성할 값 초기화
            RouteAdapter m_RouteAdapter = new RouteAdapter(tmp_route_type, tmp_route_des, tmp_route_duration, new LocateItemCallback() {
                @Override
                public void itemChange(int position) {

                }
                @Override
                public void markingLocate(LatLng latLng) {
                    marker = map.addMarker(new MarkerOptions()
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                            .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                            .position(latLng));
                    isSetMarker = true;

                    map.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
                    marker.showInfoWindow();

                }
            });

            // ListView 어댑터 연결

            m_ListView.setAdapter(m_RouteAdapter);

            m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(RoutePicker.this, "onItemClick", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 지도에 마크하기
    protected void markingWalkingMap(JSONObject result, PolylineOptions polylineOptions) {
        try {
            JSONObject tmapData = result;
            JSONArray features = tmapData.getJSONArray("features");

            for (int k = 0; k < features.length(); k++) {
                int index = features.getJSONObject(k).getJSONObject("properties").getInt("index");
                JSONObject geometry = features.getJSONObject(k).getJSONObject("geometry");
                JSONObject properties = features.getJSONObject(k).getJSONObject("properties");
                switch (geometry.getString("type")) {
                    case "Point":
                        // TODO 마커찍기
                        JSONArray markLocation = geometry.getJSONArray("coordinates");
                        LatLng mark = new LatLng(markLocation.getDouble(1), markLocation.getDouble(0)); // 위도 경도가 반대로 옴
                        try {
                            if (k == 0) {
                                marker = map.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                        .anchor(0.5f, 0.8f) // Anchors the marker on the bottom left
                                        .position(mark)
                                        .title(URLDecoder.decode(properties.getString("description"), "UTF-8"))
                                        .snippet(URLDecoder.decode("출발", "UTF-8")));
                            } else {
                                marker = map.addMarker(new MarkerOptions()
                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                        .anchor(0.5f, 0.8f) // Anchors the marker on the bottom left
                                        .position(mark)
                                        .title(URLDecoder.decode(properties.getString("name"), "UTF-8"))
                                        .snippet(URLDecoder.decode(properties.getString("description"), "UTF-8")));
                            }
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        isSetMarker = true;

                        break;
                    case "LineString":
                        // TODO 라인그리기
                        ArrayList<LatLng> walkingPoly = new ArrayList<LatLng>();
                        JSONArray polyLocation = geometry.getJSONArray("coordinates");
                        for (int l=0; l<polyLocation.length(); l++) {
                            double walkLat = polyLocation.getJSONArray(l).getDouble(1);
                            double walkLng = polyLocation.getJSONArray(l).getDouble(0);
                            walkingPoly.add(new LatLng(walkLat, walkLng));
                        }

                        // 지도에 그릴 Polyline 에 추가
                        for (int l=0; l<walkingPoly.size(); l++) {
                            polylineOptions.add(walkingPoly.get(l))
                                    .width(25)
                                    .color(Color.RED);
                        }
                        break;
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // 지도에 마크하기 대중교통 길
    protected void markingTransitMap(JSONObject result, PolylineOptions polylineOptions) {
        try {
            // 교통수단 길안내
            List<LatLng> tranPoly = PolyUtil.decode(result.getJSONObject("polyline").getString("points"));
            for (int j = 0; j < tranPoly.size(); j++) {
                polylineOptions.add(tranPoly.get(j))
                        .width(25)
                        .color(Color.BLUE);
            }
            // 교통수단 시작위치 표시
            marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .anchor(0.5f, 0.8f) // Anchors the marker on the bottom left
                    .position(new LatLng(result.getJSONObject("start_location").getDouble("lat"), result.getJSONObject("start_location").getDouble("lng")))
                    .title(URLDecoder.decode(result.getString("html_instructions"), "UTF-8"))
                    .snippet(URLDecoder.decode(result.getJSONObject("transit_details").getJSONObject("departure_stop").getString("name"), "UTF-8")));

            // 교통수단 도착위치 표시
            marker = map.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                    .anchor(0.5f, 0.8f) // Anchors the marker on the bottom left
                    .position(new LatLng(result.getJSONObject("end_location").getDouble("lat"), result.getJSONObject("end_location").getDouble("lng")))
                    .title(URLDecoder.decode(result.getString("html_instructions") + "하차", "UTF-8"))
                    .snippet(URLDecoder.decode(result.getJSONObject("transit_details").getJSONObject("arrival_stop").getString("name"), "UTF-8")));

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /******************
     * 구글플레이스 컴포넌트
     ******************/
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RoutePicker Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.daelimie.test.daelimie/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "RoutePicker Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://org.daelimie.test.daelimie/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    /***************
     * boolean 배열 컨버터
     * @param convertString
     * @return boolean[]
     ***************/
    protected boolean[] convertStringToBoolean(String convertString) {
        String[] parts = convertString.split(" ");

        boolean[] array = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++)
            array[i] = Boolean.parseBoolean(parts[i]);

        return array;
    }

    /***************
     * boolean 배열 컨버터
     * @param boolArray
     * @return boolean[]
     ***************/
    protected String convertBooleanToString(boolean[] boolArray) {
        String convertedString= "";
        for (int i = 0;i<boolArray.length; i++) {
            convertedString = convertedString + boolArray[i];
            // Do not append comma at the end of last element
            if(i<boolArray.length - 1){
                convertedString = convertedString+" ";
            }}
        return convertedString;
    }

    protected void setMyButtonEnable(Button button) {
        button.setEnabled(true);
    }

    protected void setMyButtonDisable(Button button) {
        button.setEnabled(false);
    }
}
