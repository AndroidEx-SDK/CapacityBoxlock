package com.androidex.capbox.ui.widget.image;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * @author liyp
 * @version 1.0.0
 * @description 剪裁图片边框
 * @createTime 2015/12/31
 * @editTime
 * @editor
 */
public class ClipImageBorderView extends View {
    private int horizontalPadding;
    private int verticalPadding;

    private int width;	//绘制矩形的宽高

    private int borderColor= Color.WHITE;
    private int borderWidth=1;

    private Paint paint;

    public ClipImageBorderView(Context context) {
        super(context);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    public ClipImageBorderView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        paint.setColor(Color.parseColor("#aa000000"));
        paint.setStyle(Paint.Style.FILL);

        horizontalPadding=(getWidth()-width)/2;
        verticalPadding=(getHeight()-width)/2;

        // 绘制左边1
        canvas.drawRect(0, 0, horizontalPadding, getHeight(), paint);
        // 绘制右边2
        canvas.drawRect(getWidth() - horizontalPadding, 0, getWidth(),
                getHeight(), paint);
        // 绘制上边3
        canvas.drawRect(horizontalPadding, 0, getWidth() - horizontalPadding,
                verticalPadding, paint);
        // 绘制下边4
        canvas.drawRect(horizontalPadding, getHeight() - verticalPadding,
                getWidth() - horizontalPadding, getHeight(), paint);
        // 绘制外边框
        paint.setColor(borderColor);
        paint.setStrokeWidth(borderWidth);
        paint.setStyle(Paint.Style.STROKE);
        canvas.drawRect(horizontalPadding, verticalPadding, getWidth()
                - horizontalPadding, getHeight() - verticalPadding, paint);

    }

    public void setWidth(int width) {
        this.width = width;
        invalidate();
    }


    public int getHorizontalPadding() {
        return horizontalPadding;
    }

}

