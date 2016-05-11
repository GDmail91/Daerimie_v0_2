package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
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

    ListView m_ListView;
    TextView top_locate_name;
    TextView top_locate_address;

    static final LatLng SEOUL = new LatLng(37.56, 126.97);
    private GoogleMap map;
    private Marker marker;
    private Boolean isSetMarker = false;

    // 검색 쿼리 정보
    EditText searchForm;

    // 선택된 지역 정보
    private String BUTTON_FLAG;
    private double selectedLocateLat; // 위도
    private double selectedLocateLng; // 경도
    private String selectedPlaceId; // 지역 정보
    private String selectedName; // 이름
    private String selectedAddress; // 주소

    private ArrayList<DTOSearchItem> searchList = new ArrayList<>();

    private SlidingUpPanelLayout mLayout;

    // 구글 플레이스 관련
    private int PLACE_PICKER_REQUEST = 1;

    // 네이버 지역 검색 관련
    private XmlPullParser xpp;
    String data;


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

        m_ListView = (ListView) findViewById(R.id.locate_list);
        top_locate_name = (TextView) findViewById(R.id.top_locate_name);
        top_locate_address = (TextView) findViewById(R.id.top_locate_address);
        final ImageButton searchButton = (ImageButton) findViewById(R.id.search_button);
        searchForm = (EditText) findViewById(R.id.search_form);

        searchForm.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchForm.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE
                || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    searchButton.callOnClick();
                    return true;
                }
                return false;
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    final String query = searchForm.getText().toString();
                    Log.d(TAG, "검색문자열: " + query);
                    Log.d(TAG, "변환: " + URLEncoder.encode(query, "UTF-8").toString());

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                // TODO Auto-generated method stub
                                data = getXmlData(getString(R.string.NAVER_CLIENT_ID), getString(R.string.NAVER_CLIENT_KEY), URLEncoder.encode(query, "UTF-8").toString()); //아래 메소드를 호출하여 XML data를 파싱해서 String 객체로 얻어오기
                                Log.d(TAG, "반환된 값: " + data);

                                if (searchList.size() != 0) {

                                    selectedLocateLat = searchList.get(0).getMapx();
                                    selectedLocateLng = searchList.get(0).getMapy();
                                    if (searchList.get(0).getPlaceId() != null)
                                        selectedPlaceId = "place_id:"+searchList.get(0).getPlaceId();
                                    else
                                        selectedPlaceId = searchList.get(0).getMapx() + " " + searchList.get(0).getMapy();
                                    selectedName = searchList.get(0).getTitle();
                                    selectedAddress = searchList.get(0).getAddress();

                                    Log.d(TAG, "선택된 지역: " + selectedName + ", " + selectedLocateLat + ", " + selectedLocateLng);

                                    //UI Thread(Main Thread)를 제외한 어떤 Thread도 화면을 변경할 수 없기때문에
                                    //runOnUiThread()를 이용하여 UI Thread가 TextView 글씨 변경하도록 함
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            // TODO Auto-generated method stub
                                            try {
                                                JSONArray tempArray = new JSONArray();
                                                for (int i = 0; i < searchList.size(); i++) {
                                                    JSONObject tempObject = new JSONObject();
                                                    tempObject.put("locate_name", searchList.get(i).getTitle());
                                                    tempObject.put("locate_address", searchList.get(i).getAddress());
                                                    tempObject.put("locate_mapx", searchList.get(i).getMapx());
                                                    tempObject.put("locate_mapy", searchList.get(i).getMapy());
                                                    tempArray.put(tempObject);

                                                    Log.d(TAG, "들어있는 " + i + "번째 아이템: " + searchList.get(i).getAddress());
                                                }
                                                setSearchList(tempArray.toString());
                                                // 마킹 위치 바꿈
                                                LatLng latLng = new LatLng(selectedLocateLat, selectedLocateLng);
                                                marker = map.addMarker(new MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                                        .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                                        .position(latLng));
                                                isSetMarker = true;

                                                map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(selectedLocateLat, selectedLocateLng)));   // 마커생성위치로 이동
                                                marker.showInfoWindow();

                                            } catch(JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                } else {
                                    top_locate_name.setText("검색 결과를 찾을 수 없습니다.");
                                    top_locate_address.setText("다시 검색해 주세요.");
                                }
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();


                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
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

        String testData = "[{ locate_name: '검색 또는 지도를 클릭해 주세요', locate_address: '검색이 필요합니다.' }]";
        setSearchList(testData);


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
                        Log.d(TAG, "results 길이: "+ results.length());

                        // 기존 검색결과 제거
                        searchList.clear();
                        // 새 검색결과 삽입
                        if (results.length() == 1) {
                            searchList.add(new DTOSearchItem(
                                    results.getJSONObject(0).getString("name"),
                                    results.getJSONObject(0).getString("vicinity"),
                                    results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                    results.getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng"),
                                    results.getJSONObject(0).getString("place_id")));
                        } else {
                            for (int i = 1; i < results.length(); i++) {
                                searchList.add(new DTOSearchItem(
                                        results.getJSONObject(i).getString("name"),
                                        results.getJSONObject(i).getString("vicinity"),
                                        results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
                                        results.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"),
                                        results.getJSONObject(i).getString("place_id")));
                            }
                        }

                        //JSONObject firstItem = results.getJSONObject(1); // 가장 상위는 도로정보 이므로 다음 장소 가져옴
                        //selectedPlaceId = firstItem.getString("place_id");

                        //locateDetail(selectedPlaceId);

                        JSONArray tempArray = new JSONArray();
                        for (int i=0; i<searchList.size(); i++) {
                            JSONObject tempObject = new JSONObject();
                            tempObject.put("locate_name", searchList.get(i).getTitle());
                            tempObject.put("locate_address", searchList.get(i).getAddress());
                            tempObject.put("place_id", searchList.get(i).getPlaceId());
                            tempObject.put("locate_mapx", searchList.get(i).getMapx());
                            tempObject.put("locate_mapy", searchList.get(i).getMapy());
                            tempArray.put(tempObject);

                            Log.d(TAG, "들어있는 " + i + "번째 아이템: " + searchList.get(i).getAddress());
                        }
                        setSearchList(tempArray.toString());

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
                Log.d(TAG, "각 검색된 지역 정보: "+response.body().toString());

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
    //XmlPullParser를 이용하여 Naver 에서 제공하는 OpenAPI XML 파일 파싱하기(parsing)
    String getXmlData(String clientId, String secretKey, String encodedQuery){

        StringBuffer buffer=new StringBuffer();

        String queryUrl="https://openapi.naver.com/v1/search/local.xml?"   //요청 URL
                +"&query="+encodedQuery         //지역검색 요청값
                +"&display=10"                  //검색 출력 건수  10~100
                +"&start=1"                     //검색 시작 위치  1~1000
                +"&sort=vote";

        try {
            URL url= new URL(queryUrl); //문자열로 된 요청 url을 URL 객체로 생성.
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", secretKey);
            con.connect();
            InputStream is= con.getInputStream();  //url위치로 입력스트림 연결

            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xpp= factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));  //inputstream 으로부터 xml 입력받기

            /*BufferedReader rd = new BufferedReader(new InputStreamReader(is,"UTF-8"));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                Log.d(TAG, "응답 메세지: "+line);
                response.append(line);
                response.append('\r');
            }
            rd.close();*/

            int x=0,y=0;
            String title = "없음";
            String address = "없음";
            String tag;

            // 기존 검색된 데이터 제거
            searchList.clear();
            xpp.next();
            int eventType= xpp.getEventType();
            int i =0;
            while( eventType != XmlPullParser.END_DOCUMENT ){
                switch( eventType ){
                    case XmlPullParser.START_DOCUMENT:
                        buffer.append("start NAVER XML parsing...\n\n");
                        break;

                    case XmlPullParser.START_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기

                        if(tag.equals("item")) ;// 첫번째 검색결과
                        else if(tag.equals("title")){
                            buffer.append("업소명 : ");
                            xpp.next();
                            title = xpp.getText().toString();
                            buffer.append(xpp.getText()); //title 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("category")){
                            buffer.append("업종 : ");
                            xpp.next();
                            buffer.append(xpp.getText()); //category 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("description")){
                            buffer.append("세부설명 :");
                            xpp.next();
                            buffer.append(xpp.getText()); //description 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("telephone")){
                            buffer.append("연락처 :");
                            xpp.next();
                            buffer.append(xpp.getText()); //telephone 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("address")){
                            buffer.append("주소 :");
                            xpp.next();
                            address = xpp.getText().toString();
                            buffer.append(xpp.getText()); //address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("roadAddress")){
                            buffer.append("도로명주소 :");
                            xpp.next();
                            buffer.append(xpp.getText()); //address 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("mapx")){
                            buffer.append("지도 위치 X :");
                            xpp.next();
                            x = Integer.parseInt(xpp.getText().toString());
                            buffer.append(xpp.getText()); //mapx 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("  ,  ");          //줄바꿈 문자 추가
                        }
                        else if(tag.equals("mapy")){
                            buffer.append("지도 위치 Y :");
                            xpp.next();
                            y = Integer.parseInt(xpp.getText().toString());
                            buffer.append(xpp.getText()); //mapy 요소의 TEXT 읽어와서 문자열버퍼에 추가
                            buffer.append("\n");          //줄바꿈 문자 추가
                        }
                        break;

                    case XmlPullParser.TEXT:
                        break;

                    case XmlPullParser.END_TAG:
                        tag= xpp.getName();    //테그 이름 얻어오기

                        if(tag.equals("item")) {

                            GeoPoint in_pt = new GeoPoint(x,y);
                            GeoPoint out_pt = GeoTrans.convert(GeoTrans.KATEC, GeoTrans.GEO, in_pt);

                            searchList.add(i++, new DTOSearchItem(title, address, out_pt.getY(), out_pt.getX(), null));

                            buffer.append("\n"); // 첫번째 검색결과종료..줄바꿈
                        }

                        break;
                }
                eventType= xpp.next();
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        buffer.append("end NAVER XML parsing...\n");

        return buffer.toString(); //StringBuffer 문자열 객체 반환

    }

    private void setSearchList(String searchData) {
        /**************
         * 리사이클러 뷰
         **************/
        try {
            JSONArray data = new JSONArray(searchData);
            Log.d("JSON: ", data.toString());
            ArrayList<String> tmp_locate_name = new ArrayList<String>();
            ArrayList<String> tmp_locate_address = new ArrayList<String>();

            top_locate_name.setText(Html.fromHtml(data.getJSONObject(0).getString("locate_name")));
            top_locate_address.setText(Html.fromHtml(data.getJSONObject(0).getString("locate_address")));

            // 마킹 위치 바꿈
            selectedLocateLat = searchList.get(0).getMapx();
            selectedLocateLng = searchList.get(0).getMapy();
            if (searchList.get(0).getPlaceId() != null)
                selectedPlaceId = "place_id:"+searchList.get(0).getPlaceId();
            else
                selectedPlaceId = searchList.get(0).getMapx() + " " + searchList.get(0).getMapy();
            selectedName = searchList.get(0).getTitle();
            selectedAddress = searchList.get(0).getAddress();

            for (int i = 1; i < data.length(); i++) {
                // List 어댑터에 전달할 값들
                tmp_locate_name.add(data.getJSONObject(i).getString("locate_name"));
                tmp_locate_address.add(data.getJSONObject(i).getString("locate_address"));
            }

            // ListView 생성하면서 작성할 값 초기화
            LocateAdapter m_ListAdapter = new LocateAdapter(tmp_locate_name, tmp_locate_address, new LocateItemCallback() {
                @Override
                public void itemChange(int position) {
                    DTOSearchItem temp = searchList.remove(position+1);
                    searchList.add(0, temp);

                    try {
                        JSONArray tempArray = new JSONArray();
                        for (int i=0; i<searchList.size(); i++) {
                            JSONObject tempObject = new JSONObject();
                            tempObject.put("locate_name", searchList.get(i).getTitle());
                            tempObject.put("locate_address", searchList.get(i).getAddress());
                            tempObject.put("place_id", searchList.get(i).getPlaceId());
                            tempObject.put("locate_mapx", searchList.get(i).getMapx());
                            tempObject.put("locate_mapy", searchList.get(i).getMapy());
                            tempArray.put(tempObject);

                            Log.d(TAG, "들어있는 " + i + "번째 아이템: " + searchList.get(i).getAddress());
                        }
                        setSearchList(tempArray.toString());

                        // 마킹 위치 바꿈
                        selectedLocateLat = searchList.get(0).getMapx();
                        selectedLocateLng = searchList.get(0).getMapy();
                        if (searchList.get(0).getPlaceId() != null)
                            selectedPlaceId = "place_id:"+searchList.get(0).getPlaceId();
                        else
                            selectedPlaceId = searchList.get(0).getMapx() + " " + searchList.get(0).getMapy();
                        selectedName = searchList.get(0).getTitle();
                        selectedAddress = searchList.get(0).getAddress();

                        LatLng latLng = new LatLng(selectedLocateLat, selectedLocateLng);
                        marker = map.addMarker(new MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker))
                                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                                .position(latLng));
                        isSetMarker = true;

                        map.animateCamera(CameraUpdateFactory.newLatLng(new LatLng(selectedLocateLat, selectedLocateLng)));   // 마커생성위치로 이동
                        marker.showInfoWindow();
                    } catch(JSONException e) {
                        e.printStackTrace();
                    }
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
    }
}

