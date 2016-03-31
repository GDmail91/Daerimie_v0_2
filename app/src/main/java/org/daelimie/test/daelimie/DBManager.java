package org.daelimie.test.daelimie;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
                "departureLocate TEXT, " +
                "destinationName TEXT, " +
                "destinationPlaceId TEXT, " +
                "destinationLocate TEXT, " +
                "arrivalTime TEXT, " +
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

    public void insert(String _query) {
        /*String sql = "INSERT INTO AlarmList VALUES(" +
                "null, " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + name + "', " +
                "'" + price + "');";*/
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void update(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public void delete(String _query) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(_query);
        db.close();
    }

    public String PrintData() {
        SQLiteDatabase db = getReadableDatabase();
        String str = "";

        Cursor cursor = db.rawQuery("select * from FOOD_LIST", null);
        while(cursor.moveToNext()) {
            str += cursor.getInt(0)
                    + " : foodName "
                    + cursor.getString(1)
                    + ", price = "
                    + cursor.getInt(2)
                    + "\n";
        }

        return str;
    }
}
