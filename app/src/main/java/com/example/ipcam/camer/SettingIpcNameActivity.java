package com.example.ipcam.camer;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ipcam.camer.Listener.NameListener;
import com.example.ipcam.camer.db.DeviceManager;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.util.Manager;
import com.example.ipcam.camer.util.Util;
import com.example.ipcam.camer.view.LoadingDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import hsl.p2pipcam.nativecaller.DeviceSDK;


public class SettingIpcNameActivity extends BaseActivity implements
        NameListener {
    private EditText text;
    private IpcDevice device;
    private List<IpcDevice> list;
    private LoadingDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settingname);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", R.drawable.right_finsh_selector, "");
        Manager.getIntence(this).setNameListener(this);
        setTitle("名称修改");
        initData();
        text = (EditText) findViewById(R.id.setting_name);
        text.setHint(device.getName());
    }

    private void initData() {
        device = (IpcDevice) getIntent().getSerializableExtra("device");
        list = DeviceManager.getInstence(this).GetIPCListDevice();

        dialog = new LoadingDialog(this);
        dialog.setTitle("修改中..");
    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        if (TitleBar.RIGHT == titleBar) {
            String stName = text.getText().toString().trim();
            if (stName.isEmpty()) {
                Toast.makeText(this, "名称不能为空!", Toast.LENGTH_SHORT).show();
                return;
            } else {
                dialog.show();
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("alias", stName);
                    int temp = DeviceSDK.setDeviceParam(device.getUserid(),
                            Util.SET_NAME, jsonObject.toString());
                    // JSONObject param1 = new JSONObject();
                    // param1.put("param", 14);
                    // param1.put("value", stName);
                    // long temp2 = DeviceSDK.setDeviceParam(device.getUserid(),
                    // 0x2026, param1.toString());
                    if (temp > 0) {
                        Toast.makeText(this, "设置成功!", Toast.LENGTH_SHORT).show();
                        device.setName(stName);
                        DeviceManager.getInstence(SettingIpcNameActivity.this)
                                .updateIPC_name(device);
                    } else {
                        Toast.makeText(this, "设置失败!", Toast.LENGTH_SHORT).show();
                    }
                    finish();

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    finish();
                    Log.d("zjm", e.getMessage());
                }
            }

        } else {
            finish();
        }
    }

    @Override
    public void callBack_getParam(long UserID, long nType, String param) {
        System.out.println("userid-->" + UserID + "---ntype-->" + nType);

    }

    @Override
    public void callBack_setParam(long UserID, long nType, int nResult) {
        System.out.println("userid-->" + UserID + "---ntype-->" + nType);
    }

}
