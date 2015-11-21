package com.str2653z.listrandomizer;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SubActivity extends AppCompatActivity {

    private long itemId;

    private EditText titleText;
    private EditText bodyText;
    private TextView updatedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sub);

        // メニューを設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // layoutの要素を取得
        titleText = (EditText) findViewById(R.id.titleText);
        bodyText = (EditText) findViewById(R.id.bodyText);
        updatedText = (TextView) findViewById(R.id.updatedText);

        // MainActivityからのIntentよりidを受け取る
        // 受け取れない場合は0を設定
        Intent intent = getIntent();
        itemId = intent.getLongExtra(MainActivity.EXTRA_MYID, 0L);

        // idの取得状況によって動作を出し分け
        if (itemId == 0L) {
            // new item
            if (getSupportActionBar() != null) {
                // getSupportActionBarがnullになることもあるのでチェックしたうえでタイトルを変更
                getSupportActionBar().setTitle("New item");
            }
            updatedText.setText("Updated: --------");
        } else {
            // show item
            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Edit item");
            }
            Uri uri = ContentUris.withAppendedId(
                    ItemContentProvider.CONTENT_URI,
                    itemId
            );
            // ContetProviderによりクエリ発行しCursorを取得
            String[] projection = {
                    ItemContract.Items.COL_TITLE,
                    ItemContract.Items.COL_BODY,
                    ItemContract.Items.COL_UPDATED
            };
            Cursor c = getContentResolver().query(
                    uri,                                    // CONTENT_URI
                    projection,                             // SELECT句
                    ItemContract.Items._ID + " = ?",        // WHERE句
                    new String[]{Long.toString(itemId)}, // バインド内容をString配列で指定
                    null                                    // ORDER BY、一件なので指定不要
            );
            // 最初のレコードに移動
            c.moveToFirst();
            titleText.setText(
                    c.getString(c.getColumnIndex(ItemContract.Items.COL_TITLE))
            );
            bodyText.setText(
                    c.getString(c.getColumnIndex(ItemContract.Items.COL_BODY))
            );
            updatedText.setText(
                    "Updated: " +
                            c.getString(c.getColumnIndex(ItemContract.Items.COL_UPDATED))
            );
            c.close();
        }

/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/
    }

    @Override // OptionsMenuが用意される時に呼ばれるメソッド
    public boolean onPrepareOptionsMenu(Menu menu) {
        // 追加時は削除ボタンを非表示
        MenuItem deleteItem = menu.findItem(R.id.action_delete);
        if (itemId == 0L) {
            deleteItem.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sub, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
/*
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
*/
        switch ( item.getItemId() ) {
            case R.id.action_delete:
                deleteItem();
                break;
            case R.id.action_save:
                saveItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteItem() {
        new AlertDialog.Builder(this)
                .setTitle("Delete Memo")
                .setMessage("Are you sure?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Uri uri = ContentUris.withAppendedId(
                                ItemContentProvider.CONTENT_URI,
                                itemId
                        );
                        getContentResolver().delete(
                                uri,                                    // CONTENT_URI
                                ItemContract.Items._ID + " = ?",        // WHERE句
                                new String[] { Long.toString(itemId) }  // バインド内容をString配列で指定
                        );
                        finish();
                    }
                })
                .show();
    }

    private void saveItem() {
        // 現在の入力内容を取得
        String title = titleText.getText().toString().trim();
        String body = bodyText.getText().toString().trim();
        String updated = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss", Locale.US).format(new Date());

        // エラーチェック
        if ( title.isEmpty() ) {
            Toast.makeText(
                    SubActivity.this,
                    "Please enter title",
                    Toast.LENGTH_LONG
            ).show();
        } else {
            ContentValues values = new ContentValues();
            values.put(ItemContract.Items.COL_TITLE, title);
            values.put(ItemContract.Items.COL_BODY, body);
            values.put(ItemContract.Items.COL_UPDATED, updated);

            if (itemId == 0L) {
                // insert memo
                getContentResolver().insert(
                        ItemContentProvider.CONTENT_URI,        // CONTENT_URI
                        values                                  // ContentValuesオブジェクト
                );
                finish();
            } else {
                // update memo
                Uri uri = ContentUris.withAppendedId(
                        ItemContentProvider.CONTENT_URI,
                        itemId
                );
                getContentResolver().update(
                        uri,                                    // CONTENT_URI
                        values,                                 // ContentValuesオブジェクト
                        ItemContract.Items._ID + " = ?",        // WHERE句
                        new String[] { Long.toString(itemId) }  // バインド内容をString配列で指定
                );
                // Activityを閉じる
                finish();
            }
        }
    }


}
