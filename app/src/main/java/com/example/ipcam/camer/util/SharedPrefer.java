package com.example.ipcam.camer.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

import com.example.ipcam.camer.entity.AlarmTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 主要用于存储软件的配置信息
 * Created by Administrator on 2016/1/8.
 */
public class SharedPrefer {
    public static String ALARM_LS = "alarm_ls";  //报警声音
    public static String ALARM_T = "alarm_time";  //报警时长
    public static String ALARM_Z = "alarm_zd";  //报警震动

    /**
     * 保存软件的设置信息
     */
    public static void SaveAppSetingData(Context context, HashMap<String, Object> map) {
        SharedPreferences preferences = context.getSharedPreferences("setdata",
                Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        boolean flg = (boolean) map.get(ALARM_LS);
        editor.putBoolean(ALARM_LS, flg);
        editor.putBoolean(ALARM_Z, (Boolean) map.get(ALARM_Z));
        editor.putInt(ALARM_T, (Integer) map.get(ALARM_T));
        editor.commit();
    }

    /**
     * 获取软件的设置信息
     *
     * @param context
     * @return
     */
    public static HashMap<String, Object> GetAppSetingData(Context context) {
        HashMap<String, Object> map = new HashMap<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("setdata",
                Activity.MODE_PRIVATE);
        int time = sharedPreferences.getInt(ALARM_T, 30);
        boolean zd = sharedPreferences.getBoolean(ALARM_Z, true);
        boolean sy = sharedPreferences.getBoolean(ALARM_LS, true);

        map.put(ALARM_LS, sy);
        map.put(ALARM_Z, zd);
        map.put(ALARM_T, time);
        return map;
    }


    /**
     * 保存报警时间段
     *
     * @param list
     */
    public static boolean SaveAlarmTime(List<AlarmTime> list, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("alamrtime", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        int size = list.size();
        editor.putInt("size", size);
        for (int i = 0; i < size; i++) {
            AlarmTime alarmTime = list.get(i);
            editor.remove("type" + i);
            editor.remove("start_t" + i);
            editor.remove("end_t" + i);
            editor.putInt("type" + i, alarmTime.getType());
            editor.putString("start_t" + i, alarmTime.getSt_time());
            editor.putString("end_t" + i, alarmTime.getEnd_time());
            editor.putBoolean("ischeck" + i, alarmTime.isCheck());
        }
        return editor.commit();

    }

    /**
     * 获取报警时间段
     *
     * @param
     */
    public static List<AlarmTime> GetAlarmTime(Context context) {
        List<AlarmTime> list = new ArrayList<>();
        SharedPreferences sharedPreferences = context.getSharedPreferences("alamrtime", Activity.MODE_PRIVATE);
        int size = sharedPreferences.getInt("size", 0);
        for (int i = 0; i < size; i++) {
            AlarmTime alarmTime = new AlarmTime();
            int type = sharedPreferences.getInt("type" + i, 0);
            String st_time = sharedPreferences.getString("start_t" + i, "");
            String end_t = sharedPreferences.getString("end_t" + i, "");
            boolean ischeck = sharedPreferences.getBoolean("ischeck" + i, false);
            alarmTime.setType(type);
            alarmTime.setCheck(ischeck);
            alarmTime.setEnd_time(end_t);
            alarmTime.setSt_time(st_time);
            list.add(alarmTime);
        }
        return list;
    }


}
