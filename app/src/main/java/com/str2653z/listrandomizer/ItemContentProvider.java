package com.str2653z.listrandomizer;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ItemContentProvider extends ContentProvider {

    public static final String AUTHORITY =
            "com.str2653z.listrandomizer.ItemContentProvider";
    public static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/" + ItemContract.Items.TABLE_NAME);

    // UriMatcher
    private static final int MEMOS = 1;
    private static final int MEMO_ITEM = 2;
    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, ItemContract.Items.TABLE_NAME, MEMOS);
        uriMatcher.addURI(AUTHORITY, ItemContract.Items.TABLE_NAME + "/#", MEMO_ITEM);
    }

    private ItemOpenHelper itemOpenHelper;

    public ItemContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
/*
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
*/

        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI:" + uri);
        }
        SQLiteDatabase db = itemOpenHelper.getWritableDatabase();
        int deletedCount = db.delete(
                ItemContract.Items.TABLE_NAME,
                selection,
                selectionArgs
        );
        // データ変更の通知
        getContext().getContentResolver().notifyChange(uri, null);
        // 更新件数を返却
        return deletedCount;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
/*
        // TODO: Implement this to handle requests to insert a new row.
        throw new UnsupportedOperationException("Not yet implemented");
*/

        if (uriMatcher.match(uri) != MEMOS) {
            throw new IllegalArgumentException("Invalid URI:" + uri);
        }
        SQLiteDatabase db = itemOpenHelper.getWritableDatabase();
        long newId = db.insert(
                ItemContract.Items.TABLE_NAME,
                null,                           // データが空だったときにどうするか、とりあえずnull
                values
        );

        Uri newUri = ContentUris.withAppendedId(
                ItemContentProvider.CONTENT_URI,
                newId
        );

        // データ変更の通知
        getContext().getContentResolver().notifyChange(newUri, null);
        return newUri;
    }

    @Override
    public boolean onCreate() {
        itemOpenHelper = new ItemOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder
    ) {
        // 不正なURIはリジェクト
        switch( uriMatcher.match(uri) ) {
            case MEMOS:
            case MEMO_ITEM:
                break;
            default:
                throw new IllegalArgumentException("Invalid URI:" + uri);
        }

        // DBを開きクエリを発行
        SQLiteDatabase db = itemOpenHelper.getReadableDatabase();
        Cursor c = db.query(
                ItemContract.Items.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
        // データ変更時にUIに変更を通知するための監視処理を設定
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs
    ) {
/*
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
*/

        if (uriMatcher.match(uri) != MEMO_ITEM) {
            throw new IllegalArgumentException("Invalid URI:" + uri);
        }
        SQLiteDatabase db = itemOpenHelper.getWritableDatabase();
        int updatedCount = db.update(
                ItemContract.Items.TABLE_NAME,
                values,
                selection,
                selectionArgs
        );
        // データ変更の通知
        getContext().getContentResolver().notifyChange(uri, null);
        // 更新件数を返却
        return updatedCount;
    }
}
