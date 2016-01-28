package com.example.ipcam.camer;


import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ipcam.camer.Listener.SettingsListener;
import com.example.ipcam.camer.adpter.P2pAudioAdpter;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.entity.P2pAudio;
import com.example.ipcam.camer.util.Util;
import com.example.ipcam.camer.view.CustomDialog;
import com.example.ipcam.camer.view.XListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hsl.p2pipcam.nativecaller.DeviceSDK;


public class P2pAudioActivity extends BaseActivity implements XListView.IXListViewListener, SettingsListener {
    private XListView listView;
    private IpcDevice ipcDevice;
    private List<P2pAudio> audiolist = new ArrayList<P2pAudio>();
    private PopupWindow menupop;
    private CustomDialog.Builder dialog;
    private boolean gl_msg = true;
    private TextView text;
    private P2pAudioAdpter adpter;
    private ProgressBar progressBar;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            listView.stopRefresh();
            listView.stopLoadMore();
            listView.setRefreshTime(Util.getNowTime());


            progressBar.setVisibility(View.GONE);
            if (audiolist.size() == 0) {
                text.setVisibility(View.VISIBLE);
            } else {
                text.setVisibility(View.GONE);
            }

            if (msg.what == 3) {
                if (gl_msg) {
                    adpter.setIsvisb(true);
                    setNaView(R.drawable.left_back_selector, "", 0, "", 0, "取消", 0, "");
                    gl_msg = false;
                    menupop.showAtLocation(listView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                } else {
                    adpter.setIsvisb(false);
                    gl_msg = true;
                    setNaView(R.drawable.left_back_selector, "", 0, "", 0, "管理", 0, "");
                    menupop.dismiss();
                }
                adpter.notifyDataSetChanged();
            } else if (msg.what == 5) {
                P2pAudio audio = (P2pAudio) msg.obj;
                audiolist.add(audio);
                adpter.notifyDataSetChanged();

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeMessages(5);
        handler.removeMessages(0);
        handler.removeMessages(1);
        handler.removeMessages(2);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ipc_p2p_audio_list);
        setTitle("远程录像");
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "管理", 0, "");
        listView = (XListView) findViewById(R.id.alarm_listivew);
        text = (TextView) findViewById(R.id.reminder_text);
        progressBar = (ProgressBar) findViewById(R.id.p2p_audio_loading);
        ipcDevice = (IpcDevice) getIntent().getSerializableExtra("device");
        listView.setPullLoadEnable(false);
        listView.setIXListViewListener(this);
        adpter = new P2pAudioAdpter(audiolist, this);
        listView.setAdapter(adpter);
        menuPop();
        searchAudio();
        dialog = new CustomDialog.Builder(this);
        dialog.setTitle("提示");
        dialog.setMessage("确定删除所选中项目?");
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                boolean flg = false;
                for (P2pAudio msg : audiolist) {
                    if (msg.ischeck()) {  //删除前端文件
                        flg = true;
                    }
                }
                if (flg)
                    searchAudio();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Timer time = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        time.schedule(task, 3000);

    }

    @SuppressWarnings("unchecked")
    private void searchAudio() {
        audiolist.clear();
        DeviceSDK.getRecordVideo(ipcDevice.getUserid(), 2015, 10, 11, 2016, 1, 12);

    }

    public void menuPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.msg_menu_pop, null);
        LinearLayout delect_lt = (LinearLayout) view.findViewById(R.id.delect_layout);
        LinearLayout all = (LinearLayout) view.findViewById(R.id.all_layout);
        final TextView delect = (TextView) view.findViewById(R.id.delect);
        TextView select_all = (TextView) view.findViewById(R.id.all_select);
        menupop = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        delect_lt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.create().show();
            }
        });
        all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (P2pAudio msg : audiolist) {
                    msg.setIscheck(true);
                }
                handler.sendEmptyMessage(0);
            }
        });


    }

    @Override
    public void onRefresh() {
        searchAudio();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        if (titleBar == TitleBar.RIGHT) {
            handler.sendEmptyMessage(3);

        } else
            back();
    }

    @Override
    public void callBack_getParam(long UserID, long nType, String param) {
        //新接口返回
        Log.d("zjm", "前端录像新接口查询");
        try {
            JSONObject json = new JSONObject(param);
            int file_count = (int) json.get("file_count");
            int file_no = (int) json.get("file_no");
            int file_size = (int) json.get("file_size");
            int file_type = (int) json.get("file_type");  //0视屏,1图片
            String file_date = (String) json.get("file_date");
            String file_name = (String) json.get("file_name");
            P2pAudio audio = new P2pAudio(file_name, file_date, file_count, file_type, file_no, file_size);
            audio.setIscheck(false);
            Message msg = handler.obtainMessage();
            msg.what = 5;
            msg.obj = audio;
            handler.sendMessage(msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void callBack_setParam(long UserID, long nType, int nResult) {

    }

    @Override
    public void recordFileList(long UserID, int filecount, String fname, String strDate, int size) {
        Log.d("zjm", "前端录像查询");
    }
}

