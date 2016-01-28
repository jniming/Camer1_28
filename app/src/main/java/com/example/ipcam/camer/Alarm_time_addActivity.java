package com.example.ipcam.camer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ipcam.camer.entity.AlarmTime;
import com.example.ipcam.camer.util.SharedPrefer;
import com.example.ipcam.camer.view.WheelView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/27.
 */
public class Alarm_time_addActivity extends BaseActivity implements View.OnClickListener {
    private List<String> hours_list = new ArrayList<>();
    private List<String> min_list = new ArrayList<>();
    private TextView msg_spinner_left, msg_spinner_right;
    private PopupWindow popupWindow;
    private String hours_str = "08", min_str = "00";
    private int flg;
    private boolean[] type = new boolean[]{true, false};
    private LinearLayout one_layout, day_layout;
    private FrameLayout one_fl;
    private FrameLayout day_fl;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            one_layout.setVisibility(type[0] ? View.VISIBLE : View.GONE);
            day_layout.setVisibility(type[1] ? View.VISIBLE : View.GONE);

            if (msg.what == 1) {
                msg_spinner_left.setText(hours_str + ":" + min_str);
            } else if (msg.what == 2) {
                msg_spinner_right.setText(hours_str + ":" + min_str);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_time_add);
        setNaView(R.drawable.left_back_selector, "", 0, "", 0, "", R.drawable.right_finsh_selector, "");
        setTitle("添加任务");
        msg_spinner_right = (TextView) findViewById(R.id.msg_spinner_right);
        msg_spinner_left = (TextView) findViewById(R.id.msg_spinner_left);
        one_layout = (LinearLayout) findViewById(R.id.onece_type);
        day_layout = (LinearLayout) findViewById(R.id.day_type);
        one_fl = (FrameLayout) findViewById(R.id.onece_fl);
        day_fl = (FrameLayout) findViewById(R.id.day_fl);
        one_layout.setVisibility(type[0] ? View.VISIBLE : View.GONE);
        day_layout.setVisibility(type[1] ? View.VISIBLE : View.GONE);
        timepop();
        msg_spinner_left.setOnClickListener(this);
        msg_spinner_right.setOnClickListener(this);
        one_fl.setOnClickListener(this);
        day_fl.setOnClickListener(this);
    }

    private void initData() {
        for (int i = 0; i <= 23; i++) {
            if (i < 10) {
                hours_list.add("0" + i);
            } else
                hours_list.add("" + i);

        }
        for (int i = 0; i <= 59; i++) {
            if (i < 10) {
                min_list.add("0" + i);
            } else
                min_list.add("" + i);

        }

    }


    public boolean check_time(String hh, String mm) {
        boolean flg = false;
        int left_ho = Integer.parseInt(hh.substring(0, 2));
        int left_min = Integer.parseInt(hh.substring(3, 5));
        int right_ho = Integer.parseInt(mm.substring(0, 2));
        int right_min = Integer.parseInt(mm.substring(3, 5));

        if (left_ho > right_ho) {
            Toast.makeText(this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
            flg = true;
        }
        if (left_ho == right_ho && left_min > right_min) {
            Toast.makeText(this, "开始时间不能小于结束时间", Toast.LENGTH_SHORT).show();
            flg = true;
        }
        return flg;
    }

    @Override
    public void viewEvent(TitleBar titleBar, View v) {
        if (titleBar == TitleBar.RIGHT) {
            String hh = msg_spinner_left.getText().toString().trim();
            String mm = msg_spinner_right.getText().toString().trim();
            if (hh.isEmpty()) {
                Toast.makeText(this, "开始时间不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mm.isEmpty()) {
                Toast.makeText(this, "结束时间不能为空!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (check_time(hh, mm)) {
                return;
            }
            int id = 0;
            for (int i = 0; i < type.length; i++) {
                if (type[i]) {
                    id = i;
                }
            }
            AlarmTime time = new AlarmTime();
            time.setSt_time(hh);
            time.setEnd_time(mm);
            time.setCheck(true);
            time.setType(id);





            List<AlarmTime> list = SharedPrefer.GetAlarmTime(this);
            list.add(time);
            SharedPrefer.SaveAlarmTime(list, this);
            setResult(Activity.RESULT_OK);
            finish();
        } else if (titleBar == TitleBar.LIEFT) {
            back();
        }
    }

    private void timepop() {
        initData();
        View view = LayoutInflater.from(this).inflate(R.layout.alarm_time_btom_pop, null);
        TextView left = (TextView) view.findViewById(R.id.left_text);
        TextView right = (TextView) view.findViewById(R.id.right_text);

        WheelView left_w = (WheelView) view.findViewById(R.id.hours_wheel);
        WheelView right_w = (WheelView) view.findViewById(R.id.min_wheel);
        left_w.setData((ArrayList<String>) hours_list);
        right_w.setData((ArrayList<String>) min_list);
        left_w.setDefault(8);
        right_w.setDefault(0);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler.sendEmptyMessage(flg);
                close();
            }
        });
        left_w.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                hours_str = text;
            }

            @Override
            public void selecting(int id, String text) {

            }
        });
        right_w.setOnSelectListener(new WheelView.OnSelectListener() {
            @Override
            public void endSelect(int id, String text) {
                min_str = text;
            }

            @Override
            public void selecting(int id, String text) {

            }
        });


        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setAnimationStyle(R.style.msgpop_stype);


    }

    private void close() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();

        }

    }

    @Override
    public void onClick(View v) {
        if (msg_spinner_left.getId() == v.getId()) {
            close();
            flg = 1;
            popupWindow.showAtLocation(msg_spinner_left, Gravity.BOTTOM, 0, 0);
        } else if (msg_spinner_right.getId() == v.getId()) {
            close();
            flg = 2;
            popupWindow.showAtLocation(msg_spinner_left, Gravity.BOTTOM, 0, 0);
        } else if (one_fl.getId() == v.getId()) {
            for (int i = 0; i < type.length; i++) {
                if (i == 0) {
                    type[i] = true;
                } else
                    type[i] = false;

            }
            handler.sendEmptyMessage(0);


        } else if (day_fl.getId() == v.getId()) {
            for (int i = 0; i < type.length; i++) {
                if (i == 1) {
                    type[i] = true;
                } else
                    type[i] = false;

            }
            handler.sendEmptyMessage(0);
        }
    }
}
