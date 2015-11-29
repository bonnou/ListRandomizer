package com.str2653z.listrandomizer;

import android.provider.BaseColumns;

/**
 * Created by str2653z on 2015/10/31.
 */
public final class ItemContract {

    public ItemContract() {};

    public static abstract class Items implements BaseColumns {
        public static final String TABLE_NAME = "items";
        public static final String COL_TITLE = "title";
        public static final String COL_BODY = "body";
        public static final String COL_CREATED = "created";
        public static final String COL_UPDATED = "updated";
        public static final String COL_ORDERNUM = "orderNum";
    }
}
