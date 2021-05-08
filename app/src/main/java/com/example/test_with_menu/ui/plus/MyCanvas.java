package com.example.test_with_menu.ui.plus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class MyCanvas extends View {
    public MyCanvas(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        Paint pBackground = new Paint();
        pBackground.setColor(Color.WHITE);
        canvas.drawRect(0, 0, 512, 512, pBackground);
        Paint pText = new Paint();
        pText.setColor(100);
        pText.setColor(Color.BLACK);
        pText.setTextSize(20);
        canvas.drawARGB(12, 13, 14, 15);
        canvas.drawText("Sample Text", 100, 100, pText);
    }
}