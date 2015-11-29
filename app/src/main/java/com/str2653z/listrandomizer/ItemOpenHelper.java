package com.str2653z.listrandomizer;

import android.content.Context;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by str2653z on 2015/10/31.
 */
public class ItemOpenHelper extends SQLiteOpenHelper {
    public static final String CLASS_NAME = "ItemOpenHelper";

    public static final String DB_NAME = "myapp.db";
    public static final int DB_VERSION = 2; // 1ずつあげる事！！！

    public static final String CREATE_TABLE =
            "create table items (" +
                    "_id integer primary key autoincrement, " +
                    "title text, " +
                    "body text, " +
                    "created datetime default current_timestamp, " +
                    "updated datetime default current_timestamp, " +
                    "orderNum integer default 0)";
    // TODO: いつか消えるINSERT文
    public static final String INIT_TABLE =
            "insert into items (title, body, orderNum) values " +
                    "('t1', 'b1', 1), " +
                    "('t2', 'b2', 2), " +
                    "('t3', 'b3', 3)";
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
//        db.execSQL(DROP_TABLE);
//        onCreate(db);

        final String METHOD_NAME = "onUpgrade";
        Log.d(METHOD_NAME, "entering");

        for (int ver = oldVersion + 1; ver <= newVersion; ver++) {
            if (ver == 2) {
                // カラム追加
                db.execSQL("ALTER TABLE items ADD COLUMN orderNum integer default 0;");

                // 全件の_idを取得し新規カラムに_idを設定
                SQLiteCursor c1 = (SQLiteCursor)db.rawQuery("SELECT _id FROM items;", null);
                c1.moveToFirst();
                CharSequence[] idList = new CharSequence[c1.getCount()];
                for (int i = 0; i < idList.length; i++) {
                    idList[i] = c1.getString(0);
                    c1.moveToNext();
                }
                c1.close();
                for (CharSequence charseq : idList) {
                    db.execSQL("UPDATE items SET orderNum = ? where _id = ?;", new Object[]{charseq, charseq});
                }

                // 反映確認
                SQLiteCursor c2 = (SQLiteCursor)db.rawQuery("SELECT _id, orderNum FROM items;", null);
                c2.moveToFirst();
                for (int i = 0; i < c2.getCount(); i++) {
                    Log.d(CLASS_NAME + "." + METHOD_NAME, "_id:" + c2.getString(0) + ", orderNum:" + c2.getString(1));
                    c2.moveToNext();
                }
                c2.close();
            }
        }
    }
}
