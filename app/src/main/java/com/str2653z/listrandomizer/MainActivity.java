package com.str2653z.listrandomizer;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String CLASS_NAME = "MainActivity";

    private SimpleCursorAdapter adapter;
    public static final String EXTRA_MYID = "com.str2653z.listrandomizer.MYID";
    public static final String EXTRA_AGENDA = "com.str2653z.listrandomizer.AGENDA";

    /** 改行 */
    static final String BR = System.getProperty("line.separator");

    // FloatingActionButtonのonClickイベントで参照したかったのでフィールドを作成
    // staticである必要はないのかも・・・
    public static ListView mainListView;

    public int defaultBackgroundColor;

    public Integer selectedRandomIndex;

    // ListViewのインデックスリスト
    List<Integer> indexList = new ArrayList<Integer>();

    private void initIndexSet() {
        // ListViewの件数をカウントし全インデックス値を詰める
        int mainListViewCnt = mainListView.getCount();
        for (int i = 0; i < mainListViewCnt; i++) {
            indexList.add(new Integer(i));
        }
    }

    private Integer getIndexRandom() {
        // インデックスリストからランダムに要素を取得
        Random random = new Random();
        int randomIndex = random.nextInt(indexList.size());
        Integer i = indexList.get(randomIndex);
        indexList.remove(randomIndex);
        return i;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final String METHOD_NAME = "onCreate";

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
        //
        adapter = new SimpleCursorAdapter(
                this,                                   // 本Activityオブジェクト
                android.R.layout.simple_list_item_2,    // 行レイアウトのリソース、androidのものを使用。shift二回押して検索すると構造が確認可
                null,                                   // Cursorを指定、あとでCursorLoaderが後で作ってくれるので一旦null
                from,                                   // どのカラムを・・・
                to,                                     // 行レイアウトリソースのどのIDに表示するか
                0                                       // 動作規定用フラグ
        );

        // layoutのxmlにて定義したListViewを取得しAdapterを設定
        mainListView = (ListView) findViewById(R.id.mainListView);
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

        // デフォルト色を取得し保持
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.colorBackground, typedValue, true);
        int resourceId = typedValue.resourceId;
        defaultBackgroundColor = ContextCompat.getColor(MainActivity.this, resourceId);



        // ツールバーを設定
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
/*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
*/

                if ( mainListView.getCount() == 0 ) {
                    // ListViewが0件の場合は警告
                    Toast.makeText(
                            MainActivity.this,
                            "Please add item",
                            Toast.LENGTH_LONG
                    ).show();
                } else {


                    // インデックスセットがnullまたはサイズ0の場合は初期化
                    if (indexList.size() == 0) {
                        initIndexSet();
                    }

                    // インデックスセットからランダムに1つ取り出しスクロール
                    // スクロール前に、スクロール前後の一番上のインデックスを取得しておく
                    final int randomIndex = getIndexRandom();
                    selectedRandomIndex = new Integer(randomIndex);
                    final int beforeFirstVisiblePosition = mainListView.getFirstVisiblePosition();

                    // TODO: スクロール前にも表示中の背景色を初期化する。スクロール後と処理が被るので切り出す

                    //リストアイテムの総数-1（0番目から始まって最後のアイテム）にスクロールさせる
                    mainListView.smoothScrollToPosition(randomIndex);
//                    mainListView.setSelection(randomIndex);

                    // スムーススクロール後に一番上に表示しているViewを取るために待機
                    //  http://qiita.com/Kaiketch/items/51156f2a38ba181440dc
                    // TODO:イケてなさそうなので別解を考える
                    //  http://jp.androids.help/q15937
                    mainListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            int afterFirstVisiblePosition = mainListView.getFirstVisiblePosition();

                            Log.d(CLASS_NAME + "." + METHOD_NAME, "■スクロール前の一番上の表示Viewのインデックス：" + beforeFirstVisiblePosition);
                            Log.d(CLASS_NAME + "." + METHOD_NAME, "■スクロール後の一番上の表示Viewのインデックス：" + afterFirstVisiblePosition);

                            // ランダム選択されたViewの背景色を変更
                            View randomSelectedView = mainListView.getChildAt(randomIndex - afterFirstVisiblePosition);
                            Log.d(CLASS_NAME + "." + METHOD_NAME, "■ランダム選択Viewのインデックス：" + randomIndex);

                            if (randomSelectedView != null) {
                                Log.d(CLASS_NAME + "." + METHOD_NAME, "■ランダム選択Viewタイトル：" + ((TwoLineListItem)randomSelectedView).getText1().getText());
                                randomSelectedView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                            }
                            // ランダム選択以外の背景色をデフォルトに戻す（画面表示中のもののみ）
                            // 画面表示外は再表示時のadapter.getViewをオーバーライドし実施
                            for (int i = 0;
                                 i <= mainListView.getFirstVisiblePosition() + mainListView.getChildCount();
                                 i++)
                            {

                                View v = mainListView.getChildAt(i);
                                if (v != null && v != randomSelectedView) {


                                    Log.d(CLASS_NAME + "." + METHOD_NAME, "■色初期化Viewタイトル：" + ((TwoLineListItem)v).getText1().getText());
                                    v.setBackgroundColor(defaultBackgroundColor);
                                }
                            }

                        }
                    }, 300);




/*
                    mainListView.post(new Runnable() {
                        @Override
                        public void run() {
                            ; // 選択する行
                            // listView.setItemChecked(pos, true)  //singleChoiceの時はこれも呼ぶ
                            mainListView.smoothScrollToPosition(randomIndex);
                            adapter.notifyDataSetChanged();

                            // ランダム選択されたViewの背景色を変更
                            View randomSelectedView = mainListView.getChildAt(randomIndex);
                            if (randomSelectedView != null) {
                                randomSelectedView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, android.R.color.holo_red_light));
                            }
                        }
                    });
*/





                }

            }
        });

    }

    @Override // Menuのメソッド実装、res/menuフォルダ内のどのxmlを使用するかを指定
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override // Menuのメソッド実装、メニューのオプション項目の選択イベントを判断し適切な処理を実行
    public boolean onOptionsItemSelected(MenuItem item) {
        final String METHOD_NAME = "onOptionsItemSelected";
        Log.d(METHOD_NAME, "entering");

        int id = item.getItemId();
        if (id == R.id.action_add) {
            Log.d(CLASS_NAME + "." + METHOD_NAME, "■id：R.id.action_add");

            Intent intent = new Intent(this, SubActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_agenda) {
            // TODO: 2015/11/27 アイコンがイケてないかも・・・ 
            Log.d(CLASS_NAME + "." + METHOD_NAME, "■id：R.id.action_agenda");

            // 初回query発行と同様の内容を取得
            // TODO: 2015/11/26 MainActivityのAdapterから内容を取得できないのか・・・？ [NGパターン]Cursor c = this.adapter.getCursor();　Failed to read row 0, column -1 from a CursorWindow which has 12 rows, 3 columns.　java.lang.IllegalStateException: Couldn't read row 0, col -1 from CursorWindow.  Make sure the Cursor is initialized correctly before accessing data from it.
            String[] projection = {
                    ItemContract.Items._ID,
                    ItemContract.Items.COL_TITLE,
                    ItemContract.Items.COL_BODY
            };
            Cursor c = getContentResolver().query(
                    ItemContentProvider.CONTENT_URI,
                    projection,
                    null,
                    null,
                    ItemContract.Items.COL_UPDATED + " DESC"
            );

            // アジェンダテキスト作成
            StringBuilder agendaSb = new StringBuilder();
            boolean next = c.moveToFirst();
            while (next) {
                agendaSb.append("■")
                        .append(c.getString(c.getColumnIndex(ItemContract.Items.COL_TITLE)))
                        .append(BR)
                        .append(c.getString(c.getColumnIndex(ItemContract.Items.COL_BODY)))
                        .append(BR + BR);

                // 次のレコード
                next = c.moveToNext();
            }
            Log.d(CLASS_NAME + "." + METHOD_NAME, "■agenda：" + agendaSb.toString());

            Intent intent = new Intent(this, AgendaActivity.class);
            intent.putExtra(EXTRA_AGENDA, agendaSb.toString());
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // LoaderManagerのメソッド実装、初回のクエリ実行内容を設定する
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
