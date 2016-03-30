package org.daelimie.test.daelimie;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by YS on 2016-03-28.
 */
public class AlramReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        String name = intent.getAction();
Log.i("RECIEVER", "알람 리시브");
        if(name.equals("org.daelimie.test.daelimie.TEST")){
            // TODO 알람 팜업창 띄우기
            // TODO 매너모드일경우 진동 울리기
            // TODO 매너모드 아닐경우 소리울리기
            // TODO 화면 꺼져있을 경우 화면 키기
            Toast.makeText
                    (context, "정상적으로 값을 받았습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
