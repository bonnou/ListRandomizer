package com.str2653z.listrandomizer;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class AgendaActivity extends AppCompatActivity {

    private TextView agendaText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // ツールバーに戻るボタンを追加
        toolbar.setNavigationIcon(android.R.drawable.ic_menu_revert);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 無条件でActivity終了
                finish();
            }
        });

        // ツールバーのタイトルを設定
        toolbar.setTitle("Agenda"); // TODO: 2015/11/26 効いていない・・・？ 

        // MainActivityにて作成したアジェンダテキストを取得
        Intent intent = getIntent();
        String agendaStr = intent.getStringExtra(MainActivity.EXTRA_AGENDA);

        // layoutの要素を取得
        agendaText = (TextView) findViewById(R.id.agendaText);  // xmlにて編集不可に設定している
        agendaText.setText(agendaStr);

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

}
