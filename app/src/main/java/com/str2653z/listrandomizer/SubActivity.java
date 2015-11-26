package com.str2653z.listrandomizer;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.str2653z.listrandomizer.common.DialogListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EventListener;
import java.util.Locale;

public class SubActivity extends AppCompatActivity implements DialogListener {

    public static final String CLASS_NAME = "SubActivity";

    private static final String sTAG_SUBACTIVITY_SAVEDIALOG = "SubActivity_SaveDialog";

    private long itemId;

    private EditText titleText;
    private EditText bodyText;
    private TextView updatedText;

    /** 保存確認ダイアログ表示判定用に、アクティビティ表示時点の内容を保持しておく */
    private String beforeTitle = "";
    private String beforeBody = "";

    /** trueの場合は保存確認ダイアログを無条件で非表示にする */
    public static boolean dontOpenSaveDialog = false;

    private void clearField() {
        beforeTitle = "";
        beforeBody = "";
        dontOpenSaveDialog = false;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = "onCreate";

        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_sub);

        // フィールド初期化
        clearField();

        // ツールバーを設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ツールバーに戻るボタンを追加
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 入力変更がある場合
                if ( !dontChangeData() ) {
                    // ダイアログを表示する
                    openSaveDialog();
                } else {
                    // 内容に変更がなければダイアログなしでActivityを終了
                    finish();
                }


            }
        });

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
            beforeTitle = c.getString(c.getColumnIndex(ItemContract.Items.COL_TITLE));

            bodyText.setText(
                    c.getString(c.getColumnIndex(ItemContract.Items.COL_BODY))
            );
            beforeBody = c.getString(c.getColumnIndex(ItemContract.Items.COL_BODY));

            updatedText.setText(
                    "Updated: " + c.getString(c.getColumnIndex(ItemContract.Items.COL_UPDATED))
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

    /**
     * 保存確認ダイアログを表示
     * ※現状以下で呼び出している
     * ・ツールバーの戻るボタンを押下した場合
     * ・onKeyDownにてBACKボタン押下を検知した場合
     */
    private void openSaveDialog() {
        SaveDialogFragment saveDialogFragment = new SaveDialogFragment();
        saveDialogFragment.setDialogListener(SubActivity.this);
        saveDialogFragment.show(getFragmentManager(), sTAG_SUBACTIVITY_SAVEDIALOG);
        Log.d(CLASS_NAME, "■Saveダイアログ表示");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        final String METHOD_NAME = "onKeyDown";
        Log.d(METHOD_NAME, "enterring");

        // BACKボタンの場合
        if(keyCode == KeyEvent.KEYCODE_BACK){
            // 入力変更がある場合
            if ( !dontChangeData() ) {
                // ダイアログを表示する
                openSaveDialog();
                return false;
            } else {
                // 内容に変更がなければダイアログなしでActivityを終了
                return super.onKeyDown(keyCode, event);
            }
        } else {
            // BACKボタンでなければ何もしない
            return super.onKeyDown(keyCode, event);
        }
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
        final String METHOD_NAME = "dontChangeData";
        Log.d(METHOD_NAME, "■saveItem enterring");

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
                // 保存確認ダイアログ無しでActivityを閉じる
                dontOpenSaveDialog = true;
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
                // 保存確認ダイアログ無しでActivityを閉じる
                dontOpenSaveDialog = true;
                finish();
            }
        }
    }

    @Override
    public void onDestroy() {
        final String METHOD_NAME = "onDestroy";
        Log.d(CLASS_NAME + "." + METHOD_NAME, "■onDestroy起動");

        // フィールド初期化
        clearField();
        super.onDestroy();
    }

    /**
     * 画面入力値変更判定
     *
     * @return Activity表示時から画面入力値を変更していない場合true
     */
    private boolean dontChangeData() {
        final String METHOD_NAME = "dontChangeData";

        boolean result = false;

        // 現在の入力内容を取得
        String title = titleText.getText().toString();
        String body = bodyText.getText().toString();

        Log.d(METHOD_NAME, "■title：[" + title + "]");
        Log.d(METHOD_NAME, "■beforeTitle：[" + beforeTitle + "]");
        Log.d(METHOD_NAME, "■body：" + body + "]");
        Log.d(METHOD_NAME, "■beforeBody：" + beforeBody + "]");

        if (
                title.equals(beforeTitle)
             && body.equals(beforeBody)
        ) {
            result = true;
        }

        // TODO:TAGは23文字までしか設定できない→クラス名とメソッド名にはできない
        Log.d(METHOD_NAME, "■result：" + result);
        return result;
    }

    @Override
    public void doPositiveClick() {
        Log.d(CLASS_NAME, "■doPositiveClick ");
        saveItem();
    }

    @Override
    public void doNegativeClick() {
        Log.d(CLASS_NAME, "■doNegativeClick");
    }

    public static class SaveDialogFragment extends DialogFragment {
        private DialogListener listener = null;

        private boolean finishActivityFlg = false;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("内容が変更されています。保存しますか？")
                    .setPositiveButton("保存して終了", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishActivityFlg = true;
                            listener.doPositiveClick();
                            dismiss();
                        }
                    })
                    .setNegativeButton("保存せず終了", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishActivityFlg = true;
                            listener.doNegativeClick();
                            dismiss();

                        }
                    })
                    .setNeutralButton("再編集", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finishActivityFlg = false;
                            Log.d(CLASS_NAME, "■NeutralButton：do nothing ");
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }

        @Override
        public void onStop() {
            final String METHOD_NAME = "dontChangeData";
            Log.d(METHOD_NAME, "■onStop entering");

            super.onStop();
            if (finishActivityFlg) {
                getActivity().finish();
            }
            finishActivityFlg = false;
        }

        /**
         * リスナーを追加
         */
        public void setDialogListener(DialogListener listener){
            this.listener = listener;
        }

        /**
         * リスナー削除
         */
        public void removeDialogListener(){
            this.listener = null;
        }
    }


}
