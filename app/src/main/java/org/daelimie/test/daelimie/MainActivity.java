package org.daelimie.test.daelimie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private ListView m_ListView;    // 알림 리스트 띄우기 위한 뷰
    private AlramAdapter m_ListAdapter;    // 알림 리스트 가져올 어댑터
    private ArrayList<Integer> _ids = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alram_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 경로 추가 버튼 정의
        Button addBtn = (Button) findViewById(R.id.addRouteButton);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO 경로 추가 버튼
                Intent intent = new Intent(MainActivity.this, AddAlram.class);
                startActivity(intent);
            }
        });

    }


    @Override
    protected void onResume() {
        super.onResume();

        getAlarmTitle();
    }

    // 알림 목록 얻어오는 함수
    private void getAlarmTitle() {
        try {
            DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

            ArrayList<JSONObject> allData = dbManager.getAllData();
            JSONArray data = new JSONArray();

            ArrayList<String> tmp_dep = new ArrayList<String>();
            ArrayList<String> tmp_des = new ArrayList<String>();
            ArrayList<String> tmp_depTime = new ArrayList<String>();
            ArrayList<String> tmp_desTime = new ArrayList<String>();

            if (allData.size() != 0) {
                for (int i = 0; i < allData.size(); i++) {
                    data.put(allData.get(i));
                }

                Log.d("JSON: ", data.toString());

                for (int i = 0; i < data.length(); i++) {
                    // List 어댑터에 전달할 값들
                    tmp_dep.add(data.getJSONObject(i).getString("departureName"));
                    tmp_des.add(data.getJSONObject(i).getString("destinationName"));

                    String tempDepMin, tempDesMin;
                    // 출발 분 표시 정리
                    if (Integer.valueOf(data.getJSONObject(i).getString("departureTimeMinute")) < 10) {
                        tempDepMin = "0"+data.getJSONObject(i).getString("departureTimeMinute");
                    } else {
                        tempDepMin = data.getJSONObject(i).getString("departureTimeMinute");
                    }
                    // 도착 분 표시 정리
                    if (Integer.valueOf(data.getJSONObject(i).getString("arrivalTimeMinute")) < 10) {
                        tempDesMin = "0"+data.getJSONObject(i).getString("arrivalTimeMinute");
                    } else {
                        tempDesMin = data.getJSONObject(i).getString("arrivalTimeMinute");
                    }
                    tmp_depTime.add(data.getJSONObject(i).getString("departureTimeHour") + ":" + tempDepMin);
                    tmp_desTime.add(data.getJSONObject(i).getString("arrivalTimeHour") + ":" + tempDesMin);
                    _ids.add(data.getJSONObject(i).getInt("_id"));

                }
                // ListView 생성하면서 작성할 값 초기화
                m_ListAdapter = new AlramAdapter(_ids, tmp_dep, tmp_des, tmp_depTime, tmp_desTime);

                // ListView 어댑터 연결
                m_ListView = (ListView) findViewById(R.id.myRouteList);
                m_ListView.setAdapter(m_ListAdapter);
            } else {
                TextView noitem = (TextView) findViewById(R.id.noitem);
                noitem.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 아이템 터치 이벤트
    private AdapterView.OnItemClickListener onClickListItem = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // 이벤트 발생 시 해당 아이템 위치의 텍스트를 출력
            Toast.makeText(getApplicationContext(), m_ListAdapter.getItem(position) + "\n 여기서 버튼 상세화면으로 넘어갑니다", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.all_delete) {
            DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

            dbManager.deleteAll();

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.test_subway_data) {
            DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

            DTOAlarmValues testData = new DTOAlarmValues();
            testData.setDeparture(
                    new LatLng(37.5563053, 126.953596),
                    "ChIJgXJySHuifDUR6N3TstwKijY",
                    "테스트 출발지"
            );
            testData.setDestination(
                    new LatLng(37.5100699, 126.9638987),
                    "ChIJ9Zn8GmCffDURKZct_JBNJDk",
                    "테스트 목적지"
            );
            Date curtime = new Date(System.currentTimeMillis());
            testData.setDepartureTime(curtime.getHours(), curtime.getMinutes() + 1);
            testData.setArrivalTime(curtime.getHours(), curtime.getMinutes() + 2);
            testData.setAlarmInfo(1);
            String testSubwayRoute = "[\n" +
                    "    {\n" +
                    "      \"bounds\": {\n" +
                    "        \"northeast\": {\n" +
                    "          \"lat\": 37.5563053,\n" +
                    "          \"lng\": 126.972253\n" +
                    "        },\n" +
                    "        \"southwest\": {\n" +
                    "          \"lat\": 37.5100699,\n" +
                    "          \"lng\": 126.94167\n" +
                    "        }\n" +
                    "      },\n" +
                    "      \"copyrights\": \"지도 데이터 ©2016 SK planet\",\n" +
                    "      \"legs\": [\n" +
                    "        {\n" +
                    "          \"arrival_time\": {\n" +
                    "            \"text\": \"오후 3:57\",\n" +
                    "            \"time_zone\": \"Asia/Seoul\",\n" +
                    "            \"value\": 1459925876\n" +
                    "          },\n" +
                    "          \"departure_time\": {\n" +
                    "            \"text\": \"오후 3:20\",\n" +
                    "            \"time_zone\": \"Asia/Seoul\",\n" +
                    "            \"value\": 1459923632\n" +
                    "          },\n" +
                    "          \"distance\": {\n" +
                    "            \"text\": \"7.7 km\",\n" +
                    "            \"value\": 7724\n" +
                    "          },\n" +
                    "          \"duration\": {\n" +
                    "            \"text\": \"37분\",\n" +
                    "            \"value\": 60\n" +
                    "          },\n" +
                    "          \"end_address\": \"대한민국 서울특별시 동작구 본동 노량진로26길 16-40 서울본동초등학교\",\n" +
                    "          \"end_location\": {\n" +
                    "            \"lat\": 37.5100699,\n" +
                    "            \"lng\": 126.953596\n" +
                    "          },\n" +
                    "          \"start_address\": \"대한민국 서울특별시 중구 만리동2가 손기정로 73 서울봉래초등학교\",\n" +
                    "          \"start_location\": {\n" +
                    "            \"lat\": 37.5563053,\n" +
                    "            \"lng\": 126.9638987\n" +
                    "          },\n" +
                    "          \"steps\": [\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.7 km\",\n" +
                    "                \"value\": 737\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"12분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.555911,\n" +
                    "                \"lng\": 126.972253\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"서울역까지 도보\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"}efdFks|eWnAes@\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.5563053,\n" +
                    "                \"lng\": 126.9638987\n" +
                    "              },\n" +
                    "              \"steps\": [\n" +
                    "                {\n" +
                    "                  \"distance\": {\n" +
                    "                    \"text\": \"0.7 km\",\n" +
                    "                    \"value\": 737\n" +
                    "                  },\n" +
                    "                  \"duration\": {\n" +
                    "                    \"text\": \"12분\",\n" +
                    "                    \"value\": 60\n" +
                    "                  },\n" +
                    "                  \"end_location\": {\n" +
                    "                    \"lat\": 37.555911,\n" +
                    "                    \"lng\": 126.972253\n" +
                    "                  },\n" +
                    "                  \"polyline\": {\n" +
                    "                    \"points\": \"}efdFks|eWnAes@\"\n" +
                    "                  },\n" +
                    "                  \"start_location\": {\n" +
                    "                    \"lat\": 37.5563053,\n" +
                    "                    \"lng\": 126.9638987\n" +
                    "                  },\n" +
                    "                  \"travel_mode\": \"WALKING\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"travel_mode\": \"WALKING\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"5.7 km\",\n" +
                    "                \"value\": 5714\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"9분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.514072,\n" +
                    "                \"lng\": 126.94167\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"지하철 신창(순천향대)역행\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"mcfdFqg~eWt~AvE|aA~f@zaBjoC\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.555911,\n" +
                    "                \"lng\": 126.972253\n" +
                    "              },\n" +
                    "              \"transit_details\": {\n" +
                    "                \"arrival_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.514072,\n" +
                    "                    \"lng\": 126.94167\n" +
                    "                  },\n" +
                    "                  \"name\": \"노량진역\"\n" +
                    "                },\n" +
                    "                \"arrival_time\": {\n" +
                    "                  \"text\": \"오후 3:41\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459924860\n" +
                    "                },\n" +
                    "                \"departure_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.555911,\n" +
                    "                    \"lng\": 126.972253\n" +
                    "                  },\n" +
                    "                  \"name\": \"서울역\"\n" +
                    "                },\n" +
                    "                \"departure_time\": {\n" +
                    "                  \"text\": \"오후 3:32\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459924350\n" +
                    "                },\n" +
                    "                \"headsign\": \"신창(순천향대)역\",\n" +
                    "                \"line\": {\n" +
                    "                  \"agencies\": [\n" +
                    "                    {\n" +
                    "                      \"name\": \"서울메트로\",\n" +
                    "                      \"url\": \"http://www.odsay.com/Subway/LineInfo.asp?CID=1000&LMenu=2/\"\n" +
                    "                    }\n" +
                    "                  ],\n" +
                    "                  \"color\": \"#536dfe\",\n" +
                    "                  \"name\": \"서울지하철\",\n" +
                    "                  \"short_name\": \"1호선\",\n" +
                    "                  \"text_color\": \"#ffffff\",\n" +
                    "                  \"vehicle\": {\n" +
                    "                    \"icon\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/subway.png\",\n" +
                    "                    \"name\": \"지하철\",\n" +
                    "                    \"type\": \"SUBWAY\"\n" +
                    "                  }\n" +
                    "                },\n" +
                    "                \"num_stops\": 3\n" +
                    "              },\n" +
                    "              \"travel_mode\": \"TRANSIT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.2 km\",\n" +
                    "                \"value\": 202\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"3분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.513748,\n" +
                    "                \"lng\": 126.943929\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"노량진역까지 도보\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"}}}cFmhxeW~@cM\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.514072,\n" +
                    "                \"lng\": 126.94167\n" +
                    "              },\n" +
                    "              \"steps\": [\n" +
                    "                {\n" +
                    "                  \"distance\": {\n" +
                    "                    \"text\": \"0.2 km\",\n" +
                    "                    \"value\": 202\n" +
                    "                  },\n" +
                    "                  \"duration\": {\n" +
                    "                    \"text\": \"3분\",\n" +
                    "                    \"value\": 60\n" +
                    "                  },\n" +
                    "                  \"end_location\": {\n" +
                    "                    \"lat\": 37.513748,\n" +
                    "                    \"lng\": 126.943929\n" +
                    "                  },\n" +
                    "                  \"polyline\": {\n" +
                    "                    \"points\": \"}}}cFmhxeW~@cM\"\n" +
                    "                  },\n" +
                    "                  \"start_location\": {\n" +
                    "                    \"lat\": 37.514072,\n" +
                    "                    \"lng\": 126.94167\n" +
                    "                  },\n" +
                    "                  \"travel_mode\": \"WALKING\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"travel_mode\": \"WALKING\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.8 km\",\n" +
                    "                \"value\": 789\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"3분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.51251000000001,\n" +
                    "                \"lng\": 126.952701\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"버스 노들역행\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"}{}cFqvxeW@sDN_ChDmWTmBHeEXiG@yA\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.513748,\n" +
                    "                \"lng\": 126.943929\n" +
                    "              },\n" +
                    "              \"transit_details\": {\n" +
                    "                \"arrival_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.51251000000001,\n" +
                    "                    \"lng\": 126.952701\n" +
                    "                  },\n" +
                    "                  \"name\": \"노들역5번출구앞\"\n" +
                    "                },\n" +
                    "                \"arrival_time\": {\n" +
                    "                  \"text\": \"오후 3:53\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459925593\n" +
                    "                },\n" +
                    "                \"departure_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.513748,\n" +
                    "                    \"lng\": 126.943929\n" +
                    "                  },\n" +
                    "                  \"name\": \"노량진역\"\n" +
                    "                },\n" +
                    "                \"departure_time\": {\n" +
                    "                  \"text\": \"오후 3:49\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459925392\n" +
                    "                },\n" +
                    "                \"headsign\": \"노들역\",\n" +
                    "                \"headway\": 300,\n" +
                    "                \"line\": {\n" +
                    "                  \"agencies\": [\n" +
                    "                    {\n" +
                    "                      \"name\": \"서울특별시버스운송사업조합\",\n" +
                    "                      \"url\": \"http://www.odsay.com/Bus/Seoul_Main.asp?CID=1000&LMenu=1\"\n" +
                    "                    }\n" +
                    "                  ],\n" +
                    "                  \"color\": \"#0abb0c\",\n" +
                    "                  \"name\": \"서울 마을버스\",\n" +
                    "                  \"short_name\": \"동작03\",\n" +
                    "                  \"text_color\": \"#ffffff\",\n" +
                    "                  \"vehicle\": {\n" +
                    "                    \"icon\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/bus.png\",\n" +
                    "                    \"name\": \"버스\",\n" +
                    "                    \"type\": \"BUS\"\n" +
                    "                  }\n" +
                    "                },\n" +
                    "                \"num_stops\": 2\n" +
                    "              },\n" +
                    "              \"travel_mode\": \"TRANSIT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.3 km\",\n" +
                    "                \"value\": 282\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"5분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.5100699,\n" +
                    "                \"lng\": 126.953596\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"대한민국 서울특별시 동작구 본동 노량진로26길 16-40 서울본동초등학교까지 도보\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"et}cFkmzeWfNsD\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.51251000000001,\n" +
                    "                \"lng\": 126.952701\n" +
                    "              },\n" +
                    "              \"steps\": [\n" +
                    "                {\n" +
                    "                  \"distance\": {\n" +
                    "                    \"text\": \"0.3 km\",\n" +
                    "                    \"value\": 282\n" +
                    "                  },\n" +
                    "                  \"duration\": {\n" +
                    "                    \"text\": \"5분\",\n" +
                    "                    \"value\": 60\n" +
                    "                  },\n" +
                    "                  \"end_location\": {\n" +
                    "                    \"lat\": 37.5100699,\n" +
                    "                    \"lng\": 126.953596\n" +
                    "                  },\n" +
                    "                  \"polyline\": {\n" +
                    "                    \"points\": \"et}cFkmzeWfNsD\"\n" +
                    "                  },\n" +
                    "                  \"start_location\": {\n" +
                    "                    \"lat\": 37.51251000000001,\n" +
                    "                    \"lng\": 126.952701\n" +
                    "                  },\n" +
                    "                  \"travel_mode\": \"WALKING\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"travel_mode\": \"WALKING\"\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"via_waypoint\": []\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"overview_polyline\": {\n" +
                    "        \"points\": \"}efdFks|eWnAes@t~AvE|aA~f@zaBjoC~@cM@sDN_ChDmWTmBHeEXiG@yAfNsD\"\n" +
                    "      },\n" +
                    "      \"summary\": \"\",\n" +
                    "      \"warnings\": [\n" +
                    "        \"도보 길찾기는 베타 서비스입니다. 주의 – 이 경로에는 인도 또는 보행 경로가 누락되었을 수도 있습니다.\"\n" +
                    "      ],\n" +
                    "      \"waypoint_order\": []\n" +
                    "    }\n" +
                    "  ]";
            int alarm_id = dbManager.insert(testData, "[true,true,true,true,true,true,true]", testSubwayRoute);
            Log.d("TEST", "alarm_id: " + alarm_id);
            // 메인 알람 설정
            AlarmHandler.alarmHandler.setAlarm(MainActivity.this, testData.getDepartureTimeHour(), testData.getDepartureTimeMinute(), testData.getPreAlram(), alarm_id);

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            return true;
        } else if (id == R.id.test_data) {
            DBManager dbManager = new DBManager(getApplicationContext(), "Alarm.db", null, 1);

            DTOAlarmValues testData = new DTOAlarmValues();
            testData.setDeparture(
                    new LatLng(37.5563053, 126.953596),
                    "ChIJgXJySHuifDUR6N3TstwKijY",
                    "테스트 출발지"
            );
            testData.setDestination(
                    new LatLng(37.5100699, 126.9638987),
                    "ChIJ9Zn8GmCffDURKZct_JBNJDk",
                    "테스트 목적지"
            );
            Date curtime = new Date(System.currentTimeMillis());
            testData.setDepartureTime(curtime.getHours(), curtime.getMinutes() + 1);
            testData.setArrivalTime(curtime.getHours(), curtime.getMinutes() + 2);
            testData.setAlarmInfo(1);
            String testRoute = "[\n" +
                    "    {\n" +
                    "      \"legs\": [\n" +
                    "        {\n" +
                    "          \"arrival_time\": {\n" +
                    "            \"text\": \"오후 4:01\",\n" +
                    "            \"value\": 1459926099\n" +
                    "          },\n" +
                    "          \"departure_time\": {\n" +
                    "            \"text\": \"오후 3:18\",\n" +
                    "            \"value\": 1459923534\n" +
                    "          },\n" +
                    "          \"duration\": {\n" +
                    "            \"text\": \"43분\",\n" +
                    "            \"value\": 60\n" +
                    "          },\n" +
                    "          \"end_location\": {\n" +
                    "            \"lat\": 37.5100699,\n" +
                    "            \"lng\": 126.953596\n" +
                    "          },\n" +
                    "          \"start_address\": \"대한민국 서울특별시 중구 만리동2가 손기정로 73 서울봉래초등학교\",\n" +
                    "          \"start_location\": {\n" +
                    "            \"lat\": 37.5563053,\n" +
                    "            \"lng\": 126.9638987\n" +
                    "          },\n" +
                    "          \"steps\": [\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.8 km\",\n" +
                    "                \"value\": 765\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"13분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.555439,\n" +
                    "                \"lng\": 126.972518\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"서울역버스환승센터 까지 도보\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"}efdFks|eWlD{t@\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.5563053,\n" +
                    "                \"lng\": 126.9638987\n" +
                    "              },\n" +
                    "              \"steps\": [\n" +
                    "                {\n" +
                    "                  \"distance\": {\n" +
                    "                    \"text\": \"0.8 km\",\n" +
                    "                    \"value\": 765\n" +
                    "                  },\n" +
                    "                  \"duration\": {\n" +
                    "                    \"text\": \"13분\",\n" +
                    "                    \"value\": 60\n" +
                    "                  },\n" +
                    "                  \"end_location\": {\n" +
                    "                    \"lat\": 37.555439,\n" +
                    "                    \"lng\": 126.972518\n" +
                    "                  },\n" +
                    "                  \"polyline\": {\n" +
                    "                    \"points\": \"}efdFks|eWlD{t@\"\n" +
                    "                  },\n" +
                    "                  \"start_location\": {\n" +
                    "                    \"lat\": 37.5563053,\n" +
                    "                    \"lng\": 126.9638987\n" +
                    "                  },\n" +
                    "                  \"travel_mode\": \"WALKING\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"travel_mode\": \"WALKING\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"5.5 km\",\n" +
                    "                \"value\": 5451\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"19분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.511824,\n" +
                    "                \"lng\": 126.953593\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"버스 서울대행\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"o`fdFgi~eWzBE~CYbDPfJnA|Df@bAB\\\\HjBTpAJ|TQnBOrEw@v\\\\{F`O}B`AMj@J`@TfC|ArQzPxWnVnDhD`QlPrAfAvCvCrA`AbDlBrCzAxNdHnDpBnFlDlLrHxBrAbAt@^h@bBvDb@~@x@p@f@\\\\pAj@\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.555439,\n" +
                    "                \"lng\": 126.972518\n" +
                    "              },\n" +
                    "              \"transit_details\": {\n" +
                    "                \"arrival_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.511824,\n" +
                    "                    \"lng\": 126.953593\n" +
                    "                  },\n" +
                    "                  \"name\": \"상도터널노량진동\"\n" +
                    "                },\n" +
                    "                \"arrival_time\": {\n" +
                    "                  \"text\": \"오후 3:58\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459925903\n" +
                    "                },\n" +
                    "                \"departure_stop\": {\n" +
                    "                  \"location\": {\n" +
                    "                    \"lat\": 37.555439,\n" +
                    "                    \"lng\": 126.972518\n" +
                    "                  },\n" +
                    "                  \"name\": \"서울역버스환승센터\"\n" +
                    "                },\n" +
                    "                \"departure_time\": {\n" +
                    "                  \"text\": \"오후 3:39\",\n" +
                    "                  \"time_zone\": \"Asia/Seoul\",\n" +
                    "                  \"value\": 1459924787\n" +
                    "                },\n" +
                    "                \"headsign\": \"서울대\",\n" +
                    "                \"headway\": 480,\n" +
                    "                \"line\": {\n" +
                    "                  \"agencies\": [\n" +
                    "                    {\n" +
                    "                      \"name\": \"서울특별시버스운송사업조합\",\n" +
                    "                      \"url\": \"http://www.odsay.com/Bus/Seoul_Main.asp?CID=1000&LMenu=1\"\n" +
                    "                    }\n" +
                    "                  ],\n" +
                    "                  \"color\": \"#304ffe\",\n" +
                    "                  \"name\": \"서울 간선버스\",\n" +
                    "                  \"short_name\": \"501\",\n" +
                    "                  \"text_color\": \"#ffffff\",\n" +
                    "                  \"vehicle\": {\n" +
                    "                    \"icon\": \"//maps.gstatic.com/mapfiles/transit/iw2/6/bus.png\",\n" +
                    "                    \"name\": \"버스\",\n" +
                    "                    \"type\": \"BUS\"\n" +
                    "                  }\n" +
                    "                },\n" +
                    "                \"num_stops\": 8\n" +
                    "              },\n" +
                    "              \"travel_mode\": \"TRANSIT\"\n" +
                    "            },\n" +
                    "            {\n" +
                    "              \"distance\": {\n" +
                    "                \"text\": \"0.2 km\",\n" +
                    "                \"value\": 195\n" +
                    "              },\n" +
                    "              \"duration\": {\n" +
                    "                \"text\": \"3분\",\n" +
                    "                \"value\": 60\n" +
                    "              },\n" +
                    "              \"end_location\": {\n" +
                    "                \"lat\": 37.5100699,\n" +
                    "                \"lng\": 126.953596\n" +
                    "              },\n" +
                    "              \"html_instructions\": \"대한민국 서울특별시 동작구 본동 노량진로26길 16-40 서울본동초등학교까지 도보\",\n" +
                    "              \"polyline\": {\n" +
                    "                \"points\": \"{o}cF}rzeW|IA\"\n" +
                    "              },\n" +
                    "              \"start_location\": {\n" +
                    "                \"lat\": 37.511824,\n" +
                    "                \"lng\": 126.953593\n" +
                    "              },\n" +
                    "              \"steps\": [\n" +
                    "                {\n" +
                    "                  \"distance\": {\n" +
                    "                    \"text\": \"0.2 km\",\n" +
                    "                    \"value\": 195\n" +
                    "                  },\n" +
                    "                  \"duration\": {\n" +
                    "                    \"text\": \"3분\",\n" +
                    "                    \"value\": 60\n" +
                    "                  },\n" +
                    "                  \"end_location\": {\n" +
                    "                    \"lat\": 37.5100699,\n" +
                    "                    \"lng\": 126.953596\n" +
                    "                  },\n" +
                    "                  \"polyline\": {\n" +
                    "                    \"points\": \"{o}cF}rzeW|IA\"\n" +
                    "                  },\n" +
                    "                  \"start_location\": {\n" +
                    "                    \"lat\": 37.511824,\n" +
                    "                    \"lng\": 126.953593\n" +
                    "                  },\n" +
                    "                  \"travel_mode\": \"WALKING\"\n" +
                    "                }\n" +
                    "              ],\n" +
                    "              \"travel_mode\": \"WALKING\"\n" +
                    "            }\n" +
                    "          ],\n" +
                    "          \"via_waypoint\": []\n" +
                    "        }\n" +
                    "      ],\n" +
                    "      \"overview_polyline\": {\n" +
                    "        \"points\": \"}efdFks|eWlD{t@zBE~CYbDPdPvBbAB\\\\H|D`@|TQnBOjc@sHbQkCj@J`@TfC|ArQzPh]x[`QlPrAfAvCvCrA`AbDlBrCzAxNdHnDpB|S`NxBrAbAt@^h@bBvDb@~@x@p@f@\\\\pAj@|IA\"\n" +
                    "      }\n" +
                    "    }\n" +
                    "  ]";
            int alarm_id = dbManager.insert(testData, "[true,true,true,true,true,true,true]", testRoute);
            Log.d("TEST", "alarm_id: " + alarm_id);
            // 메인 알람 설정
            AlarmHandler.alarmHandler.setAlarm(MainActivity.this, testData.getDepartureTimeHour(), testData.getDepartureTimeMinute(), testData.getPreAlram(), alarm_id);

            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
