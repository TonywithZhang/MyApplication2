package com.tec.zhang.prv.personalized;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import com.tec.zhang.prv.R;

/**
 * Created by Administrator on 2017/4/26.
 */

public class CircleProgressView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = "CircleProgressView";

    private  int circleSolidColor,circleFrameColor,circleRadius,progressColor,textColor,mCenterX,mCenterY;
    private final int CIRCLE_FRAME_WIDTH = 4;
    private final int PROGRESS_WIDTH = 4;

    private Rect mBounds;
    private Paint mPaint;
    private RectF mArcRectF;

    private final String mText = "跳过";

    private  long TIME_IN_MILLIS = 3000;

    private  OnProgressListener progressListener;

    private int progress = 100;

    private ProgressType mProgressType = ProgressType.COUNT_BACK;

    public void setProgressType(ProgressType progressType){
        this.mProgressType = progressType;
        resetProgress();
        invalidate();
    }

    private void resetProgress() {

    }

    private void init(){
        mPaint = new Paint();
        mBounds = new Rect();
        mArcRectF = new RectF();
    }


    public CircleProgressView(Context context){
        super(context);
        init();
    }

    public CircleProgressView(Context context, AttributeSet attributeSet){
        super(context,attributeSet);
        init();
        TypedArray typedArray = context.obtainStyledAttributes(attributeSet, R.styleable.circleProgressView);
        if (typedArray != null){
            if (typedArray.hasValue(R.styleable.circleProgressView_circleSolidColor)){
                circleSolidColor = typedArray.getColor(R.styleable.circleProgressView_circleSolidColor,0);
            }else {
                circleSolidColor = typedArray.getColor(R.styleable.circleProgressView_circleSolidColor, Color.parseColor("#D3D3D3"));
            }
            if (typedArray.hasValue(R.styleable.circleProgressView_circleFrameColor)){
                circleFrameColor = typedArray.getColor(R.styleable.circleProgressView_circleFrameColor,0);
            }else {
                circleFrameColor = typedArray.getColor(R.styleable.circleProgressView_circleFrameColor,Color.parseColor("#A9A9A9"));
            }
            if (typedArray.hasValue(R.styleable.circleProgressView_textColor)){
                textColor = typedArray.getColor(R.styleable.circleProgressView_textColor,0);
            }else {
                textColor = typedArray.getColor(R.styleable.circleProgressView_textColor,Color.parseColor("#ffffff"));
            }

            if (typedArray.hasValue(R.styleable.circleProgressView_progressColor)){
                progressColor = typedArray.getColor(R.styleable.circleProgressView_progressColor,0);
            }else {
                progressColor = typedArray.getColor(R.styleable.circleProgressView_progressColor,Color.parseColor("#0000FF"));
            }

            typedArray.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        if (width > height){
            height = width;
            circleRadius = width/2;
        }else {
            width = height;
            circleRadius = height/2;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        getDrawingRect(mBounds);
        mCenterX = mBounds.centerX();
        mCenterX = mBounds.centerY();

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(circleSolidColor);
        canvas.drawCircle(mCenterX,mCenterY,circleRadius,mPaint);

        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(CIRCLE_FRAME_WIDTH);
        mPaint.setColor(circleFrameColor);
        canvas.drawCircle(mCenterX,mCenterY,circleRadius - circleFrameColor,mPaint);

        Paint textPaint = getPaint();
        textPaint.setColor(textColor);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        float textY = mCenterY - (textPaint.descent() + textPaint.ascent())/2;
        canvas.drawText(mText,mCenterX,textY,textPaint);

        mPaint.setColor(progressColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(PROGRESS_WIDTH);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcRectF.set(mBounds.left + PROGRESS_WIDTH,mBounds.top + PROGRESS_WIDTH,mBounds.right - PROGRESS_WIDTH,mBounds.bottom - PROGRESS_WIDTH);
        canvas.drawArc(mArcRectF,-90,360*progress/100,false,mPaint);
    }

    public enum ProgressType{
        COUNT,COUNT_BACK;
    }

    public interface OnProgressListener{
        void onProgress(int progress);
    }

    public void start(){
        stop();
        post(progressChangeTask);
    }
    public void stop(){
        removeCallbacks(progressChangeTask);
    }

    public void restart(){
        resetProgress();
        start();
    }
    private Runnable progressChangeTask = new Runnable() {
        @Override
        public void run() {
            removeCallbacks(this);
            switch (mProgressType){
                case COUNT:
                    progress += 1;
                    break;
                case COUNT_BACK:
                    progress -= 1;
                    break;
            }
            if (progress >= 0 && progress <= 100){
                if (progressListener != null){
                    progressListener.onProgress(progress);
                }
                invalidate();
                postDelayed(progressChangeTask,TIME_IN_MILLIS/60);
            }else {
                progress = validateProgress(progress);
            }
        }
    };
    private int validateProgress(int progress){
        if (progress >100){
            progress = 100;
        }else if (progress < 0){
            progress = 0;
        }
        return progress;
    }

    public int getCircleSolidColor() {
        return circleSolidColor;
    }

    public void setCircleSolidColor(int circleSolidColor) {
        this.circleSolidColor = circleSolidColor;
    }

    public int getCircleFrameColor() {
        return circleFrameColor;
    }

    public void setCircleFrameColor(int circleFrameColor) {
        this.circleFrameColor = circleFrameColor;
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getProgressColor() {
        return progressColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    @Override
    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getmCenterX() {
        return mCenterX;
    }

    public void setmCenterX(int mCenterX) {
        this.mCenterX = mCenterX;
    }

    public int getmCenterY() {
        return mCenterY;
    }

    public void setmCenterY(int mCenterY) {
        this.mCenterY = mCenterY;
    }

    public int getCIRCLE_FRAME_WIDTH() {
        return CIRCLE_FRAME_WIDTH;
    }

    public int getPROGRESS_WIDTH() {
        return PROGRESS_WIDTH;
    }

    public Rect getmBounds() {
        return mBounds;
    }

    public void setmBounds(Rect mBounds) {
        this.mBounds = mBounds;
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public void setmPaint(Paint mPaint) {
        this.mPaint = mPaint;
    }

    public RectF getmArcRectF() {
        return mArcRectF;
    }

    public void setmArcRectF(RectF mArcRectF) {
        this.mArcRectF = mArcRectF;
    }

    public String getmText() {
        return mText;
    }

    public long getTIME_IN_MILLIS() {
        return TIME_IN_MILLIS;
    }

    public void setTIME_IN_MILLIS(long TIME_IN_MILLIS) {
        this.TIME_IN_MILLIS = TIME_IN_MILLIS;
        invalidate();
    }

    public OnProgressListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(OnProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public ProgressType getmProgressType() {
        return mProgressType;
    }

    public void setmProgressType(ProgressType mProgressType) {
        this.mProgressType = mProgressType;
    }

    public Runnable getProgressChangeTask() {
        return progressChangeTask;
    }

    public void setProgressChangeTask(Runnable progressChangeTask) {
        this.progressChangeTask = progressChangeTask;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                float x = event.getX();
                float y = event.getY();
                if (Math.abs(x - mBounds.centerX()) <= circleRadius*2 && Math.abs( y - mBounds.centerY()) <= circleRadius*2){
                    Log.d(TAG, "onTouchEvent: ");
                }
        }
        return super.onTouchEvent(event);
    }
}
