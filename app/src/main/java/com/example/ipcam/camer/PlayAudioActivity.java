package com.example.ipcam.camer;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ipcam.camer.Listener.GraphicListener;
import com.example.ipcam.camer.Listener.PlayListener;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.util.AudioPlayer;
import com.example.ipcam.camer.util.CustomAudioRecorder;
import com.example.ipcam.camer.util.CustomBuffer;
import com.example.ipcam.camer.util.CustomBufferData;
import com.example.ipcam.camer.util.CustomBufferHead;
import com.example.ipcam.camer.util.Manager;
import com.example.ipcam.camer.util.MyRender;
import com.example.ipcam.camer.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import hsl.p2pipcam.nativecaller.DeviceSDK;


public class PlayAudioActivity extends BaseActivity implements PlayListener,
        MyRender.RenderListener, CustomAudioRecorder.AudioRecordResult, OnClickListener, GraphicListener {

    private long userid;
    private CustomAudioRecorder customAudioRecorder;
    private CustomBuffer AudioBuffer;
    private AudioPlayer audioPlayer;
    private MyRender myRender;
    private LinearLayout progressLayout, down_lt;
    private GLSurfaceView glSurfaceView1;
    private FrameLayout suff_lt;
    private ImageView full_img;
    private boolean flg = false, audio_isdio = false;
    private LinearLayout capture, audio, jiant, luminance;
    private SoundPool sound;
    private int music, resolution = 0;
    private long start, end;
    private TextView audioing;
    private PopupWindow luminancepop;
    private int luminanceP, contrastP; // 亮度和对比度的值
    private LoadTask task;
    private boolean isfrst = true;
    private boolean resoult = true;  //false为标清,true为高清
    private FrameLayout play_audio_bottom;
    private boolean isshow = true;
    private String filename;
    private CheckBox left_right, up_down;

    private int filp;   //翻转 0是正常,

    private ImageView pic_btn, mic_btn, sound_btn, hd_btn, vidio_btn;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isshow) {
                play_audio_bottom.setVisibility(View.VISIBLE);
            } else {
                play_audio_bottom.setVisibility(View.GONE);
            }
            if (msg.what == 2) {
                switch (filp) {
                    case 0:
                        left_right.setChecked(false);
                        up_down.setChecked(false);
                        break;
                    case 1:
                        left_right.setChecked(true);
                        up_down.setChecked(false);
                        break;
                    case 2:
                        left_right.setChecked(false);
                        up_down.setChecked(true);
                        break;
                    case 3:
                        left_right.setChecked(true);
                        up_down.setChecked(true);
                        break;
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playdevice);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", 0, "");
        setTitle("实时观看");
        Manager.getIntence(this).setPlayListener(this);
        Manager.getIntence(this).setGraphicListener(this);
        userid = getIntent().getLongExtra("userid", 0);
        filename = getIntent().getStringExtra("filename");
        customAudioRecorder = new CustomAudioRecorder(this);
        AudioBuffer = new CustomBuffer();
        audioPlayer = new AudioPlayer(AudioBuffer);
        DeviceSDK.getDeviceParam(userid, 0x2025); // 获取图象那个参数
        initView();
        task = new LoadTask();
        task.execute();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        System.out.println("change");

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 检测屏幕的方向：纵向或横向
        if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // 当前为横屏， 在此处添加额外的处理代码
            down_lt.setVisibility(View.GONE);
            PlayAudioActivity.this.titleShow(true);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            suff_lt.setLayoutParams(lp);

        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 当前为竖屏， 在此处添加额外的处理代码
            down_lt.setVisibility(View.VISIBLE);
            PlayAudioActivity.this.titleShow(false);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, Util.dip2px(this,
                    250));

            suff_lt.setLayoutParams(lp);
        }
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        closePop();
        task = null;
        isfrst = true;

    }

    private void initView() {
        glSurfaceView1 = (GLSurfaceView) findViewById(R.id.glsurfaceview1);
        progressLayout = (LinearLayout) findViewById(R.id.progressLayout1);
        jiant = (LinearLayout) findViewById(R.id.jianting);
        audio = (LinearLayout) findViewById(R.id.audio_layout);
        capture = (LinearLayout) findViewById(R.id.capture_img);
        luminance = (LinearLayout) findViewById(R.id.luminance);

        full_img = (ImageView) findViewById(R.id.full_srcee);
        up_down = (CheckBox) findViewById(R.id.cz_fz_sx);
        left_right = (CheckBox) findViewById(R.id.cz_sp_fz);

        pic_btn = (ImageView) findViewById(R.id.pic_btn);
        mic_btn = (ImageView) findViewById(R.id.mic_btn);
        vidio_btn = (ImageView) findViewById(R.id.vidio_btn);
        sound_btn = (ImageView) findViewById(R.id.sound_btn);
        hd_btn = (ImageView) findViewById(R.id.hd_btn);

        down_lt = (LinearLayout) findViewById(R.id.down_lt);
        suff_lt = (FrameLayout) findViewById(R.id.suface_framlt);
        audioing = (TextView) findViewById(R.id.audioing_text);
        play_audio_bottom = (FrameLayout) findViewById(R.id.play_audio_bottom_layout);

        myRender = new MyRender(glSurfaceView1);
        myRender.setListener(this);
        glSurfaceView1.setRenderer(myRender);
        audioing.setVisibility(View.GONE);
        sound = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5); // 设置一个声音点
        music = sound.load(this, R.raw.photoshutter, 1); // 加载raw文件内的声音

        glSurfaceView1.setOnClickListener(this);
        luminance.setOnClickListener(this);
        full_img.setOnClickListener(this);
        capture.setOnClickListener(this);
        audio.setOnClickListener(this);
        jiant.setOnClickListener(this);

        up_down.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && left_right.isChecked()) {
                    fanzhuan(3);
                } else if (!isChecked && left_right.isChecked()) {
                    fanzhuan(1);
                } else if (isChecked && !left_right.isChecked()) {
                    fanzhuan(1);
                } else if (!isChecked && !left_right.isChecked()) {
                    fanzhuan(0);
                }
            }
        });
        left_right.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked && up_down.isChecked()) {
                    fanzhuan(3);
                } else if (!isChecked && up_down.isChecked()) {
                    fanzhuan(2);
                } else if (isChecked && !up_down.isChecked()) {
                    fanzhuan(2);
                } else if (!isChecked && !up_down.isChecked()) {
                    fanzhuan(0);
                }

            }
        });
    }

    public void fanzhuan(int id) {
        JSONObject obj1 = new JSONObject();
        try {
            obj1.put("param", 5);
            obj1.put("value", id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        DeviceSDK.setDeviceParam(userid, 0x2026, obj1.toString());
    }

    private Handler frushHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressLayout.setVisibility(View.GONE);
        }
    };

    public void cameraGetParamsResult(long userid, String cameraParams) {
        // TODO Auto-generated method stub

    }

    public void callBackAudioData(long userID, byte[] pcm, int size) {
        if (userID == userid) {
            CustomBufferHead head = new CustomBufferHead();
            CustomBufferData data = new CustomBufferData();
            head.length = size;
            head.startcode = 0xff00ff;
            data.head = head;
            data.data = pcm;
            if (audioPlayer.isAudioPlaying())
                AudioBuffer.addData(data);
        }

    }

    public void callBackVideoData(long userID, byte[] data, int type, int size) {

    }

    public void smartAlarmCodeGetParamsResult(long userid, String params) {
        Log.d("zjm", params);

    }

    public void smartAlarmNotify(long userid, String message) {
        // TODO Auto-generated method stub

    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... arg0) {
            DeviceSDK.setRender(userid, myRender);
            DeviceSDK.startPlayRecord(userid, filename, 0);

            try {
                JSONObject obj = new JSONObject();
                obj.put("param", 13);
                obj.put("value", 1024);
                DeviceSDK.setDeviceParam(userid, 0x2026, obj.toString());
                JSONObject obj1 = new JSONObject();
                obj1.put("param", 6);
                obj1.put("value", 15);
                DeviceSDK.setDeviceParam(userid, 0x2026, obj1.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

    }

    public void initComplete(int size, int width, int height) {
        // TODO Auto-generated method stub
        frushHandler.sendEmptyMessage(0);
    }

    public void takePicture(byte[] imageBuffer, int width, int height) {
        // TODO Auto-generated method stub

    }

    public void AudioRecordData(byte[] data, int len) {
        // TODO Auto-generated method stub
        DeviceSDK.SendTalkData(userid, data, len);
    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        // TODO Auto-generated method stub
        finish();
    }

    private void vidio() {
        if (!audio_isdio) {
            System.out.println("..................");
            int temp = IpcDevice.StartRecord(this, userid, 480, 800, 20);
            System.out.println(temp);
            if (temp > 0) {
                System.out.println("..................");
                audioing.setVisibility(View.VISIBLE);
                audio.setBackgroundResource(R.color.gray_2);
                vidio_btn.setBackgroundResource(R.drawable.vidio_btn_pressed);
                start = System.currentTimeMillis();
                audio_isdio = true;
            }
        } else {
            end = System.currentTimeMillis();
            long time = end - start;
            if (time > 10 * 1000) {
                int isrecord = IpcDevice.StopRecord(userid);
                if (isrecord > 0) {
                    audio_isdio = false;
                    audioing.setVisibility(View.VISIBLE);
                    vidio_btn.setBackgroundResource(R.drawable.vidio_btn_nomal);
                    audio.setBackgroundResource(R.color.gray);
                    audioing.setVisibility(View.GONE);
                    Toast.makeText(PlayAudioActivity.this, "录像成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(PlayAudioActivity.this, "录像不能低于十秒", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == full_img.getId()) {
            if (!flg) {
                flg = true;

                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            } else {
                flg = false;
                if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT);
                }
            }
        } else if (capture.getId() == id) {
            sound.play(music, 1, 1, 0, 0, 1);
            IpcDevice.capturePicture(this, userid);
            Toast.makeText(this, "抓拍成功", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.audio_layout) { // 录像
            vidio();

        } else if (id == luminance.getId()) {
            if (resoult) {
                IpcDevice.startPlayStream(userid, 0);
                resoult = false;
            } else {
                IpcDevice.startPlayStream(userid, 1);
                resoult = true;
            }


        } else if (id == glSurfaceView1.getId()) {
            handler.sendEmptyMessage(0);
            if (isshow) {
                isshow = false;
            } else
                isshow = true;

        } else if (id == pic_btn.getId()) {
            sound.play(music, 1, 1, 0, 0, 1);
            IpcDevice.capturePicture(this, userid);
            Toast.makeText(this, "抓拍成功", Toast.LENGTH_SHORT).show();


        } else if (id == mic_btn.getId()) {
            return;
        } else if (id == sound_btn.getId()) {
            return;
        } else if (id == hd_btn.getId()) {
            if (resoult) {
                IpcDevice.startPlayStream(userid, 0);
                resoult = false;
            } else {
                IpcDevice.startPlayStream(userid, 1);
                resoult = true;
            }
        } else if (id == vidio_btn.getId()) {
            vidio();

        }

    }


    @SuppressWarnings("unused")
    private void initluminancepop(final int id) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(this).inflate(
                R.layout.luminance_pop, null);
        final SeekBar seek = (SeekBar) layout.findViewById(R.id.luminance_seek);
        final TextView tv = (TextView) layout.findViewById(R.id.luminance_tv);
        seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (id == 1) { // 亮度
                    try {
                        seek.setProgress(seekBar.getProgress());
                        PlayAudioActivity.this.luminanceP = seekBar
                                .getProgress();
                        tv.setText(PlayAudioActivity.this.luminanceP + "/255");
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("param", 1);
                        jsonObject.put("value", seekBar.getProgress());
                        DeviceSDK.setDeviceParam(userid, 0x2026,
                                jsonObject.toString());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else { // 对比度
                    try {
                        JSONObject object = new JSONObject();
                        PlayAudioActivity.this.contrastP = seekBar
                                .getProgress();
                        seek.setProgress(PlayAudioActivity.this.contrastP);
                        tv.setText(seekBar.getProgress() + "/255");
                        object.put("param", 2);
                        object.put("value", seekBar.getProgress());
                        DeviceSDK.setDeviceParam(userid, 0x2026,
                                object.toString());
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

            }
        });
        if (id == 1) {
            seek.setProgress(this.luminanceP);
            tv.setText(this.luminanceP + "/255");
        } else {
            seek.setProgress(this.contrastP);
            tv.setText(this.contrastP + "/255");

        }
        int i = getWindow().getWindowManager().getDefaultDisplay().getWidth();
        luminancepop = new PopupWindow(layout, i * 2 / 3,
                LayoutParams.WRAP_CONTENT);
        luminancepop.setFocusable(true);
        luminancepop.setOutsideTouchable(true);
        this.luminancepop.setBackgroundDrawable(new ColorDrawable(0));
    }


    private void closePop() {
        if (luminancepop != null && luminancepop.isShowing()) {
            luminancepop.dismiss();
        }
    }

    @Override
    public void callBack_getParam(long UserID, long nType, String param) {
        // 图像参数返回值
        Log.d("zjm", "图像参数");
        if (nType == 0x2025) {
            try {
                JSONObject jsonObject = new JSONObject(param);
                this.luminanceP = jsonObject.getInt("vbright");
                this.contrastP = jsonObject.getInt("vcontrast");
                this.resolution = jsonObject.getInt("resolution");
                this.filp = jsonObject.getInt("flip");  //图像翻转
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            handler.sendEmptyMessage(2);
        }

    }

    @Override
    public void callBack_setParam(long UserID, long nType, int nResult) {
        // 图像参数设置回馈
    }

}
