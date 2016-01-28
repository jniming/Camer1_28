/**
 *
 */
package com.example.ipcam.camer.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.ipcam.camer.Fragment.MainFragment;
import com.example.ipcam.camer.R;
import com.example.ipcam.camer.util.Manager;
import com.example.ipcam.camer.util.Util;


/**
 * @author wang.jingui
 */
public class BridgeService extends Service {

    public static boolean isBackground = false;
    public static boolean isEXIT = false;
    private NotificationManager mNotificationManager;
    private String EXIT_REVICE = "android.intent.exit";
    private Manager manager;

    /**
     * 创建通知
     *
     * @return
     */
    public Notification getNotification(String string) {

        Intent intent = new Intent("android.intent.action.MAIN");  //设置启动意图
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setClass(BridgeService.this, MainFragment.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);  //该类是对intent的一个封装类,他不会马上启动,瞒住条件才会启动里面装载的intent
        RemoteViews localRemoteViews = new RemoteViews(getPackageName(), R.layout.notification); //创建通知栏的布局
        Notification notification = new Notification(R.drawable.camera, string, System.currentTimeMillis());
        notification.flags = Notification.FLAG_ONGOING_EVENT;   //设置通知栏像qq一样一直显示
        notification.contentIntent = pendingIntent;
        notification.contentView = localRemoteViews;
        notification.contentView.setTextViewText(R.id.nofincation_time, Util.getNowTime());
        notification.contentView.setTextViewText(R.id.nofincation_state, "程序正在运行");
        mNotificationManager.notify(100, notification);
        return notification;
    }

    private void init() {
        new Thread() {
            @Override
            public void run() {
                manager.initSdk();
            }
        }.start();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("zjm", "onCreate");
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager = Manager.getIntence(this);
        init();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(EXIT_REVICE);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        manager.onDestory();
        mNotificationManager.cancel(100);
//        initThtread = null;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = 0;
        Log.e("zjm", "onStartCommand");
        if (intent != null) {
            i = intent.getIntExtra("tag", -1);
            if (i == 1) {
                startForeground(100, getNotification("程序正在运行"));   //让服务前台运行
            } else if (i == 2) {
                stopForeground(true);   //让服务后台运行
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

//    class ConRecive extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
//            if (networkInfo.isConnected()) {
//
//
//            }
//
//
//        }
//    }


}
