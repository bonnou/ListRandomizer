package com.str2653z.listrandomizer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by str2653z on 2015/10/31.
 */
public class ItemOpenHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "myapp.db";
    public static final int DB_VERSION = 1;

    public static final String CREATE_TABLE =
            "create table items (" +
                    "_id integer primary key autoincrement, " +
                    "title text, " +
                    "body text, " +
                    "created datetime default current_timestamp, " +
                    "updated datetime default current_timestamp)";
    public static final String INIT_TABLE =
            "insert into items (title, body) values " +
                    "('t1', 'b1'), " +
                    "('t2', 'b2'), " +
                    "('t3', 'b3')";
    public static final String DROP_TABLE =
            "drop table if exsit items";

    public ItemOpenHelper(Context c) {
        super(c, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(INIT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE);
        onCreate(db);
    }
}
