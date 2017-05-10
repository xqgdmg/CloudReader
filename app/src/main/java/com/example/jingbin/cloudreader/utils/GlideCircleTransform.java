package com.example.jingbin.cloudreader.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * 用于显示圆形图片
 */

public class GlideCircleTransform extends BitmapTransformation {

    public GlideCircleTransform(Context context) {
        super(context);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    /**
     * 其实原理是一样的，也是用画笔
     */
    private static Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        // 宽高最小值
        int size = Math.min(source.getWidth(), source.getHeight());

        // 中心点坐标
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);// BitmapShader

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);// 画板的大小
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);// 画板放进画布
        Paint paint = new Paint();

        // setShader 好像看不出效果，当图片小于圆形范围的时候，圆形图片周围的颜色采用 图片边缘的颜色，这样就看不出来图片比圆形范围小
        // BitmapShader.TileMode.CLAMP --> replicate the edge color if the shader draws outside of its original bounds 复制边缘颜色
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));

        paint.setAntiAlias(true);
        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);

        return result;
    }

    @Override
    public String getId() {
        return getClass().getName();
    }
}
