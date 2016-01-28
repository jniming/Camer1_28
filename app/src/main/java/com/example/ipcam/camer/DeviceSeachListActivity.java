package com.example.ipcam.camer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ipcam.camer.Listener.SeachListener;
import com.example.ipcam.camer.adpter.IpcSearchListAdpter;
import com.example.ipcam.camer.db.DeviceManager;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.util.Manager;
import com.example.ipcam.camer.util.Util;
import com.example.ipcam.camer.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import hsl.p2pipcam.nativecaller.DeviceSDK;

public class DeviceSeachListActivity extends BaseActivity implements SeachListener, XListView.IXListViewListener {

    private List<IpcDevice> list = null;
    private List<IpcDevice> ipclist;
    private IpcSearchListAdpter adpter;
    private XListView xListView;
    private ProgressBar loading;
    private TextView text;
    private List<IpcDevice> searchList=new ArrayList<>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            xListView.stopRefresh();
            xListView.stopLoadMore();
            xListView.setRefreshTime(Util.getNowTime());
            xListView.setVisibility(View.VISIBLE);
            loading.setVisibility(View.GONE);
//                isAdd(list);
            Log.d("zjm", "搜索到的ipc数量" + list.size());
            IpcDevice device = (IpcDevice) msg.obj;
            list.add(device);
            text.setVisibility(View.GONE);
            adpter.notifyDataSetChanged();
            if (list.size() == 0) {
                text.setVisibility(View.VISIBLE);
            }
        }

    };

    /**
     * 检查是否该ipc已添加
     *
     * @param ipl
     */
    public void isAdd(List<IpcDevice> ipl) {


        if (ipclist != null && ipl != null) {
            for (IpcDevice cd : ipclist) {
                for (IpcDevice ipcDevice : ipl) {
                    if (ipcDevice.getDeviceid().equals(cd.getDeviceid())) {
                        ipcDevice.setAdd(true);
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.device_list_chang_activity);
        setTitle(getResources().getString(R.string.ipc_list));
        Manager.getIntence(this).setSearchListener(this);
        list = new ArrayList();
        xListView = (XListView) findViewById(R.id.ipc_search_xlistview);
        xListView.setPullLoadEnable(false);
        xListView.setIXListViewListener(this);
        loading = (ProgressBar) findViewById(R.id.loading);
        text = (TextView) findViewById(R.id.ipc_search_text);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", R.drawable.right_search_selector, "");
        ipclist = DeviceManager.getInstence(this).GetIPCListDevice();
        Log.d("zjm", "已添加的ipc数量" + ipclist.size());
        SearchDevice();
        adpter = new IpcSearchListAdpter(list, this);
        xListView.setAdapter(adpter);

        xListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                IpcDevice device = (IpcDevice) list.get(position - 1);
                Intent intent = new Intent();
                intent.putExtra("device", device);
                DeviceSeachListActivity.this.setResult(100, intent);
                finish();
            }
        });
//        Timer Timer = new Timer();
//        TimerTask TimerTask = new TimerTask() {
//            @Override
//            public void run() {
//                handler.sendEmptyMessage(0);
//            }
//        };
//        Timer.schedule(TimerTask, 5000);

    }


    public void SearchDevice() {
        list.clear();
        DeviceSDK.searchDevice(); // 初始化搜索


    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        if (titleBar == TitleBar.RIGHT) {
            xListView.setVisibility(View.GONE);
            loading.setVisibility(View.VISIBLE);
            SearchDevice();
        } else
            finish();
    }


    @Override
    public void callBack_SeachData(String Deviceinfo) {
        boolean temp = false;
        try {
            JSONObject jsonObject = new JSONObject(Deviceinfo);
            String deviceid = jsonObject.getString("DeviceID");
            IpcDevice device = new IpcDevice();
            device.setMac(jsonObject.getString("Mac"));
            device.setName(jsonObject.getString("DeviceName"));
            device.setDeviceid(deviceid);
            device.setIp(jsonObject.getString("IP"));
            device.setPort(jsonObject.getInt("Port"));
            device.setAdd(false);
            if (searchList.size() == 0) {
                temp = false;
            } else {
                for (IpcDevice ipc : searchList) {
                    if (ipc.getDeviceid().equals(deviceid)) {
                        temp = true;
                    }

                }
            }
            if (!temp) {
                if (!checkIsAdd(deviceid)) {
                    device.setAdd(false);
                } else
                    device.setAdd(true);

                searchList.add(device);
                Message msg = handler.obtainMessage();
                msg.obj = device;
                msg.what = 0;
                handler.sendMessage(msg);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public boolean checkIsAdd(String deviceid) {
        for (IpcDevice device : ipclist) {
            if (device.getDeviceid().equals(deviceid))
                return true;

        }

        return false;
    }


    @Override
    public void onRefresh() {
        SearchDevice();
    }

    @Override
    public void onLoadMore() {

    }

}
