package com.qianmo.mystepview.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.qianmo.mystepview.R;
import com.qianmo.mystepview.bean.StepBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wangjitao on 2017/3/16 0016.
 * E-Mail：543441727@qq.com
 * 横向指示器的创建
 */

public class StepViewIndicator extends View {
    //定义默认的高度
    private int defaultStepIndicatorNum = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());


    //直线
    private Path mPath;
    //虚线绘制函数
    private DashPathEffect mEffect;
    //完成的下标集合
    private List<Float> mComplectedXPosition;

    //已完成的部分绘制对象
    private Paint mComplectedPaint;
    //定义默认完成线的颜色  ;
    private int mCompletedLineColor = Color.WHITE;
    //已完成线的高度
    private float mCompletedLineHeight;

    //未完成的部分绘制对象
    private Paint mUnComplectedPaint;
    //定义默认未完成线的颜色  ;
    private int mUnCompletedLineColor = Color.WHITE;

    //定义圆的半径
    private float mCircleRadius;
    //定义线的长度
    private float mLinePadding;

    //定义三种状态下的图片(已完成的图片，正在进行的图片，未完成的图片)
    Drawable mCompleteIcon;
    Drawable mAttentionIcon;
    Drawable mDefaultIcon;

    //动态计算view位置
    private float mCenterY;
    private float mLeftY;
    private float mRightY;

    //总共有多少步骤
    private int mStepNum = 0;
    private List<StepBean> mStepBeanList;
    //正在进行的位置
    private int mComplectingPosition;

    //添加对view的监听
    private OnDrawIndicatorListener mOnDrawListener;


    private int screenWidth;//this screen width

    public StepViewIndicator(Context context) {
        this(context, null);
    }

    public StepViewIndicator(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepViewIndicator(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化常见属性
     */
    private void init() {
        mStepBeanList = new ArrayList<>();
        mPath = new Path();
        mEffect = new DashPathEffect(new float[]{8, 8, 8, 8}, 1);

        //设置已完成的初始化基本设置
        mComplectedXPosition = new ArrayList();
        mComplectedPaint = new Paint();
        mComplectedPaint.setAntiAlias(true);
        mComplectedPaint.setColor(mCompletedLineColor);
        mComplectedPaint.setStyle(Paint.Style.FILL);
        mComplectedPaint.setStrokeWidth(2);

        //设置未完成的初始化基本设置
        mUnComplectedPaint = new Paint();
        mUnComplectedPaint.setAntiAlias(true);
        mUnComplectedPaint.setColor(mUnCompletedLineColor);
        mUnComplectedPaint.setStyle(Paint.Style.STROKE);
        mUnComplectedPaint.setStrokeWidth(2);
        mUnComplectedPaint.setPathEffect(mEffect);

        //圆的半径、线的长度、线的高度基本值设置
        mCompletedLineHeight = 0.05f * defaultStepIndicatorNum;
        mCircleRadius = 0.28f * defaultStepIndicatorNum;
        mLinePadding = 1.5f * defaultStepIndicatorNum;

        //初始化三种状态下的图片
        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.drawable.complted);
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.drawable.attention);
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.drawable.default_icon);


        //添加测试数据
//        List<StepBean> datas = new ArrayList<>();
//        datas.add(new StepBean("接单", StepBean.STEP_COMPLETED));
//        datas.add(new StepBean("打包", StepBean.STEP_CURRENT));
//        datas.add(new StepBean("出发", StepBean.STEP_UNDO));
//        setStepNum(datas);
    }

    /**
     * 测量自身的高度
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = defaultStepIndicatorNum * 2;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec);
        }
        int height = defaultStepIndicatorNum;
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec));
        }
        setMeasuredDimension(width, height);
    }



    /**
     * View大小改变
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //下面这三句代码只是为了绘制矩形的线
        mCenterY = 0.5f * getHeight();
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;

        mComplectedXPosition.clear();
        //计算所有总空间离最左边的距离，并记录所有的圆心x轴的坐标
        for (int i = 0; i < mStepNum; i++) {
            //计算全部最左边
            float paddingLeft = (getWidth() - mStepNum * mCircleRadius * 2 - (mStepNum - 1) * mLinePadding) / 2;
            //将所有的圆心X轴坐标记录到集合中
            mComplectedXPosition.add(paddingLeft + mCircleRadius + i * mCircleRadius * 2 + i * mLinePadding);
        }
        Log.i("wangjitao", "screenWidth:" + screenWidth + ",geiwidth:" + getWidth());

        //当位置发生改变时的回调监听
//        if (mOnDrawListener != null) {
//            mOnDrawListener.ondrawIndicator();
//            Log.i("wangjitao", "onSizeChanged");
//        }

    }

    /**
     * 绘制view
     *
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mOnDrawListener != null) {
            mOnDrawListener.ondrawIndicator();
            Log.i("wangjitao", "onDraw");
        }
        mUnComplectedPaint.setColor(mUnCompletedLineColor);
        mComplectedPaint.setColor(mCompletedLineColor);

        //首先绘制线
        for (int i = 0; i < mComplectedXPosition.size() - 1; i++) {
            //前一个ComplectedXPosition
            final float preComplectedXPosition = mComplectedXPosition.get(i);
            //后一个ComplectedXPosition
            final float afterComplectedXPosition = mComplectedXPosition.get(i + 1);
            if (i < mComplectingPosition) {
                //判断在完成之前的所有的点画完成的线，这里是矩形(这里会有10像素的偏移，没懂)
                canvas.drawRect(preComplectedXPosition + mCircleRadius - 10, mLeftY, afterComplectedXPosition - mCircleRadius + 10, mRightY, mComplectedPaint);
            } else {
                mPath.moveTo(preComplectedXPosition + mCircleRadius, mCenterY);
                mPath.lineTo(afterComplectedXPosition - mCircleRadius, mCenterY);
                canvas.drawPath(mPath, mUnComplectedPaint);
            }
        }

        //再绘制图标
        for (int i = 0; i < mComplectedXPosition.size(); i++) {
            final float currentComplectedXPosition = mComplectedXPosition.get(i);
            //创建矩形
            Rect rect = new Rect((int) (currentComplectedXPosition - mCircleRadius), (int) (mCenterY - mCircleRadius), (int) (currentComplectedXPosition + mCircleRadius), (int) (mCenterY + mCircleRadius));
            if (i < mComplectingPosition) {
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
            } else if (i == mComplectingPosition && mComplectedXPosition.size() != 1) {
                canvas.drawCircle(currentComplectedXPosition, mCenterY, mCircleRadius * 1.1f, mComplectedPaint);
                mAttentionIcon.setBounds(rect);
                mAttentionIcon.draw(canvas);
            } else {
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
            }
        }

    }

    /**
     * 设置步骤的基本流程
     */
    public void setStepNum(List<StepBean> stepsBeanList) {
        this.mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();

        if (mStepBeanList != null && mStepBeanList.size() > 0) {
            for (int i = 0; i < mStepNum; i++) {
                StepBean stepsBean = mStepBeanList.get(i);
                {
                    if (stepsBean.getState() == StepBean.STEP_CURRENT) {
                        mComplectingPosition = i;
                    }
                }
            }
        }
        requestLayout();
    }

    /**
     * 设置监听
     *
     * @param onDrawListener
     */
    public void setOnDrawListener(OnDrawIndicatorListener onDrawListener) {
        mOnDrawListener = onDrawListener;
    }

    /**
     * 设置对view监听
     */
    public interface OnDrawIndicatorListener {
        void ondrawIndicator();
    }

    /**
     * 得到所有圆点所在的位置
     *
     * @return
     */
    public List<Float> getComplectedXPosition() {
        return mComplectedXPosition;
    }


    /**
     * 设置正在进行position
     *
     * @param complectingPosition
     */
    public void setComplectingPosition(int complectingPosition) {
        this.mComplectingPosition = complectingPosition;
        invalidate();
    }

    /**
     * 设置正在进行position
     */
    public int getComplectingPosition() {
        return this.mComplectingPosition;
    }

    /**
     * 设置流程步数
     *
     * @param stepNum 流程步数
     */
    public void setStepNum(int stepNum) {
        this.mStepNum = stepNum;
        invalidate();
    }

    /**
     * 设置未完成线的颜色
     *
     * @param unCompletedLineColor
     */
    public void setUnCompletedLineColor(int unCompletedLineColor) {
        this.mUnCompletedLineColor = unCompletedLineColor;
    }

    /**
     * 设置已完成线的颜色
     *
     * @param completedLineColor
     */
    public void setCompletedLineColor(int completedLineColor) {
        this.mCompletedLineColor = completedLineColor;
    }

    /**
     * 设置默认图片
     *
     * @param defaultIcon
     */
    public void setDefaultIcon(Drawable defaultIcon) {
        this.mDefaultIcon = defaultIcon;
    }

    /**
     * 设置已完成图片
     *
     * @param completeIcon
     */
    public void setCompleteIcon(Drawable completeIcon) {
        this.mCompleteIcon = completeIcon;
    }

    /**
     * 设置正在进行中的图片
     *
     * @param attentionIcon
     */
    public void setAttentionIcon(Drawable attentionIcon) {
        this.mAttentionIcon = attentionIcon;
    }
}
