
package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
 * Created by YS on 2016-03-09.
 */
public class LocatePicker extends AppCompatActivity {

    private static final String TAG = "LocatePicker";

    TextView top_locate_name;
    TextView top_locate_address;

    static final LatLng SEOUL = new LatLng( 37.56, 126.97);
    private GoogleMap map;
    private Marker marker;
    private Boolean isSetMarker = false;

    // 선택된 지역 정보
    private String BUTTON_FLAG;
    private double selectedLocateLat; // 위도
    private double selectedLocateLng; // 경도
    private String selectedPlaceId; // 지역 정보
    private String selectedName; // 이름

    private SlidingUpPanelLayout mLayout;

    // 구글 플레이스 관련
    private int PLACE_PICKER_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.locate_picker);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BUTTON_FLAG = bundle.getString("BUTTON_FLAG");

        // 툴바 생성
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 구글 맵 생성
        initGoogleMap();


        top_locate_name = (TextView) findViewById(R.id.top_locate_name);
        top_locate_address = (TextView) findViewById(R.id.top_locate_address);
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //openAutocompleteActivity();
            }
        });

        Button addLocateButton = (Button) findViewById(R.id.addLocateButton);
        addLocateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, selectedName+" "+selectedPlaceId);
                Intent intent = new Intent();
                intent.putExtra("BUTTON_FLAG", BUTTON_FLAG);
                intent.putExtra("selectedLocateLat", selectedLocateLat);
                intent.putExtra("selectedLocateLng", selectedLocateLng);
                intent.putExtra("selectedPlaceId", selectedPlaceId);
                intent.putExtra("selectedName", selectedName);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        /**************
         * 리사이클러 뷰
         **************/
        try {
            String testData = "[{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }" +
                    ",{ locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }, { locate_name: '럭키아파트', locate_address: '서울시 금천구 시흥대로 47길' }]";
            JSONArray data = new JSONArray(testData);
            Log.d("JSON: ", data.toString());
            ArrayList<String> tmp_locate_name = new ArrayList<String>();
            ArrayList<String> tmp_locate_address = new ArrayList<String>();

            top_locate_name.setText(data.getJSONObject(0).getString("locate_name"));
            top_locate_address.setText(data.getJSONObject(0).getString("locate_address"));

            for (int i = 1; i < data.length(); i++) {
                // List 어댑터에 전달할 값들
                tmp_locate_name.add(data.getJSONObject(i).getString("locate_name"));
                tmp_locate_address.add(data.getJSONObject(i).getString("locate_address"));
            }

            // 가장 상위(도로) 다음 장소 item 셋팅
            top_locate_name.setText(data.getJSONObject(0).getString("locate_name"));
            top_locate_address.setText(data.getJSONObject(0).getString("locate_address"));

            // ListView 생성하면서 작성할 값 초기화
            LocateAdapter m_ListAdapter = new LocateAdapter(tmp_locate_name, tmp_locate_address);

            // ListView 어댑터 연결
            ListView m_ListView = (ListView) findViewById(R.id.locate_list);
            m_ListView.setAdapter(m_ListAdapter);

            m_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(LocatePicker.this, "onItemClick", Toast.LENGTH_SHORT).show();
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

    // 구글맵 초기화
    private void initGoogleMap() {

        /*******************
         * 구글맵 컴포넌트
         *******************/
        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map))
                .getMap(); // 맵 가져옴

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 15)); // Zoom 단계 설정

        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if (isSetMarker) {
                    marker.remove();
                }
                selectedLocateLat = latLng.latitude;
                selectedLocateLng = latLng.longitude;
                marker = map.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                        .position(latLng));
                isSetMarker = true;

                map.animateCamera(CameraUpdateFactory.newLatLng(latLng));   // 마커생성위치로 이동
                marker.showInfoWindow();
                picker_info(latLng);
            }
        });
    }

    public void picker_info(LatLng latLng) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsCom service = retrofit.create(GoogleMapsCom.class);
        String location = latLng.latitude + "," + latLng.longitude;
        Log.d(TAG, location);

        Call<LinkedHashMap> res = service.nearbySearch(getString(R.string.WEB_API_KEY), location, "50", "ko");

        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String status = responseData.getString("status");
                    if (status.equals("OK")) {
                        JSONArray results = responseData.getJSONArray("results");
                        Log.d(TAG, results.toString());
                        JSONObject firstItem = results.getJSONObject(1); // 가장 상위(도로) 다음 장소 가져옴
                        selectedPlaceId = firstItem.getString("place_id");

                        locateDetail(selectedPlaceId);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            // 받은 데이터 출력
                Log.d(TAG, response.body().toString());

            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });
    }


    // 지역 정보 받아오기
    public void locateDetail(String place_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        GoogleMapsCom service = retrofit.create(GoogleMapsCom.class);

        Call<LinkedHashMap> res = service.detailInfo(getString(R.string.WEB_API_KEY), place_id, "ko");

        res.enqueue(new Callback<LinkedHashMap>() {
            @Override
            public void onResponse(Call<LinkedHashMap> call, Response<LinkedHashMap> response) {
                try {
                    // 받은 데이터
                    Log.d(TAG, response.body().toString());
                    LinkedHashMap temp = response.body();

                    JSONObject responseData = new JSONObject(temp);
                    String status = responseData.getString("status");
                    if (status.equals("OK")) {
                        JSONObject itemInfo = responseData.getJSONObject("result");
                        String name = itemInfo.getString("name");
                        String address = itemInfo.getString("formatted_address");

                        selectedName = name;
                        top_locate_name.setText(name);
                        top_locate_address.setText(address);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // 받은 데이터 출력
                Log.d(TAG, response.body().toString());

            }

            @Override
            public void onFailure(Call<LinkedHashMap> call, Throwable t) {

                // TODO 실패
                Log.d(TAG, t.toString());
                Log.d(TAG, "아예실패");
            }
        });
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

}

