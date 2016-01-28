package com.example.ipcam.camer.adpter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ipcam.camer.R;
import com.example.ipcam.camer.entity.AlarmTime;
import com.example.ipcam.camer.util.SharedPrefer;
import com.example.ipcam.camer.view.CustomDialog;

import java.util.List;


public class AlarmTimeAdpter extends BaseAdapter {
    private List<AlarmTime> list;
    private Context context;
    private Handler handler;

    public AlarmTimeAdpter(Context context, List<AlarmTime> list, Handler handler) {
        this.context = context;
        this.handler = handler;
        this.list = list;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        if (list == null || list.size() == 0) {
            return 0;
        }
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Myview view;
        if (convertView == null) {
            view = new Myview();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.alarm_time_item, null);
            view.end_time = (TextView) convertView.findViewById(R.id.end_time);
            view.st_time = (TextView) convertView.findViewById(R.id.start_time);
            view.btn = (ToggleButton) convertView.findViewById(R.id.togbtn);
            convertView.setTag(view);
        } else {
            view = (Myview) convertView.getTag();
        }
        final AlarmTime alarmTime = list.get(position);
        view.end_time.setText(alarmTime.getEnd_time());
        view.st_time.setText(alarmTime.getSt_time());
        view.btn.setChecked(alarmTime.isCheck());
        view.btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                alarmTime.setCheck(isChecked);
                list.set(position, alarmTime);
                boolean flg = SharedPrefer.SaveAlarmTime(list, context);
            }
        });


        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                CustomDialog.Builder builder = new CustomDialog.Builder(context);
                builder.setTitle("提示");
                builder.setMessage("是否确定删除");
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        list.remove(position);

                        boolean flg = SharedPrefer.SaveAlarmTime(list, context);
                        if (flg) {
                            AlarmTimeAdpter.this.notifyDataSetChanged();
                            Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "删除失败", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                return false;
            }
        });

        return convertView;
    }

    class Myview {
        TextView st_time, end_time;
        ToggleButton btn;
    }

}
