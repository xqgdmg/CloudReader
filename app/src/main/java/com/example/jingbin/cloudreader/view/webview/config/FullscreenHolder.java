package com.example.jingbin.cloudreader.view.webview.config;

import android.content.Context;
import android.view.MotionEvent;
import android.widget.FrameLayout;

/**
 * Created by jingbin on 2016/11/17.
 * WebView 全屏播放视频
 */

public class FullscreenHolder extends FrameLayout {

    public FullscreenHolder(Context ctx) {
        super(ctx);
        setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
    }

    // true: 1.告诉Android，MotionEvent对象已被使用，不能再提供给其他方法。
    // 2.还告诉Android，继续将此触摸序列的触摸事件(move,up)发送到此方法。
    // false:1.告诉Android，onTouch()方法未使用该事件，所以Android寻找要调用的下一个方法。
    // 2.告诉Android。不再将此触摸序列的触摸事件（move,up）发送到此方法。
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 屏蔽掉了触摸事件？ down 事件还能传递？
        return true;
    }
}
