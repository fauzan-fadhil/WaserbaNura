package com.arindo.waserbanura.Sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    static final String DATABASE_NAME = "nuradatabase.db";
    public static final String TABLE_SQLite = "sqlitetabel1";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_MENUID = "menuid";
    public static final String COLUMN_NAMA = "nama";
    public static final String COLUMN_TOTAL = "total";
    public static final String COLUMN_HARGA = "harga";
    public static final String COLUMN_JUMLAH = "jumlah";
    public static final String COLUMN_URL = "url";
    public static final String COLUMN_PLACEID = "placeid";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + TABLE_SQLite + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY autoincrement, " +
                COLUMN_MENUID + " TEXT NOT NULL, " +
                COLUMN_NAMA + " TEXT NOT NULL, " +
                COLUMN_TOTAL + " TEXT NOT NULL, " +
                COLUMN_HARGA + " TEXT NOT NULL, " +
                COLUMN_JUMLAH + " TEXT NOT NULL, " +
                COLUMN_URL + " TEXT NOT NULL, " +
                COLUMN_PLACEID + " TEXT " +
                " )";
        db.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion >= newVersion)
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SQLite);
        onCreate(db);
    }

    public ArrayList<HashMap<String, String>> getAllData() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_SQLite;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(COLUMN_ID, cursor.getString(0));
                map.put(COLUMN_MENUID, cursor.getString(1));
                map.put(COLUMN_NAMA, cursor.getString(2));
                map.put(COLUMN_TOTAL, cursor.getString(3));
                map.put(COLUMN_HARGA, cursor.getString(4));
                map.put(COLUMN_JUMLAH, cursor.getString(5));
                map.put(COLUMN_URL, cursor.getString(6));
                map.put(COLUMN_PLACEID, cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        Log.e("hasilnyabroooo sqlite ", "" + wordList);
        database.close();
        return wordList;
    }

    public ArrayList<HashMap<String, String>> getAllDataPlaceid(String placeeid) {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_SQLite + " WHERE " + COLUMN_PLACEID + " = '" + placeeid + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(COLUMN_ID, cursor.getString(0));
                map.put(COLUMN_MENUID, cursor.getString(1));
                map.put(COLUMN_NAMA, cursor.getString(2));
                map.put(COLUMN_TOTAL, cursor.getString(3));
                map.put(COLUMN_HARGA, cursor.getString(4));
                map.put(COLUMN_JUMLAH, cursor.getString(5));
                map.put(COLUMN_URL, cursor.getString(6));
                map.put(COLUMN_PLACEID, cursor.getString(7));
                wordList.add(map);
            } while (cursor.moveToNext());
        }
        Log.e("tampil by placeid ", "" + wordList);
        database.close();
        return wordList;
    }

    public void insert(String menuid, String nama, String total, String harga, String jumlah, String url, String placeid) {
        SQLiteDatabase database = this.getWritableDatabase();
        String queryValues = "INSERT INTO " + TABLE_SQLite + " (menuid, nama, total, harga, jumlah, url, placeid) " +
                "VALUES ('" + menuid + "' , '" + nama + "' , '" + total + "','" + harga + "','" + jumlah + "','" + url + "','" + placeid +"')";

        Log.e("insert sqlite ", "" + queryValues);
        database.execSQL(queryValues);
        database.close();
    }

    public void update( String menuid, String nama, String total, String harga, String jumlah, String url, String placeid) {
        SQLiteDatabase database = this.getWritableDatabase();

        String updateQuery = "UPDATE " + TABLE_SQLite + " SET "
                + COLUMN_MENUID + "='" + menuid + "',"
                + COLUMN_NAMA + "='" + nama + "',"
                + COLUMN_TOTAL + "='" + total + "',"
                + COLUMN_JUMLAH + "='" + jumlah + "',"
                + COLUMN_HARGA + "='" + harga + "',"
                + COLUMN_URL + "='" + url + "',"
                + COLUMN_PLACEID + "='" + placeid + "'"
                + " WHERE " + COLUMN_MENUID + "=" + "'" + menuid + "'";
        Log.e("update sqlite ", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public int delete(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int ID = db.delete(TABLE_SQLite, COLUMN_ID + "=?",
                new String[]{String.valueOf(id)});
        db.close();
        return ID;
    }

    public int getNotesCount() {
        String countQuery = "SELECT  * FROM " + TABLE_SQLite ;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getNotesCount2(String placeid) {
        String countQuery = "SELECT  * FROM " + TABLE_SQLite + " WHERE " + COLUMN_PLACEID + " = '" + placeid + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public void delete2(int id) {
        SQLiteDatabase database = this.getWritableDatabase();
        String updateQuery = "DELETE FROM " + TABLE_SQLite;
        Log.e("update sqlite ", updateQuery);
        database.execSQL(updateQuery);
        database.close();
    }

    public int cekmenuid(String menuid) {
        String countQuery = "SELECT  * FROM " + TABLE_SQLite + " WHERE " + COLUMN_MENUID + " = '" + menuid + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        Log.e(" sqlite ", countQuery);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int getJumlah(String menuid){
        String getjml =   "SELECT * FROM " + TABLE_SQLite + " WHERE " + COLUMN_MENUID + " = '" + menuid + "'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(getjml, null);
        if (cursor.moveToFirst()) {
            int count = Integer.parseInt(cursor.getString(5));
            return count;
        } else {
            return 0;
        }
    }
}
