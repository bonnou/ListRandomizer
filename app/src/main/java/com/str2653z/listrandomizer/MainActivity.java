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
import android.view.ContextMenu;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.TwoLineListItem;

import com.str2653z.listrandomizer.custom.view.CustomTwoLineListItem;

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


    // TODO: ソート順序はアプリで記憶したいがひとまずカラムの表示順序の昇順
    public static String loaderOrderBy = ItemContract.Items.COL_ORDERNUM + " ASC";



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
                ItemContract.Items.COL_TITLE,// TODO:デバッグ用：COL_ORDERNUM
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

        // ビュー単推し時
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

        // ビュー長推し時、コンテキストメニューを登録（onCreateContextMenu、onContextItemSelected参照）
        // ※trueを返すだけのonItemLongClickを書きかけ実装してmainListView.setOnItemLongClickListenerしてたから一生コンテキストメニューがでないどころだった
        // TODO: なんでregisterForContextMenuメソッドの背景色が黄色くなる？？？動いてるからいいけど・・・
        registerForContextMenu(mainListView);


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

    /*
     * ContextMenu生成時に呼び出されるメソッド.
     * 今回の設定ではListViewのindex=0,1はContextMenuが表示されないよう制御.
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, view, menuInfo);

        // ListViewにキャストする。
        ListView listView = (ListView) view;
        // AdapterContextMenuInfoにキャストする。
        AdapterView.AdapterContextMenuInfo contextMenuInfo= (AdapterView.AdapterContextMenuInfo) menuInfo;

        boolean sortByOrderNumFlg = false;
        if ( loaderOrderBy.equals(ItemContract.Items.COL_ORDERNUM + " ASC") ) {
            sortByOrderNumFlg = true;
        }

        menu.setHeaderTitle("ContextMenu");
        menu.add("削除（未実装）");
        // 表示位置移動は固定順序表示時のみ可能
        if (sortByOrderNumFlg) {
            // 一番上のViewは表示位置を上に移動できない
            if (contextMenuInfo.position != 0) {
                menu.add("表示位置を1つ上へ（未実装）");
            }
            // 一番下のViewは表示位置を下に移動できない
            if (contextMenuInfo.position != mainListView.getCount() - 1) {
                menu.add("表示位置を1つ下へ（未実装）");
            }
        }
    }

    /*
     * メニューがクリックされた際に呼び出されるメソッド.
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        // menu名を取得
        String menuName = item.getTitle().toString();
        // ContextMenuInfoを取得
        AdapterView.AdapterContextMenuInfo detailInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        // TODO:実装
        if ("削除".equals(menuName)) {
            Log.d("onContextItemSelected", detailInfo .position + "の" + menuName + "処理を実行");

        } else if ("表示位置を1つ上へ".equals(menuName)) {
            Log.d("onContextItemSelected", detailInfo .position + "の" + menuName + "処理を実行");

        } else if ("表示位置を1つ下へ".equals(menuName)) {
            Log.d("onContextItemSelected", detailInfo .position + "の" + menuName + "処理を実行");

        }
        return true;
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
        } else if ( id == R.id.menuSortOrderNum ) {
            // TODO:Loader再作成でクエリ発行がされるがレコード数が多い場合に問題ないか
            // loader用のORDER BY句を変更しonCreateLoaderを呼ぶ
            loaderOrderBy = ItemContract.Items.COL_ORDERNUM + " ASC";
            getLoaderManager().restartLoader(0, null, this);

        } else if ( id == R.id.menuSortNewest ) {
            // loader用のORDER BY句を変更しonCreateLoaderを呼ぶ
            loaderOrderBy = ItemContract.Items.COL_UPDATED + " DESC";
            getLoaderManager().restartLoader(0, null, this);

        } else if ( id == R.id.menuSortOldest ) {
            // loader用のORDER BY句を変更しonCreateLoaderを呼ぶ
            loaderOrderBy = ItemContract.Items.COL_UPDATED + " ASC";
            getLoaderManager().restartLoader(0, null, this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override // LoaderManagerのメソッド実装、初回のクエリ実行内容を設定する
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String METHOD_NAME = "onCreateLoader";
        Log.d(METHOD_NAME, "entering");

        // SELECT句
        String[] projection = {
                ItemContract.Items._ID,
                ItemContract.Items.COL_TITLE,
                ItemContract.Items.COL_UPDATED,
                ItemContract.Items.COL_ORDERNUM

        };

        // 初回query発行
        return new CursorLoader(
                this,
                ItemContentProvider.CONTENT_URI,
                projection,
                null,
                null,
                loaderOrderBy
        );
    }

    @Override // LoaderManagerのメソッド実装、ContentProviderからデータが帰ってきた時に実行される
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final String METHOD_NAME = "onLoadFinished";
        Log.d(METHOD_NAME, "entering");
        // 帰ってきたデータでAdapterを更新
        adapter.swapCursor(data);
    }

    @Override // LoaderManagerのメソッド実装、何らかの理由でLoaderがリセットされた時に実行される
    public void onLoaderReset(Loader<Cursor> loader) {
        final String METHOD_NAME = "onLoaderReset";
        Log.d(METHOD_NAME, "entering");
        // ひとまずnull
        adapter.swapCursor(null);
    }
}
