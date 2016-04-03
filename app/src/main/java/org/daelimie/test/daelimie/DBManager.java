package org.daelimie.test.daelimie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by YS on 2016-03-31.
 */
public class DBManager extends SQLiteOpenHelper {

    public DBManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 새로운 테이블을 생성한다.
        // create table 테이블명 (컬럼명 타입 옵션);
        db.execSQL("CREATE TABLE AlarmList( " +
                "_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "departureName TEXT, " +
                "departurePlaceId TEXT, " +
                "departureLocateLat REAL, " +
                "departureLocateLng REAL, " +
                "destinationName TEXT, " +
                "destinationPlaceId TEXT, " +
                "destinationLocateLat REAL, " +
                "destinationLocateLng REAL, " +
                "arrivalTimeHour INTEGER, " +
                "arrivalTimeMinute INTEGER, " +
                "departureTimeHour INTEGER, " +
                "departureTimeMinute INTEGER, " +
                "preAlarm INTEGER, " +
                "alarmDay TEXT, " +
                "_tag TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public String insert(DTOAlarmValues mAlarmValues,
                       String alarmDay) {
        SQLiteDatabase dbR = getReadableDatabase();
        int topNumber = 0;

        Cursor cursor = dbR.rawQuery("SELECT _id FROM AlarmList ORDER BY _id DESC LIMIT 1", null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                topNumber = cursor.getInt(0);
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            topNumber = 1;
        }

        String alarmTAG = "org.daelimie.test.daelimie.Alarm" + (topNumber+1);
        String sql = "INSERT INTO AlarmList VALUES(" +
                "'" + (topNumber+1) + "', " +
                "'" + mAlarmValues.getDepartureName() + "', " +
                "'" + mAlarmValues.getDeparturePlaceId() + "', " +
                "'" + mAlarmValues.getDepartureLocate().latitude + "', " +
                "'" + mAlarmValues.getDepartureLocate().longitude + "', " +
                "'" + mAlarmValues.getDestinationName() + "', " +
                "'" + mAlarmValues.getDestinationPlaceId() + "', " +
                "'" + mAlarmValues.getDestinationLocate().latitude + "', " +
                "'" + mAlarmValues.getDestinationLocate().longitude + "', " +
                "'" + mAlarmValues.getArrivalTimeHour() + "', " +
                "'" + mAlarmValues.getArrivalTimeMinute() + "', " +
                "'" + mAlarmValues.getDepartureTimeHour() + "', " +
                "'" + mAlarmValues.getDepartureTimeMinute() + "', " +
                "'" + mAlarmValues.getPreAlram() + "', " +
                "'" + alarmDay + "', " +
                "'" + alarmTAG + "');";
        SQLiteDatabase dbW = getWritableDatabase();
        dbW.execSQL(sql);
        dbW.close();

        return alarmTAG;
    }

    public String update(DTOAlarmValues mAlarmValues,
                       String alarmDay,
                       int ids) {
        String alarmTAG = "org.daelimie.test.daelimie.Alarm" + ids;
        String sql = "UPDATE AlarmList SET " +
                "departureName='" + mAlarmValues.getDepartureName() + "', " +
                "departurePlaceId='" + mAlarmValues.getDeparturePlaceId() + "', " +
                "departureLocateLat='" + mAlarmValues.getDepartureLocate().latitude + "', " +
                "departureLocateLng='" + mAlarmValues.getDepartureLocate().longitude + "', " +
                "destinationName='" + mAlarmValues.getDestinationName() + "', " +
                "destinationPlaceId='" + mAlarmValues.getDestinationPlaceId() + "', " +
                "destinationLocateLat='" + mAlarmValues.getDestinationLocate().latitude + "', " +
                "destinationLocateLng='" + mAlarmValues.getDestinationLocate().longitude + "', " +
                "arrivalTimeHour='" + mAlarmValues.getArrivalTimeHour() + "', " +
                "arrivalTimeMinute='" + mAlarmValues.getArrivalTimeMinute() + "', " +
                "departureTimeHour='" + mAlarmValues.getDepartureTimeHour() + "', " +
                "departureTimeMinute='" + mAlarmValues.getDepartureTimeMinute() + "', " +
                "preAlarm='" + mAlarmValues.getPreAlram() + "', " +
                "alarmDay='" + alarmDay + "'" +
                "WHERE _id='"+ids+"' ;";

        SQLiteDatabase dbW = getWritableDatabase();
        dbW.execSQL(sql);
        dbW.close();

        return alarmTAG;
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(int ids) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM AlarmList WHERE _id='"+ids+"'");
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM AlarmList");
        db.close();
    }

    public int printCountOfData() {
        SQLiteDatabase db = getReadableDatabase();
        int count=0;

        Cursor cursor = db.rawQuery("SELECT * FROM AlarmList ORDER BY _id DESC", null);
        while(cursor.moveToNext()) {
            count += cursor.getInt(0);
        }
        return count;
    }

    public ArrayList<JSONObject> getAllData() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<JSONObject> allData = new ArrayList<>();
        int i =0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM AlarmList ORDER BY _id DESC", null);
            while(cursor.moveToNext()) {
                JSONObject tempData = new JSONObject();
                tempData.put("_id", cursor.getInt(0));
                tempData.put("departureName", cursor.getString(1));
                tempData.put("departurePlaceId", cursor.getString(2));
                tempData.put("departureLocateLat", cursor.getDouble(3));
                tempData.put("departureLocateLng", cursor.getDouble(4));
                tempData.put("destinationName", cursor.getString(5));
                tempData.put("destinationPlaceId", cursor.getString(6));
                tempData.put("destinationLocateLat", cursor.getDouble(7));
                tempData.put("destinationLocateLng", cursor.getDouble(8));
                tempData.put("arrivalTimeHour", cursor.getInt(9));
                tempData.put("arrivalTimeMinute", cursor.getInt(10));
                tempData.put("departureTimeHour", cursor.getInt(11));
                tempData.put("departureTimeMinute", cursor.getInt(12));
                tempData.put("preAlarm", cursor.getInt(13));
                tempData.put("alarmDay", cursor.getString(14));
                tempData.put("_tag", cursor.getString(15));

                allData.add(i++, tempData);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return allData;
    }

    public JSONObject getData(int id) {
        SQLiteDatabase db = getReadableDatabase();
        JSONObject data = new JSONObject();
        int i =0;
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM AlarmList WHERE _id='"+id+"' ORDER BY _id DESC", null);
            while(cursor.moveToNext()) {
                JSONObject tempData = new JSONObject();
                tempData.put("_id", cursor.getInt(0));
                tempData.put("departureName", cursor.getString(1));
                tempData.put("departurePlaceId", cursor.getString(2));
                tempData.put("departureLocateLat", cursor.getDouble(3));
                tempData.put("departureLocateLng", cursor.getDouble(4));
                tempData.put("destinationName", cursor.getString(5));
                tempData.put("destinationPlaceId", cursor.getString(6));
                tempData.put("destinationLocateLat", cursor.getDouble(7));
                tempData.put("destinationLocateLng", cursor.getDouble(8));
                tempData.put("arrivalTimeHour", cursor.getInt(9));
                tempData.put("arrivalTimeMinute", cursor.getInt(10));
                tempData.put("departureTimeHour", cursor.getInt(11));
                tempData.put("departureTimeMinute", cursor.getInt(12));
                tempData.put("preAlarm", cursor.getInt(13));
                tempData.put("alarmDay", cursor.getString(14));
                tempData.put("_tag", cursor.getString(15));

                data = tempData;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return data;
    }
}
