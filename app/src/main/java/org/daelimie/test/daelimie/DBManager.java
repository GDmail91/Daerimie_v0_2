package org.daelimie.test.daelimie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
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

        db.execSQL("CREATE TABLE RouteInfo (" +
                "_alarm_id INTEGER PRIMARY KEY, " +
                "routeInfo TEXT);");
        db.execSQL("CREATE TABLE RunIndex (" +
                "_alarm_id INTEGER PRIMARY KEY, " +
                "cur_index INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS dic");
        onCreate(db);
        db.close();
    }

    /** 삽입 SQL
     *
     * @param mAlarmValues
     * @param alarmDay
     * @param routeInfo
     * @return topNumber
     */
    public int insert(DTOAlarmValues mAlarmValues,
                         String alarmDay,
                         String routeInfo) {
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
        //String alarmTAG = "org.daelimie.test.daelimie.TEST";
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
        String sqlForRoute = "INSERT INTO RouteInfo VALUES(" +
                "'" + (topNumber+1) + "', " +
                "'" + routeInfo + "');";
        Log.d("DB Manager", "알람 데이터: "+sql);
        Log.d("DB Manager", "루트 데이터: "+sqlForRoute);

        // DB 작업 실행
        SQLiteDatabase dbW = getWritableDatabase();
        dbW.beginTransaction();
        try {
            dbW.execSQL(sql);
            dbW.execSQL(sqlForRoute);
            dbW.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbW.endTransaction(); //트랜잭션을 끝내는 메소드.
        }

        dbW.close();
        dbR.close();
        return topNumber+1;
    }

    /** 수정 SQL
     *
     * @param mAlarmValues
     * @param alarmDay
     * @param routeInfo
     * @param ids
     * @return ids
     */
    public int update(DTOAlarmValues mAlarmValues,
                         String alarmDay,
                         String routeInfo,
                         int ids) {
        String alarmTAG = "org.daelimie.test.daelimie.Alarmming" + ids;
        //String alarmTAG = "org.daelimie.test.daelimie.TEST";
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
        String sqlForRoute = "UPDATE RouteInfo SET " +
                "routeInfo='" + routeInfo + "' " +
                "WHERE _alarm_id='"+ids+"' ;";

        // DB 작업 실행
        SQLiteDatabase dbW = getWritableDatabase();
        dbW.beginTransaction();
        try {
            dbW.execSQL(sql);
            dbW.execSQL(sqlForRoute);
            dbW.setTransactionSuccessful();
            //dbW.endTransaction();
            //dbW.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dbW.endTransaction(); //트랜잭션을 끝내는 메소드.
        }

        dbW.close();

        return ids;
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(int ids) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM AlarmList WHERE _id='" + ids + "'");
            db.execSQL("DELETE FROM RouteInfo WHERE _alarm_id='" + ids + "'");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.execSQL("DELETE FROM AlarmList");
            db.execSQL("DELETE FROM RouteInfo");
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            db.endTransaction();
        }
        db.close();
    }

    public int printCountOfData() {
        SQLiteDatabase db = getReadableDatabase();
        int count=0;

        Cursor cursor = db.rawQuery("SELECT * FROM AlarmList ORDER BY _id DESC", null);
        while(cursor.moveToNext()) {
            count += cursor.getInt(0);
        }
        db.close();
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

        db.close();
        return allData;
    }

    public JSONObject getData(int id) {
        SQLiteDatabase db = getReadableDatabase();
        JSONObject data = new JSONObject();
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

        db.close();
        return data;
    }

    public JSONObject getData(String tag) {
        SQLiteDatabase db = getReadableDatabase();
        JSONObject data = new JSONObject();
        try {
            Cursor cursor = db.rawQuery("SELECT * FROM AlarmList WHERE _tag='"+tag+"' ORDER BY _id DESC", null);
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

        db.close();
        return data;
    }

    public JSONArray getRoute(int id) {
        SQLiteDatabase db = getReadableDatabase();
        JSONArray data = new JSONArray();
        try {
            Cursor cursor = db.rawQuery("SELECT routeInfo FROM RouteInfo WHERE _alarm_id='"+id+"'", null);
            while(cursor.moveToNext()) {
                JSONArray tempData = new JSONArray(cursor.getString(0));

                data = tempData;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        db.close();
        return data;
    }

    public int getIndex(int _alarm_id) {
        SQLiteDatabase db = getReadableDatabase();
        int cur_index = 0;
        Cursor cursor = db.rawQuery("SELECT * FROM RunIndex WHERE _alarm_id='" + _alarm_id + "';", null);
        while(cursor.moveToNext()) {
            cur_index = cursor.getInt(1);
        }

        db.close();
        return cur_index;
    }

    public int setIndex(int _alarm_id, int cur_index) {
        SQLiteDatabase dbR = getReadableDatabase();
        Cursor cursor = dbR.rawQuery("SELECT * FROM RunIndex WHERE _alarm_id='" + _alarm_id + "';", null);
        if(cursor.moveToNext()) {
            updateIndex(_alarm_id, cur_index);
        } else {
            String sql = "INSERT INTO RunIndex VALUES(" +
                    "'" + _alarm_id + "'," +
                    "'" + cur_index + "');";

            // DB 작업 실행
            SQLiteDatabase dbW = getWritableDatabase();
            dbW.execSQL(sql);
            dbW.close();
        }

        dbR.close();
        return cur_index;
    }

    public int updateIndex(int _alarm_id, int cur_index) {

        String sql = "UPDATE RunIndex SET " +
                "_alarm_id = '" + _alarm_id + "'," +
                "cur_index = '" + cur_index + "' " +
                "WHERE _alarm_id ='"+_alarm_id+"';";

        // DB 작업 실행
        SQLiteDatabase dbW = getWritableDatabase();
        dbW.execSQL(sql);
        dbW.close();

        return cur_index;
    }

    public void delIndex(int _alarm_id) {
        String sql = "DELETE FROM RunIndex " +
                "WHERE _alarmd_id = '" + _alarm_id + "');";

        // DB 작업 실행
        SQLiteDatabase dbW = getWritableDatabase();
        dbW.execSQL(sql);
        dbW.close();
    }
}
