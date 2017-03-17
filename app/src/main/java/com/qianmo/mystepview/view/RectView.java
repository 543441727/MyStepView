package com.qianmo.mystepview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;


/**
 * Created by Administrator on 2017/3/16 0016.
 * E-Mail：543441727@qq.com
 */

public class RectView extends View {

    public RectView(Context context) {
        this(context, null);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        //设置背景颜色为绿色
//
//        //绘制矩形
//        Paint paint = new Paint();
//        //设置实心
//        paint.setStyle(Paint.Style.STROKE);
//        //设置消除锯齿
//        paint.setAntiAlias(true);
//        //设置画笔颜色
//        paint.setColor(Color.RED);
//        //设置paint的外边框宽度
//        paint.setStrokeWidth(40);
//        //绘制矩形
//        canvas.drawRect(200, 200, 800, 420, paint);
//        //绘制圆
//        canvas.drawCircle(350,350,100,paint);


        //绘制虚线
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2);
        /***
         * 构造函数为DashPathEffect(float[] intervals, float offset)，其中intervals为虚线的ON和OFF数组，该数组的length必须大于等于2，phase为绘制时的偏移量。
         本代码中先绘制8长度的实现，再绘制8长度的虚线，再绘制8长度的实现，再绘制8长度的虚线
         */
        DashPathEffect mEffect = new DashPathEffect(new float[]{8, 8, 8, 8,}, 1);
        Path path = new Path();
        //移动画笔到坐标200,600 这个点
        path.moveTo(200, 600);
        //绘制直线
        path.lineTo(800, 600);
        paint.setPathEffect(mEffect);

        canvas.drawPath(path, paint);

//        //View.getWidth():表示的是当前控件的宽度，即getRight()-getLeft()
    }

}
