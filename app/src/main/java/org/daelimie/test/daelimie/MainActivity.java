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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
