package com.example.ipcam.camer.adpter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.ipcam.camer.R;
import com.example.ipcam.camer.entity.P2pAudio;

import java.util.List;


public class P2pAudioAdpter extends BaseAdapter {
    private Context context;
    private List<P2pAudio> list;
    private boolean isvisb;   //是否显示

    public boolean isvisb() {
        return isvisb;
    }

    public void setIsvisb(boolean isvisb) {
        this.isvisb = isvisb;
    }

    public P2pAudioAdpter(List<P2pAudio> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
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

    @SuppressLint("NewApi")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Viewholder viewholder;
        if (convertView == null) {
            viewholder = new Viewholder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.p2p_audio_item, null);
            viewholder.time = (TextView) convertView
                    .findViewById(R.id.alarm_time);
            viewholder.dname = (TextView) convertView
                    .findViewById(R.id.alarm_name);
            viewholder.box = (CheckBox) convertView
                    .findViewById(R.id.msg_item_check);
            viewholder.msg = (TextView) convertView.findViewById(R.id.alarmmsg);
            viewholder.file = (TextView) convertView
                    .findViewById(R.id.alarmfile);
            convertView.setTag(viewholder);
        } else
            viewholder = (Viewholder) convertView.getTag();
        final P2pAudio manager = list.get(position);
        viewholder.time.setText(manager.getFile_date());
        viewholder.dname.setText(manager.getFile_name());
        viewholder.msg.setText(manager.getFile_size());
        viewholder.box.setChecked(manager.ischeck());
        if (isvisb) {
            viewholder.box.setVisibility(View.VISIBLE);
        } else
            viewholder.box.setVisibility(View.GONE);

        viewholder.box.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.set(position, manager);
            }
        });

        return convertView;

    }


    class Viewholder {
        TextView dname, time, msg, file;
        CheckBox box;
    }
}
