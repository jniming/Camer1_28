package com.example.ipcam.camer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.ipcam.camer.Listener.PictureListener;
import com.example.ipcam.camer.db.DeviceManager;
import com.example.ipcam.camer.entity.AlarmMsg;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.util.FileHelper;
import com.example.ipcam.camer.util.Manager;
import com.example.ipcam.camer.util.SharedPrefer;
import com.example.ipcam.camer.util.Util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hsl.p2pipcam.nativecaller.DeviceSDK;


public class DeviceAlarmActivity extends Activity implements OnClickListener,
        PictureListener {
    private TextView ipcName, alarmMsg, timemsg;
    private LinearLayout check, dimss;
    private long userid;
    private String msg_str;
    public static boolean isvis = false;
    private SoundPool sound;
    private int music;
    private Vibrator vibrator;
    private Bitmap bitmap;
    private ImageView imageView;
    private String imgstr;
    private IpcDevice device;
    private int infotype;
    private String time;
    private MediaPlayer mediaPlayer;
    private List<String> list = new ArrayList<String>();
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            if (bitmap != null)
                imageView.setImageBitmap(bitmap);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Manager.getIntence(this).setPictureListener(this);
        isvis = true;
        setContentView(R.layout.activity_alarm);
        initView();
        init();

    }

    public void init() {
        mediaPlayer = MediaPlayer.create(this, R.raw.sms_received3);
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {100, 400, 100, 400};
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        });
        HashMap<String, Object> map = SharedPrefer.GetAppSetingData(this);
        int time = (int) map.get(SharedPrefer.ALARM_T);         //报警时间
        boolean zd = (boolean) map.get(SharedPrefer.ALARM_Z);  //报警震动
        boolean ls = (boolean) map.get(SharedPrefer.ALARM_LS);  //报警声音
        if (ls) {
            mediaPlayer.start();
        }
        if (zd) {
            vibrator.vibrate(pattern, 2);
        }
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                mediaPlayer.stop();
                vibrator.cancel();
            }
        };
        timer.schedule(task, time * 1000);
    }

    private void initView() {
        DeviceSDK.getDeviceParam(userid, 0x270E);
        Intent intent = getIntent();
        userid = intent.getLongExtra("userid", -1);
        int type = intent.getIntExtra("nType", -1);
        infotype = intent.getIntExtra("type", -1);
        String pic = intent.getStringExtra("pic");
        msg_str = Util.GetAlarmMsg(type);
        device = DeviceManager.getInstence(this).QueryDevice(userid);
        getpic();
        ipcName = (TextView) findViewById(R.id.alarm_ipcname);
        timemsg = (TextView) findViewById(R.id.alarm_time);
        alarmMsg = (TextView) findViewById(R.id.alarm_msg);
        check = (LinearLayout) findViewById(R.id.alarm_check);
        dimss = (LinearLayout) findViewById(R.id.alarm_dimss);
        imageView = (ImageView) findViewById(R.id.alarm_img);
        check.setOnClickListener(this);
        dimss.setOnClickListener(this);
        if (device == null) {
            alarmMsg.setText(msg_str);
            ipcName.setText("未知设备");
        } else {
            if (!"".equals(device.getName())) {
                alarmMsg.setText(msg_str);
                ipcName.setText(device.getName());
            }
        }
        time = Util.getNowTime();
        timemsg.setText(time);
    }

    public void getpic() {
        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 0;
                while (i <= 2) {
                    DeviceSDK.getDeviceParam(userid, 0x270E);
                    try {
                        Thread.sleep(800);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                    i++;
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (list != null || list.size() != 0) {
            for (String str : list) {
                if (device != null) {
                    AlarmMsg alarmmsg = new AlarmMsg(0, msg_str, time,
                            device.getDeviceid(), str, infotype);
                    DeviceManager.getInstence(DeviceAlarmActivity.this).SaveMsg(alarmmsg);
                }
            }
        }


    }

    public void stopsound() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        vibrator.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        vibrator.cancel();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        stopsound();
        if (id == check.getId()) {
            isvis = false;
            Intent intent = new Intent(this, PlayDeviceActivity.class);
            intent.putExtra("userid", userid);
            startActivity(intent);
            finish();
        } else if (id == dimss.getId()) {
            isvis = false;
            finish();
            // moveTaskToBack(true);
        }
    }

    public void save(Bitmap bitmap) {
        String str1 = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                + ".jpg";
        String str = "";
        if (device != null) {
            str = device.getDeviceid();
        }
        String uri = FileHelper.IMAGE_PATH + "/" + str;
        File localFile = new File(uri);
        if (!localFile.exists())
            localFile.mkdirs();
        String str2 = uri + "/" + "_" + str1;
        BufferedOutputStream bos = null;
        try {
            bos = new BufferedOutputStream(new FileOutputStream(str2));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        list.add(str2);
        handler.sendEmptyMessage(0);
    }

    @Override
    public void CallBack_RecordPicture(long userid, byte[] buff, int len) {
        try {
            imgstr = new String(buff, "ISO-8859-1");
            bitmap = Util.decodeBitmap(imgstr);
            save(bitmap);

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
