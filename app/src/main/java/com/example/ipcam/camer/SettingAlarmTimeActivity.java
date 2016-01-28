package com.example.ipcam.camer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.example.ipcam.camer.adpter.AlarmTimeAdpter;
import com.example.ipcam.camer.entity.AlarmTime;
import com.example.ipcam.camer.util.SharedPrefer;
import com.example.ipcam.camer.util.Util;
import com.example.ipcam.camer.view.XListView;

import java.util.ArrayList;
import java.util.List;


public class SettingAlarmTimeActivity extends BaseActivity implements
        XListView.IXListViewListener {
    private XListView listView;
    private AlarmTimeAdpter adpter;
    private List<AlarmTime> list = new ArrayList<>();

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            listView.stopRefresh();
            listView.stopLoadMore();
            listView.setRefreshTime(Util.getNowTime());
            adpter.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingoverturn);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", R.drawable.right_add_selector, "");
        setTitle("报警时间");
        initData();
        listView = (XListView) findViewById(R.id.listview);
        listView.setIXListViewListener(this);
        listView.setPullLoadEnable(false);
        listView.setPullRefreshEnable(true);
        adpter = new AlarmTimeAdpter(this, list, handler);
        listView.setAdapter(adpter);


    }

    private void initData() {
        list.clear();
        List<AlarmTime> sha = SharedPrefer.GetAlarmTime(this);
        list.addAll(sha);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            initData();
            adpter.notifyDataSetChanged();
        }
    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        if (titleBar == TitleBar.RIGHT) {
            Intent intent = new Intent(this, Alarm_time_addActivity.class);

            startActivityForResult(intent, 100);
        } else
            back();
    }

    @Override
    public void onRefresh() {
        initData();
        handler.sendEmptyMessage(0);

    }

    @Override
    public void onLoadMore() {

    }
}
