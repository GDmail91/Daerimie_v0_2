package org.daelimie.test.daelimie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by YS on 2016-04-06.
 */
public class SelectableAlarm extends Activity implements
        View.OnClickListener {

    private Button mConfirm, mCancel, mNextRoute;
    private LatLng departureStop;
    private int alarm_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selectable_alram_page);

        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        alarm_id = intentBundle.getInt("alarm_id");
        String title = intentBundle.getString("title");
        String message = intentBundle.getString("message");
        String transit = intentBundle.getString("transit");
        //int confDegree = intentBundle.getInt("confDegree");

        departureStop = new LatLng(intentBundle.getDouble("departure_stop_lat"), intentBundle.getDouble("departure_stop_lng"));

        TextView titleView = (TextView) findViewById(R.id.title_view);
        TextView messageView = (TextView) findViewById(R.id.message_view);

        TextView preStop = (TextView) findViewById(R.id.pre_stop);
        TextView prePreStop = (TextView) findViewById(R.id.pre_pre_stop);
        TextView preTime = (TextView) findViewById(R.id.pre_time);
        TextView prePreTime = (TextView) findViewById(R.id.pre_pre_time);
        try {
            switch (transit) {
                case "BUS":
                    JSONObject busInfo = new JSONObject(intentBundle.getString("busInfo"));

                    preTime.setText(busInfo.getInt("predict_time1") + "분");
                    if (!busInfo.isNull("arvlMsg2")) {
                        prePreStop.setText(busInfo.getInt("arvlMsg2"));
                        prePreTime.setText(busInfo.getInt("predict_time2") + "분");
                    }
                    break;
                case "SUBWAY":
                    JSONArray subArrivalInfo = new JSONArray(intentBundle.getString("subwayInfo"));
                    Log.d("ALARMPAGE", intentBundle.getString("subwayInfo"));

                    TextView takeTime = (TextView) findViewById(R.id.takeTime);
                    takeTime.setText("차량위치");
                    JSONObject preSubwayInfo = subArrivalInfo.getJSONObject(0);
                    preStop.setText(preSubwayInfo.getString("bstatnNm") + "행");
                    preTime.setText(preSubwayInfo.getString("arvlMsg2"));

                    JSONObject prePreSubwayInfo;
                    if (subArrivalInfo.length() > 1) {
                        prePreSubwayInfo = subArrivalInfo.getJSONObject(1);
                        prePreStop.setText(prePreSubwayInfo.getString("bstatnNm") + "행");
                        prePreTime.setText(prePreSubwayInfo.getString("arvlMsg2"));
                    } else {
                        prePreStop.setText("전역 정보 없음");
                    }
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (title != null)
            titleView.setText(title);
        if (message != null)
            messageView.setText(message);


        setContent();
    }

    private void setContent() {
        mConfirm = (Button) findViewById(R.id.alram_confirm);
        mNextRoute = (Button) findViewById(R.id.next_route);

        mConfirm.setOnClickListener(this);
        mNextRoute.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alram_confirm:
                this.finish();
                break;
            case R.id.next_route:
                // 기존 알람 해제
                AlarmHandler.alarmHandler.releaseAlarm(this, "org.daelimie.test.daelimie.ALARMING");

                Intent intent = new Intent(SelectableAlarm.this, MainActivity.class);
                intent.putExtra("alarm_id", alarm_id);
                intent.putExtra("departureStop", departureStop);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

                break;
            default:
                break;
        }
    }
}
