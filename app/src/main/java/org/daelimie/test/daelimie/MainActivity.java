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
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ListView m_ListView;    // 알림 리스트 띄우기 위한 뷰
    private AlramAdapter m_ListAdapter;    // 알림 리스트 가져올 어댑터


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_alram_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 클릭키를 추가할 버튼정의
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

        getButtonTitle();
    }

    // 버튼 목록 얻어오는 함수
    private void getButtonTitle() {
        try {
            String testData = "[{ departure: 럭키아파트, destination: 남서울대학교 }, { departure: 럭키아파트, destination: 남서울대학교 }, { departure: 럭키아파트, destination: 남서울대학교 }]";
            JSONArray data = new JSONArray(testData);
Log.d("JSON: ", data.toString());
            ArrayList<String> tmp_List = new ArrayList<String>();
            ArrayList<String> tmp_Mac = new ArrayList<String>();
            ArrayList<Integer> tmp_Fid = new ArrayList<Integer>();

            for (int i = 0; i < data.length(); i++) {
                // List 어댑터에 전달할 값들
                tmp_List.add(data.getJSONObject(i).getString("departure"));
                tmp_Mac.add(data.getJSONObject(i).getString("destination"));
            }

            // ListView 생성하면서 작성할 값 초기화
            m_ListAdapter = new AlramAdapter(tmp_List, tmp_Mac);



            // ListView 어댑터 연결
            m_ListView = (ListView) findViewById(R.id.myRouteList);
            m_ListView.setAdapter(m_ListAdapter);
            //m_ListView.setOnItemClickListener(onClickListItem);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
