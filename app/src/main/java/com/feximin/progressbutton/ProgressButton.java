package com.feximin.progressbutton;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Neo on 16/4/1.
 */
public class ProgressButton extends View {

    private int mProgressColor = 0xFF06EE00;
    private int mProgressBackgroundColor = 0xFF99F999;       //有进度的时候，非进度的部分
    private int mRoundCorner = 0;
    private int mProgress;
    private String mText = "";
    private int mTextColor = 0xFFFFFFFF;
    private float mTextSize;      //in sp
    private LinearGradient mBackgroundGradient;

    private final int MAX = 100;

    private Paint mTextPaint;
    private Paint mBackgroundPaint;
    public ProgressButton(Context context) {
        this(context, null);
    }

    public ProgressButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        if (attrs != null){
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ProgressButton);
            mProgressColor = ta.getColor(R.styleable.ProgressButton_progress_color, mProgressColor);
            mProgressBackgroundColor = ta.getColor(R.styleable.ProgressButton_progress_background_color, mProgressBackgroundColor);
            mText = ta.getString(R.styleable.ProgressButton_android_text);
            mTextSize = ta.getDimensionPixelSize(R.styleable.ProgressButton_android_textSize, (int) spToPx(13));
            if (mTextSize < 0) mTextSize = spToPx(13);
            mRoundCorner = ta.getDimensionPixelSize(R.styleable.ProgressButton_corner_radius, 0);
            if (mRoundCorner < 0) mRoundCorner = 0;
            mTextColor = ta.getColor(R.styleable.ProgressButton_android_textColor, mTextColor);

            ta.recycle();
        }
        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(mTextColor);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setDither(true);
        mBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundPaint.setDither(true);

    }

    public void setProgress(int progress){
        if (progress >= 0 && mProgress != progress){
            mProgress = progress;
            invalidate();
        }
    }

    public void setProgressColor(int color){
        if (color != mProgressColor){
            this.mProgressColor = color;
            invalidate();
        }
    }

    public void setProgressBackgroundColor(int color) {
        if (color != mProgressBackgroundColor){
            mProgressBackgroundColor = color;
            invalidate();
        }
    }

    public int getProgress(){
        return mProgress;
    }

    public void setRoundCorner(int radius){
        if (radius >= 0 && radius != mRoundCorner){
            mRoundCorner = radius;
            invalidate();
        }
    }


    private RectF mBackgroundRectF = new RectF();
    private Paint.FontMetricsInt mCurMetricsInt;
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getPaddingLeft() + getPaddingRight();
        int height = getPaddingTop() + getPaddingBottom();
        if (!TextUtils.isEmpty(mText)){
            Rect rect = new Rect();
            mTextPaint.getTextBounds(mText, 0, mText.length(), rect);
            width += rect.width();
        }
        //无论是否有文字内容都需要把文字的高度算上
        //http://blog.csdn.net/hursing/article/details/18703599
        mCurMetricsInt = mTextPaint.getFontMetricsInt();
        height += (Math.abs(mCurMetricsInt.bottom) + Math.abs(mCurMetricsInt.top));

        width = Math.max(width, mRoundCorner * 2);
        height = Math.max(height, mRoundCorner * 2);
        width = getDefaultSize3(width, widthMeasureSpec);
        height = getDefaultSize3(height, heightMeasureSpec);
        setMeasuredDimension(width, height);

        mBackgroundRectF.top = 0;
        mBackgroundRectF.left = 0;
        mBackgroundRectF.right = getMeasuredWidth();
        mBackgroundRectF.bottom = getMeasuredHeight();
    }

    private static int getDefaultSize3(int size, int measureSpec) {
        int result = size;
        int specMode = View.MeasureSpec.getMode(measureSpec);
        int specSize = View.MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case View.MeasureSpec.UNSPECIFIED:
                result = size;
                break;
            case View.MeasureSpec.AT_MOST:
                result = Math.min(size, specSize);
                break;
            case View.MeasureSpec.EXACTLY:
                result = specSize;
                break;
        }
        return result;
    }

    public void setText(String text){
        if ((TextUtils.isEmpty(text) && !TextUtils.isEmpty(mText))
                || (!TextUtils.isEmpty(text) && TextUtils.isEmpty(mText))
                || (text != mText)){
            this.mText = text;
            invalidate();
        }
    }

    private float spToPx(float sp){
        return getResources().getDisplayMetrics().scaledDensity * sp;
    }

    //sp
    public void setTextSize(float size){
        if (size > 0 && size != mTextSize){
            size = spToPx(size);
            if (size != mTextSize) {
                this.mTextSize = size;
                mTextPaint.setTextSize(mTextSize);
                invalidate();
            }
        }
    }

    public void setTextColor(int color){
        if (color != mTextColor){
            this.mTextColor = color;
            mTextPaint.setColor(mTextColor);
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawBackground(canvas);
        drawText(canvas);
    }

    private void drawBackground(Canvas canvas){
        if (mProgress > 0){
            float scale = mProgress / 100f;
            mBackgroundGradient = new LinearGradient(0, 0, getMeasuredWidth(), 0,
                    new int[]{mProgressColor, mProgressBackgroundColor},
                    new float[]{scale, scale},
                    Shader.TileMode.CLAMP
            );
            mBackgroundPaint.setColor(mProgressColor);
            mBackgroundPaint.setShader(mBackgroundGradient);
        }else{
            mBackgroundPaint.setColor(mProgressBackgroundColor);
        }
        canvas.drawRoundRect(mBackgroundRectF, mRoundCorner, mRoundCorner, mBackgroundPaint);
    }

    private void drawText(Canvas canvas){
        if (TextUtils.isEmpty(mText)) return;
        Rect rect = new Rect();
        int length = mText.length();
        int validW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int offset = 1;
        for (int i = 1; i <= length; i++){
            mTextPaint.getTextBounds(mText, 0, i, rect);
            offset = i;
            if (rect.width() > validW){
                break;
            }
        }
        int x = getPaddingLeft() + (validW - rect.width()) / 2;
        int validH = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        //top是负值
        int baseline = (validH - (mCurMetricsInt.bottom - mCurMetricsInt.top))/ 2  - mCurMetricsInt.top + getPaddingTop();

        canvas.drawText(mText, 0, offset, x, baseline, mTextPaint);
    }
}
