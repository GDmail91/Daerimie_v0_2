package org.daelimie.test.daelimie;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by YS on 2016-03-31.
 */
public class AlarmHandler {
    private String TAG = "AlarmHandler";
    public static AlarmHandler alarmHandler = new AlarmHandler();

    /***************
     * 알람 등록
     * @param context
     * @param mHour
     * @param mMinute
     * @param mSecond
     * @param INTENT_ACTION
     * @return boolean
     ***************/
    protected boolean setAlarm(Context context, int mHour, int mMinute, int mSecond, String INTENT_ACTION){
        try {
            Log.i(TAG, "set time : " + mHour + "/" + mMinute);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent Intent = new Intent(INTENT_ACTION);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, Intent, 0);

            // 기존 등록된 알람은 해제
            alarmManager.cancel(pIntent);
            Calendar settingTime = Calendar.getInstance();
            settingTime.set(Calendar.HOUR_OF_DAY, mHour);
            settingTime.set(Calendar.MINUTE, mMinute);
            settingTime.set(Calendar.SECOND, mSecond);
            // TODO 반복 시간 24시간으로 바꿈
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime.getTimeInMillis(), 3600 * 24 * 1000, pIntent);
            //alarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + second, 3600 * 24 * 1000, pIntent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***************
     * 알람 해제
     * @param context
     * @param INTENT_ACTION
     * @return boolean
     ***************/
    protected boolean releaseAlarm(Context context, String INTENT_ACTION){
        try {
            Log.i(TAG, "release alarm");
            AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

            Intent Intent = new Intent(INTENT_ACTION);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, Intent, 0);
            alarmManager.cancel(pIntent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /***************
     * boolean 배열 컨버터
     * @param convertString
     ***************/
    private boolean[] convertStringToBoolean(String convertString) {
        String[] parts = convertString.split(" ");

        boolean[] array = new boolean[parts.length];
        for (int i = 0; i < parts.length; i++)
            array[i] = Boolean.parseBoolean(parts[i]);

        return array;
    }

}
