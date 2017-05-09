package com.example.jingbin.cloudreader.utils;

import android.view.View;
import android.view.View.OnClickListener;

import java.util.Calendar;

/**
 * 避免在1秒内出发多次点击
 * Created by yangcai on 2016/1/15.
 */
public abstract class PerfectClickListener implements OnClickListener {
    public static final int MIN_CLICK_DELAY_TIME = 1000;
    private long lastClickTime = 0;
    private int mId = -1;

    @Override
    public void onClick(View v) {
        long currentTime = Calendar.getInstance().getTimeInMillis();
        int id = v.getId();
        if (this.mId != id) {
            this.mId = id;
            lastClickTime = currentTime;
            onNoDoubleClick(v);
            return;
        }
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    /**
     * 必须实现，已经判断了不是连续一秒内的多次点击
     */
    protected abstract void onNoDoubleClick(View v);
}
