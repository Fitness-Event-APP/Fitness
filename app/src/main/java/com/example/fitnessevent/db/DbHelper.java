package com.example.fitnessevent.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.Type;

public abstract class DbHelper {
    private final Gson gson = new GsonBuilder().create();
    private String tableName;
    private String columnKey;
    private String columnData;
    private String columnDate;
    private boolean autoPurge;

    private SQLiteDatabase cachedb = null;
    private FirebaseDatabase db;

    private static final int MILLISECONDS_IN_A_HOUR = 1000 * 60 * 60;

    DbHelper(Context context, String dbName, int dbVersion,
             String tableName, String columnKey, String columnData, String columnDate, boolean autoPurge) {

        this.tableName = tableName;
        this.columnKey = columnKey;
        this.columnData = columnData;
        this.columnDate = columnDate;
        this.autoPurge = autoPurge;

        SQLiteOpenHelper helper = new DatabaseHelper(context, dbName, dbVersion);
        this.cachedb = helper.getWritableDatabase();

        if (autoPurge) {
            purgeDB();
        }
        this.db = FirebaseDatabase.getInstance();
    }

    /*
     * cache delete: Remove content from a repository and stores this in a "graveyard".
     */
    private void purgeDB() {
        if (!autoPurge) {
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                long time = (System.currentTimeMillis() - (MILLISECONDS_IN_A_HOUR * 6));
                // whereArgs = ? in whereClause
                cachedb.delete(tableName,columnDate + "<? AND " + columnDate + "!=0", new String[]{String.valueOf(time)});
            }
        }).start();
    }

    /*
     * cache update,insert
     */
    public void insert(String key, Object data, long expirationOffset) {
        try{
            ContentValues cv = new ContentValues();
            cv.put(columnKey, key);
            cv.put(columnData, gson.toJson(data));
            cv.put(columnDate, System.currentTimeMillis() + (expirationOffset * 1000));

            int rows = cachedb.update(tableName, cv,columnKey + "=?",new String[]{key});
            if (rows == 0) {
                cachedb.insert(tableName,columnKey, cv);
            }
        } catch (Throwable t) {
            Log.d("insert/update","cache sql insert/update data failed");
        }
    }

    /*
     * firebase write and read(listener)

    public void write(String key, Object data) {
        try{
            DatabaseReference ref = db.getReference(key);
            ref.child(key)

            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            })
        } catch (Throwable t) {
            Log.d("firebase write","write key and json(data) failed");
        }
    }
    */


    /*
     * cache get Item from sqliteDatabase cachedb
     */
    public <T> DbItem<T> getDbItem(String key, Type type) {
        if (key == null || type == null) {
            return null;
        }
        Cursor c = cachedb.query(tableName, new String[]{columnKey, columnData, columnDate},
                columnKey + "=?", new String[]{key},null, null, null);
        DbItem<T> response = null;
        if (c.moveToFirst()) {
            String json;
            try {
                json = c.getString(c.getColumnIndex(columnData));
                long date = c.getLong(c.getColumnIndex(columnDate));
                response = new DbItem<>((T) gson.fromJson(json,type), date);
            } catch (Throwable e) {
                Log.d("getItem", "retrieve an object from the database failed!");
            }
        }
        c.close();
        return  response;
    }



    private class DatabaseHelper extends SQLiteOpenHelper {

        private DatabaseHelper(Context context, String dbName, int dbVersion) {
            super(context, dbName,null, dbVersion);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(String.format("CREATE TABLE %s (%s TEXT PRIMARY KEY, %s TEXT, %s LONG);",
                    tableName,columnKey,columnData,columnDate));
            db.execSQL(String.format("CREATE INDEX %s_%s ON %s(%s);",
                    tableName, columnDate, tableName, columnDate));

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        }
    }



}
