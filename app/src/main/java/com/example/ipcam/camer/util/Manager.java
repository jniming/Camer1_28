package com.example.ipcam.camer.util;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.ipcam.camer.Callback.InitCallBack;
import com.example.ipcam.camer.DeviceAlarmActivity;
import com.example.ipcam.camer.Listener.APListener;
import com.example.ipcam.camer.Listener.AlarmInformationMsg;
import com.example.ipcam.camer.Listener.AlarmListener;
import com.example.ipcam.camer.Listener.DateListener;
import com.example.ipcam.camer.Listener.DeviceStatusListener;
import com.example.ipcam.camer.Listener.EmailListenner;
import com.example.ipcam.camer.Listener.FTPListener;
import com.example.ipcam.camer.Listener.GraphicListener;
import com.example.ipcam.camer.Listener.IpcWiFiListListener;
import com.example.ipcam.camer.Listener.NameListener;
import com.example.ipcam.camer.Listener.PictureListener;
import com.example.ipcam.camer.Listener.PlayListener;
import com.example.ipcam.camer.Listener.RecorderListener;
import com.example.ipcam.camer.Listener.SdkListener;
import com.example.ipcam.camer.Listener.SeachListener;
import com.example.ipcam.camer.Listener.SettingsListener;
import com.example.ipcam.camer.Listener.UserListener;
import com.example.ipcam.camer.Listener.WiFiListener;
import com.example.ipcam.camer.Service.BridgeService;
import com.example.ipcam.camer.db.DeviceManager;
import com.example.ipcam.camer.entity.AlarmTime;
import com.example.ipcam.camer.entity.IpcDevice;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import hsl.p2pipcam.nativecaller.DeviceSDK;

/**
 * 管理器
 * Created by Administrator on 2016/1/15.
 */
public class Manager {
    public static Manager manager;
    private static PlayListener playListener;
    private static DeviceStatusListener deviceStatusListener;
    private static RecorderListener recorderListener;
    private static SettingsListener settingsListener;
    private static SeachListener seachlistenner;
    private static DateListener datelistener;
    private static WiFiListener wifilistener;
    private static FTPListener ftplistener;
    private static NameListener namelistener;
    private static APListener aplistener;
    private static AlarmListener alarmlistener;
    private static UserListener userlistener;
    private static PictureListener picturelistener;
    private static GraphicListener graphiclistener;
    private static AlarmInformationMsg informationMsg; // 报警信息回调
    private static IpcWiFiListListener ipcWiFiListListener;
    private static EmailListenner emailListenner;
    private static SdkListener sdkListener;
    private Thread initThtread;
    public static InitCallBack callbacke;
    private Context context;
    private List<IpcDevice> device_list = new ArrayList<>();

    public void initalize() {
        List localList = DeviceManager.getInstence(context).GetIPCListDevice();
        this.device_list.clear();
        Iterator iterator;
        if (localList.size() > 0) {
            iterator = localList.iterator();
            if (iterator.hasNext()) {
                IpcDevice device = (IpcDevice) iterator.next();

                device_list.add(device);
            }
        }

    }


    public Manager(Context context) {
        Log.e("zjm", "manager实例");
        this.context = context;
    }

    public static Manager getIntence(Context context) {

        if (manager == null) {
            manager = new Manager(context);
        }
        return manager;
    }

    public void initSdk() {
        DeviceSDK.initialize("");
        DeviceSDK.setCallback(this);
        DeviceSDK.networkDetect();
        DeviceSDK.setSearchCallback(this);
    }

    public void onDestory() {
        DeviceSDK.unInitialize();
    }


    public void setDeviceStatusListener(
            DeviceStatusListener deviceStatusListener) {
        this.deviceStatusListener = deviceStatusListener;
    }

    public void setAlarmInformationMsg(
            AlarmInformationMsg deviceStatusListener) {
        this.informationMsg = deviceStatusListener;
    }

    public void setInitCallBack(InitCallBack callbacke) {
        this.callbacke = callbacke;
    }

    // 设置时间设置的监听
    public void setDateListener(DateListener dateListener) {
        this.datelistener = dateListener;
    }

    public void setIpcWifiLinstener(IpcWiFiListListener dateListener) {
        this.ipcWiFiListListener = dateListener;
    }

    public void setSdkListener(SdkListener sdkListener) {
        this.sdkListener = sdkListener;
    }

    public void setEmailLinstener(EmailListenner emailListenner) {
        this.emailListenner = emailListenner;
    }

    public void setWifiListener(WiFiListener wifilistener) {
        this.wifilistener = wifilistener;
    }

    public void setAlarmListener(AlarmListener alarmilistener) {
        this.alarmlistener = alarmilistener;
    }

    public void setPlayListener(PlayListener playListener) {
        this.playListener = playListener;
    }

    public void setNameListener(NameListener namelistener) {
        this.namelistener = namelistener;
    }

    public void setUserListener(UserListener userlistener) {
        this.userlistener = userlistener;
    }

    public void setFTPListener(FTPListener ftpListener) {
        this.ftplistener = ftpListener;
    }

    public void setAPListener(APListener apListener) {
        this.aplistener = apListener;
    }

    public void setRecorderListener(RecorderListener recorderListener) {
        this.recorderListener = recorderListener;
    }

    public void setSearchListener(SeachListener seachlistenner) {
        this.seachlistenner = seachlistenner;
    }

    public void setSettingsListener(SettingsListener settingsListener) {
        this.settingsListener = settingsListener;
    }

    public void setGraphicListener(GraphicListener graphiclistener) {
        this.graphiclistener = graphiclistener;
    }

    public void setPictureListener(PictureListener picturelistener) {
        this.picturelistener = picturelistener;
    }

    // -------------------------------------------------------------------------
    // ---------------------------以下是JNI层回调的接口-------------------------------
    // -------------------------------------------------------------------------

    public void CallBack_SnapShot(long UserID, byte[] buff, int len) {
        System.out.println("CallBack_SnapShot回调");
        if (picturelistener != null) {
            picturelistener.CallBack_RecordPicture(UserID, buff, len);
        }

    }

    public void CallBack_GetParam(long UserID, long nType, String param) {
        // if (settingsListener != null)
        // settingsListener.callBack_getParam(UserID, nType, param);
        switch (new Long(nType).intValue()) {
            case 0x2016:
                if (datelistener != null)
                    datelistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2013:
                if (wifilistener != null)
                    wifilistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2014:
                if (ipcWiFiListListener != null)
                    ipcWiFiListListener.callBack_getWIFI(UserID, nType, param);
                break;
            case 0x2007:
                if (ftplistener != null)
                    ftplistener.callBack_getParam(UserID, nType, param);
                break;

            case 0x2703:
                if (aplistener != null)
                    aplistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2018:
                if (alarmlistener != null)
                    alarmlistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2003:
                if (userlistener != null)
                    userlistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2025:
                if (graphiclistener != null)
                    graphiclistener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2021:
                if (sdkListener != null)
                    sdkListener.callBack_getParam(UserID, nType, param);
                break;
            case 0x2009:
                if (emailListenner != null)
                    emailListenner.callBack_getParam(UserID, nType, param);
                break;
            default:
                break;
        }

    }

    public void CallBack_SetParam(long UserID, long nType, int nResult) {
        // if (settingsListener != null)
        // settingsListener.callBack_setParam(UserID, nType, nResult);
        switch (new Long(nType).intValue()) {
            case 0x2015:
                if (datelistener != null)
                    datelistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2014:
                if (wifilistener != null)
                    wifilistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2006:
                if (ftplistener != null)
                    ftplistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2704:
                if (aplistener != null)
                    aplistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2017:
                if (alarmlistener != null)
                    alarmlistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2002:
                if (userlistener != null)
                    userlistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2026:
                if (graphiclistener != null)
                    graphiclistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2702:
                if (namelistener != null)
                    namelistener.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2008:
                if (emailListenner != null)
                    emailListenner.callBack_setParam(UserID, nType, nResult);
                break;
            case 0x2022:
                if (sdkListener != null)
                    sdkListener.callBack_setParam(UserID, nType, nResult);
                break;
            default:
                break;
        }
    }

    public void CallBack_Event(long UserID, long nType) {
        int status = new Long(nType).intValue();
        Log.d("zjm", "服务中初始化摄像头的状态--" + status);
        if (deviceStatusListener != null)
            deviceStatusListener.receiveDeviceStatus(UserID, status);
        DeviceManager.getInstence(context).updateIPC_status(UserID, status);

    }

    public void VideoData(long UserID, byte[] VideoBuf, int h264Data, int nLen,
                          int Width, int Height, int time) {

    }

    public void callBackAudioData(long nUserID, byte[] pcm, int size) {
        if (playListener != null)
            playListener.callBackAudioData(nUserID, pcm, size);
        if (recorderListener != null)
            recorderListener.callBackAudioData(nUserID, pcm, size);
    }

    /**
     * 录像查询
     *
     * @param UserID
     * @param filecount
     * @param fname
     * @param strDate
     * @param size
     */
    public void CallBack_RecordFileList(long UserID, int filecount,
                                        String fname, String strDate, int size) {
//        if (settingsListener != null)
//            settingsListener.recordFileList(UserID, filecount, fname, strDate,
//                    size);

    }

    public void CallBack_RecordFileListV2(long UserID, String param) {
        if (settingsListener != null)
            settingsListener.callBack_getParam(UserID, 0, param);
    }

    public void CallBack_SearchDevice(String DeviceInfo) {
        seachlistenner.callBack_SeachData(DeviceInfo);
    }

    public void CallBack_P2PMode(long UserID, int nType) {
    }

    public void CallBack_RecordPicture(long userid, byte[] buff, int len) {
        System.out.println("手动抓拍后回调--->");

    }

    public void CallBack_RecordPlayPos(long userid, int pos) {
    }

    /**
     * 录像回调
     *
     * @param nUserID
     * @param data
     * @param type
     * @param size
     */
    public void CallBack_VideoData(long nUserID, byte[] data, int type, int size) {
    }


    private boolean Time(String hh, String mm) {
        boolean flg = false;
        long now_minl = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-HH-dd hh:mm:ss");
        String date_str = simpleDateFormat.format(new Date(now_minl));
        String date_str2 = simpleDateFormat.format(new Date(now_minl));
        String date_str3 = simpleDateFormat.format(new Date(now_minl));

        String h_m = date_str.substring(date_str.length() - 8, date_str.length() - 3);
        String start_str = date_str2.replace(h_m, hh);
        String end_str = date_str3.replace(h_m, mm);
        try {
            long start_m = simpleDateFormat.parse(start_str).getTime();
            long end_m = simpleDateFormat.parse(end_str).getTime();
            if (now_minl >= start_m && now_minl <= end_m) {
                return true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        return false;
    }


    /**
     * 报警回调
     *
     * @param UserID
     * @param nType
     */
    public void CallBack_AlarmMessage(long UserID, int nType) throws InterruptedException {
        // informationMsg.alarmMgs(UserID, nType);
        Log.e("zjm", "报警-----");
        List<AlarmTime> list = SharedPrefer.GetAlarmTime(context);
        Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            AlarmTime alarmTime = (AlarmTime) iterator.next();
            String start_t = alarmTime.getSt_time();
            String end_t = alarmTime.getEnd_time();
            if (Time(start_t, end_t)) {
                if (alarmTime.isCheck()) {
                    if (!BridgeService.isEXIT) {
                        showNotification(UserID, nType);
                    }
                }


            }

        }


    }

    public void showNotification(long userid, int nType) throws InterruptedException {

        boolean flg = DeviceAlarmActivity.isvis;
        if (!flg) {
            Intent intent = new Intent();
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setClass(context, DeviceAlarmActivity.class);
            intent.putExtra("userid", userid);
            intent.putExtra("nType", nType);
            intent.putExtra("pic", "");
            intent.putExtra("type", Config.alarm_msg);
            context.startActivity(intent);

        }
    }


}
