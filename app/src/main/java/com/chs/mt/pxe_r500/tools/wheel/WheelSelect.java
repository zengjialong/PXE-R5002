package com.chs.mt.pxe_r500.tools.wheel;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class WheelSelect {
    //黑框背景颜色
    public static final int COLOR_BACKGROUND = Color.parseColor("#77777777");
    //黑框的Y坐标起点、宽度、高度
    //private int startY;
    private int startX;
    private int width;
    private int height;
    //四点坐标
    private Rect rect = new Rect();
    //需要选择文本的颜色、大小、补白
    private String selectText;
    private int fontColor;
    private int fontSize;
    private int padding;
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

//    public WheelSelect(int startY, int width, int height, String selectText, int fontColor, int fontSize, int padding) {
//        this.startY = startY;
//        this.width = width;
//        this.height = height;
//        this.selectText = selectText;
//        this.fontColor = fontColor;
//        this.fontSize = fontSize;
//        this.padding = padding;
//        rect.left = 0;
//        rect.top = startY;
//        rect.right = width;
//        rect.bottom = startY + height;
//    }
    public WheelSelect(int startX, int width, int height, String selectText, int fontColor, int fontSize, int padding) {
        this.startX = startX;
        this.width = width;
        this.height = height;
        this.selectText = selectText;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.padding = padding;
        rect.left = startX;
        rect.top = 0;
        rect.right = startX + width;
        rect.bottom = height;
    }
//    public int getStartY() {
//        return startY;
//    }
    public int getStartX() {
        return startX;
    }
//    public void setStartY(int startY) {
//        this.startY = startY;
//    }
    public void setStartX(int startX) {
        this.startX = startX;
    }
    public void onDraw(Canvas mCanvas) {
        //绘制背景
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(COLOR_BACKGROUND);
//        mCanvas.drawRect(rect, mPaint);
        //绘制提醒文字
        if(selectText != null){
            //设置钢笔属性
        	//Log.d("BUG", "WheelSelect fontSize:" + fontSize);
            mPaint.setTextSize(fontSize);
            mPaint.setColor(fontColor);
            //得到字体的宽度
            int textWidth = (int)mPaint.measureText(selectText);
            //drawText的绘制起点是左下角,y轴起点为baseLine
            Paint.FontMetrics metrics =  mPaint.getFontMetrics();
            int baseLine = (int)(rect.centerY() + (metrics.bottom - metrics.top) / 2 - metrics.bottom);
            //在靠右边绘制文本
            mCanvas.drawText(selectText, rect.right - padding - textWidth, baseLine, mPaint);
        }
    }
}
