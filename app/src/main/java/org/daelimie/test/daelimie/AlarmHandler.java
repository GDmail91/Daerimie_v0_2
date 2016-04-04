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
     * @param preAlarm
     * @param INTENT_ACTION
     * @return boolean
     ***************/
    protected boolean setAlarm(Context context, int mHour, int mMinute, int preAlarm, String INTENT_ACTION){
        try {
            Log.i(TAG, "set time : " + mHour + "/" + mMinute);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent("org.daelimie.test.daelimie.TEST");
            intent.putExtra("tag", INTENT_ACTION);
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            // preAlarm 시간만큼 빼서 미리 알림 주기
            mMinute -= preAlarm;
            if(mMinute < 0) {
                mHour -= 1;
                mMinute += 60;
                if (mHour < 0) {
                    mHour += 24;
                }
            }
            /*
            if (mMinute >60) {
                mHour += 1;
                mMinute -=60;
                if (mHour >= 24) {
                    mHour -= 24;
                }
            }*/

            // 기존 등록된 알람은 해제
            alarmManager.cancel(pIntent);
            Calendar settingTime = Calendar.getInstance();
            settingTime.set(Calendar.HOUR_OF_DAY, mHour);
            settingTime.set(Calendar.MINUTE, mMinute);
            Log.d(TAG, "알람시간: "+mHour+":"+mMinute);
            // TODO 반복 시간 24시간으로 바꿈
            alarmManager.set(AlarmManager.RTC_WAKEUP, settingTime.getTimeInMillis(), pIntent);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime.getTimeInMillis(), 10 * 1000, pIntent);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, settingTime.getTimeInMillis(), 3600 * 24 * 1000, pIntent);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /***************
     * 인스턴스 알람 등록
     * @param context
     * @param timer
     * @param steps
     * @param step_index
     * @return boolean
     ***************/
    protected boolean setInstanceAlarm(Context context, long timer, String steps, int step_index){
        Log.d(TAG, "steps: "+steps+"\nstep_index: "+step_index);
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent intent = new Intent("org.daelimie.test.daelimie.ALARMING");
            intent.putExtra("step_index", step_index+1);    // 진행중인 단계
            intent.putExtra("steps", steps);    // 진행중인 루트 스텝
            PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

            // 알림 설정
            alarmManager.set(AlarmManager.RTC_WAKEUP, timer, pIntent);

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
