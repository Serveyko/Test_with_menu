package com.example.test_with_menu;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class PathView extends View {

    Paint mPaint;
    Path mPath;
    int mStrokeColor;
    float mStrokeWidth;

    float mProgress = 0.0f;
    float mLength = 0f;
    float mTotal;

    public PathView(Context context) {
        this(context, null);
        init();
    }

    public PathView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        init();
    }

    public PathView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mStrokeColor = Color.RED;
        mStrokeWidth = 8.0f;
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(mStrokeColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mStrokeWidth);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);

        setPath(new Path());
        // setPath2(new Path());
    }

    public void setPath(Path p) {
        mPath = p;
        PathMeasure measure = new PathMeasure(mPath, false);
        float mPathLength = measure.getLength();
    }


    public void setPath(List<float[][]> list) {
        Path p = new Path();
        p.moveTo(list.get(0)[0][0], list.get(1)[0][1]);

        for (int i = 1; i < list.size(); i++) {
            p.lineTo(list.get(i)[0][0], list.get(i)[0][1]);
            //if (i > 100)
            //p.moveTo(list.get(i)[0][0], list.get(i)[0][1]);
        }
        //p.setFillType(FillType.WINDING);
        setPath(p);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mTotal = (mLength - mLength * mProgress);
        PathEffect pathEffect = new DashPathEffect(new float[]{mLength, mLength}, mTotal);

        mPaint.setPathEffect(pathEffect);

        canvas.save();
        // canvas.translate(getPaddingLeft(), getPaddingTop());
        canvas.drawPath(mPath, mPaint);
        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(widthMeasureSpec);

        int measuredWidth, measuredHeight;

        if (widthMode == MeasureSpec.AT_MOST)
            throw new IllegalStateException("Use MATCH_PARENT");
        else
            measuredWidth = widthSize;

        if (heightMode == MeasureSpec.AT_MOST)
            throw new IllegalStateException("Use MATCH_PARENT");
        else
            measuredHeight = heightSize;

        setMeasuredDimension(measuredWidth, measuredHeight);
        setPath();
    }

    void setPath() {
        int cX = getWidth() / 2;
        int cY = getHeight() / 2;
        cY += 50;
        cX -= 50;
        List<float[][]> list = new ArrayList<float[][]>();

        for (int i = 0; i < 50; i++) {
            list.add(new float[][]{{cX--, cY++}});
        }

        for (int i = 0; i < 100; i++) {
            list.add(new float[][]{{cX--, cY--}});
        }

        for (int i = 0; i < 100; i++) {
            list.add(new float[][]{{cX++, cY--}});
        }

        for (int i = 0; i < 200; i++) {
            list.add(new float[][]{{cX++, cY++}});
        }

        for (int i = 0; i < 100; i++) {
            list.add(new float[][]{{cX++, cY--}});
        }
        for (int i = 0; i < 100; i++) {
            list.add(new float[][]{{cX--, cY--}});
        }

        for (int i = 0; i < 100; i++) {
            list.add(new float[][]{{cX--, cY++}});
        }

        setPath(list);
    }
}
