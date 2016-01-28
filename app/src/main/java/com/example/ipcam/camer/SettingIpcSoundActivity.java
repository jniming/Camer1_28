package com.example.ipcam.camer;

import android.os.Bundle;
import android.view.View;


public class SettingIpcSoundActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settinglan);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", R.drawable.right_finsh_selector, "");
        setTitle("内网配置");
        initData();
        initView();

    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        back();
    }

    private void initView() {
    }

    private void initData() {
    }


}
