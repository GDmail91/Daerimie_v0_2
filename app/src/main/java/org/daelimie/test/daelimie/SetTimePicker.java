package org.daelimie.test.daelimie;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.ArrayList;

/**
 * Created by YS on 2016-03-21.
 */
public class SetTimePicker extends AppCompatActivity {

    private String TAG = "SetTimePicker";

    private String BUTTON_FLAG;
    private int arrivalTimeHour; // 도착하고 싶은 시간
    private int arrivalTimeMinute; // 도착하고 싶은 분
    private boolean[] alramDay = new boolean[] {false, false, false, false, false, false, false}; // 알림 받을 요일 [0:월, 1:화, 2:수, 3:목, 4:금, 5:토, 6:일]
    private int preAlram = 10; // 출발전 미리알림 시간

    // 뷰
    private LinearLayout setTime;
    private TextView arrivalTimeHourView;
    private TextView arrivalTimeMinuteView;
    private ArrayList<ToggleButton> alramDayView = new ArrayList<>();
    private TextView ampm;
    private Button preAlramButton;
    private Button addAlramButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_picker);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        BUTTON_FLAG = bundle.getString("BUTTON_FLAG");
        arrivalTimeHour = bundle.getInt("hour", 0);
        arrivalTimeMinute = bundle.getInt("minute", 0);

        setTime = (LinearLayout) findViewById(R.id.set_time);
        arrivalTimeHourView = (TextView) findViewById(R.id.arrival_time_hour);
        arrivalTimeMinuteView = (TextView) findViewById(R.id.arrival_time_minute);
        alramDayView.add((ToggleButton) findViewById(R.id.day_1));
        alramDayView.add((ToggleButton) findViewById(R.id.day_2));
        alramDayView.add((ToggleButton) findViewById(R.id.day_3));
        alramDayView.add((ToggleButton) findViewById(R.id.day_4));
        alramDayView.add((ToggleButton) findViewById(R.id.day_5));
        alramDayView.add((ToggleButton) findViewById(R.id.day_6));
        alramDayView.add((ToggleButton) findViewById(R.id.day_7));

        preAlramButton = (Button) findViewById(R.id.pre_alram);
        ampm = (TextView) findViewById(R.id.ampm);

        addAlramButton = (Button) findViewById(R.id.addAlramButton);

        setTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                new TimePickerDialog(SetTimePicker.this, timeSetListener, arrivalTimeHour, arrivalTimeMinute, false).show();
            }
        });

        preAlramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
                        SetTimePicker.this);
                alertBuilder.setTitle("항목중에 하나를 선택하세요.");

                // List Adpater 생성
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        SetTimePicker.this,
                        android.R.layout.select_dialog_singlechoice);
                adapter.add("3 분전");
                adapter.add("5 분전");
                adapter.add("10 분전");
                adapter.add("15 분전");
                adapter.add("20 분전");

                // Adapter 셋팅
                alertBuilder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // 글 가져와 분 선택
                                String strName = adapter.getItem(id);
                                String[] str = strName.split(" ");
                                preAlram = Integer.parseInt(str[0]);
                                preAlramButton.setText(strName);
                            }
                        });
                alertBuilder.show();
            }
        });

        addAlramButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("BUTTON_FLAG", BUTTON_FLAG);
                intent.putExtra("arrivalTimeHour", arrivalTimeHour);
                intent.putExtra("arrivalTimeMinute", arrivalTimeMinute);
                intent.putExtra("alramDay", alramDay);
                intent.putExtra("preAlram", preAlram);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

    }

    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            arrivalTimeHour = hourOfDay;
            arrivalTimeMinute = minute;
            if (hourOfDay > 12) {
                arrivalTimeHourView.setText(String.valueOf(hourOfDay-12));
                ampm.setText("pm");
            } else if (hourOfDay == 0){
                arrivalTimeHourView.setText("12");
                ampm.setText("am");
            } else {
                arrivalTimeHourView.setText(String.valueOf(hourOfDay));
                ampm.setText("am");
            }

            if (minute < 10) {
                arrivalTimeMinuteView.setText("0"+String.valueOf(minute));
            } else {
                arrivalTimeMinuteView.setText(String.valueOf(minute));
            }
        }
    };

    public void togglePicker(View v) {
        ToggleButton temp = (ToggleButton) findViewById(v.getId());
        if (temp.isChecked()) {
            switch (temp.getText().toString()) {
                case "월":
                    alramDay[0] = true;
                    break;
                case "화":
                    alramDay[1] = true;
                    break;
                case "수":
                    alramDay[2] = true;
                    break;
                case "목":
                    alramDay[3] = true;
                    break;
                case "금":
                    alramDay[4] = true;
                    break;
                case "토":
                    alramDay[5] = true;
                    break;
                case "일":
                    alramDay[6] = true;
                    break;
            }
        } else {
            switch (temp.getText().toString()) {
                case "월":
                    alramDay[0] = false;
                    break;
                case "화":
                    alramDay[1] = false;
                    break;
                case "수":
                    alramDay[2] = false;
                    break;
                case "목":
                    alramDay[3] = false;
                    break;
                case "금":
                    alramDay[4] = false;
                    break;
                case "토":
                    alramDay[5] = false;
                    break;
                case "일":
                    alramDay[6] = false;
                    break;
            }
        }
    }
/*

    private void showListDialog(){
        String[] item = getResources().getStringArray(R.array.list_dialog_list_item);

// array를 ArrayList로 변경을 하는 방법
        List<String> listItem = Arrays.asList(item);
        ArrayList<String> itemArrayList = new ArrayList<String> (listItem);
        mDialog = new ListViewDialog(this, getString(R.string.list_dialog_title), itemArrayList);

        mDialog.onOnSetItemClickListener(new ListViewDialogSelectListener(){

            @Override
            public void onSetOnItemClickListener(int position) {
// TODO Auto-generated method stub
                if (position == 0){
                    Log.v(TAG, " 첫번째 인덱스가 선택되었습니다" +
                            "여기에 맞는 작업을 해준다.");
                }
                else if (position == 1){
                    Log.v(TAG, " 두번째 인덱스가 선택되었습니다" +
                            "여기에 맞는 작업을 해준다.");
                }
                else if(position == 2){
                    Log.v(TAG, " 세번째 인덱스가 선택되었습니다" +
                            "여기에 맞는 작업을 해준다.");
                }
                else if(position == 3){
                    Log.v(TAG, " 네번째 인덱스가 선택되었습니다" +
                            "여기에 맞는 작업을 해준다.");
                }
                mDialog.dismiss();
            }
        });

        mDialog.show();
    }
*/

}
