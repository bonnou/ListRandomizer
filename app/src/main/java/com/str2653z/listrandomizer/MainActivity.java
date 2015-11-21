package com.str2653z.listrandomizer;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter adapter;
    public static final String EXTRA_MYID = "com.str2653z.listrandomizer.MYID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // プロジェクト作成時からあるお約束
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adapter設定
        String[] from = {
                ItemContract.Items.COL_TITLE,
                ItemContract.Items.COL_UPDATED
        };
        int[] to = {
                android.R.id.text1,
                android.R.id.text2
        };
        adapter = new SimpleCursorAdapter(
                this,                                   // 本Activityオブジェクト
                android.R.layout.simple_list_item_2,    // 行レイアウトのリソース、androidのものを使用。shift二回押して検索すると構造が確認可
                null,                                   // Cursorを指定、あとでCursorLoaderが後で作ってくれるので一旦null
                from,                                   // どのカラムを・・・
                to,                                     // 行レイアウトリソースのどのIDに表示するか
                0                                       // 動作規定用フラグ
        );

        // layoutのxmlにて定義したListViewを取得しAdapterを設定
        ListView mainListView = (ListView) findViewById(R.id.mainListView);
        mainListView.setAdapter(adapter);
        //
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(
                    AdapterView<?> parent,
                    View view,
                    int position,
                    long id
            ) {
                // 別Activityに画面遷移する（タップした行のidを渡す）
                // 呼び出されるActivityのonCreateに続く...
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra(EXTRA_MYID, id);
                startActivity(intent);




            }
        });

        // CursorLoaderのためのLoader初期化 → AdapterにonCreateLoaderが返却したCursorが設定される
        getLoaderManager().initLoader(0, null, this);






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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

    @Override // Menuのメソッド実装、res/menuフォルダ内のどのxmlを使用するかを指定
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override // Menuのメソッド実装、メニューのオプション項目の選択イベントを判断し適切な処理を実行
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            Intent intent = new Intent(this, SubActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // LoaderManagerのメソッド実装、初回のクエリ実行内容を指定
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // SELECT句
        String[] projection = {
                ItemContract.Items._ID,
                ItemContract.Items.COL_TITLE,
                ItemContract.Items.COL_UPDATED
        };

        // 初回query発行
        return new CursorLoader(
                this,
                ItemContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                ItemContract.Items.COL_UPDATED + " DESC"
        );
    }

    @Override // LoaderManagerのメソッド実装、ContentProviderからデータが帰ってきた時に実行される
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // 帰ってきたデータでAdapterを更新
        adapter.swapCursor(data);
    }

    @Override // LoaderManagerのメソッド実装、何らかの理由でLoaderがリセットされた時に実行される
    public void onLoaderReset(Loader<Cursor> loader) {
        // ひとまずnull
        adapter.swapCursor(null);
    }
}
