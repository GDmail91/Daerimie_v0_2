package org.daelimie.test.daelimie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by YS on 2016-04-06.
 */
public class SelectableAlarm extends Activity implements
        View.OnClickListener {

    private Button mConfirm, mCancel, mNextRoute;
    private LatLng departureStop;
    private String _TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.selectable_alram_page);

        Intent intent = getIntent();
        Bundle intentBundle = intent.getExtras();
        _TAG = intentBundle.getString("tag");
        String title = intentBundle.getString("title");
        String message = intentBundle.getString("message");
        departureStop = new LatLng(intentBundle.getDouble("departure_stop_lat"), intentBundle.getDouble("departure_stop_lng"));

        TextView titleView = (TextView) findViewById(R.id.title_view);
        TextView messageView = (TextView) findViewById(R.id.message_view);

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
                intent.putExtra("tag", _TAG);
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
