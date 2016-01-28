package com.example.ipcam.camer.Fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ipcam.camer.BaseActivity;
import com.example.ipcam.camer.R;
import com.example.ipcam.camer.adpter.IpcAlarmListAdpter;
import com.example.ipcam.camer.entity.IpcDevice;
import com.example.ipcam.camer.view.XListView;
import com.shizhefei.fragment.LazyFragment;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.slidebar.ColorBar;

import java.util.List;


public class AudioLayerFragment extends LazyFragment implements XListView.IXListViewListener, View.OnClickListener {

    private XListView xListView;
    private IpcAlarmListAdpter adpter;
    private List<IpcDevice> list;
    private LayoutInflater inflate;
    private IndicatorViewPager indicatorViewPager;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onPauseLazy() {
        super.onPauseLazy();
    }

    @Override
    protected void onCreateViewLazy(Bundle savedInstanceState) {
        super.onCreateViewLazy(savedInstanceState);
        setContentView(R.layout.ipc_audio_msg);
        BaseActivity.activitys.add(AudioLayerFragment.this.getActivity());
        ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
        Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
        inflate = LayoutInflater.from(getApplicationContext());
        viewPager.setOffscreenPageLimit(2);
        float unSelectSize = 14;
        float selectSize = unSelectSize * 1.2f;
        indicator.setScrollBar(new ColorBar(getApplicationContext(), getResources().getColor(R.color.buttom_color), 5));
//        indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(R.color.yellow, R.color.buttom_color).setSize(selectSize, unSelectSize));
        indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
        indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
    }

    @Override
    protected void onResumeLazy() {
        super.onResumeLazy();
//        initView();
    }

//    @SuppressWarnings("unchecked")
//    private void initView() {
//        list = DeviceManager.getInstence(AudioLayerFragment.this.getActivity()).GetIPCListDevice();
//        adpter = new IpcAlarmListAdpter(AudioLayerFragment.this.getActivity(), list, 2);
//        xListView.setAdapter(adpter);
//
//    }

    @Override
    public void onRefresh() {
        handler.sendEmptyMessage(0);
    }

    @Override
    public void onLoadMore() {
    }

    @Override
    public void onClick(View v) {

    }

    private class MyAdapter extends IndicatorViewPager.IndicatorFragmentPagerAdapter {

        public MyAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public View getViewForTab(int position, View convertView, ViewGroup container) {
            if (convertView == null) {
                convertView = inflate.inflate(R.layout.button, container, false);
            }
            TextView textView = (TextView) convertView;
            if (position == 0) {
                textView.setText("本地录像");
            } else if (position == 1) {
                textView.setText("远程录像");
            }
            return convertView;
        }

        @Override
        public Fragment getFragmentForPage(int position) {
            Fragment fragment = null;
            if (position == 0) {
                fragment = new LocalAudioLayerFragment();
            } else if (position == 1)
                fragment = new P2pAudioLayerFragment();
            return fragment;
        }
    }
}
