package com.androidex.capbox.ui.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.androidex.capbox.R;


/**
 * @author liyp
 * @version 1.0.0
 * @description 剪裁图片控件
 * @createTime 2015/12/31
 * @editTime
 * @editor
 */
public class ClipImageLayout extends RelativeLayout {
    private ClipZoomImageView zoomImageView;
    private ClipImageBorderView clipImageBorderView;

    private int borderWidth; // 边框宽度
    private int srcId; // 显示资源id

    private int defBorderWidth = 250;

    public ClipImageLayout(Context context) {
        super(context);
    }

    public ClipImageLayout(Context context, AttributeSet set) {
        super(context, set);

        zoomImageView = new ClipZoomImageView(context);
        clipImageBorderView = new ClipImageBorderView(context);

        android.view.ViewGroup.LayoutParams lp = new LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.MATCH_PARENT);

        this.addView(zoomImageView, lp);
        this.addView(clipImageBorderView, lp);

        TypedArray typedArray = context.obtainStyledAttributes(set,
                R.styleable.ClipImageLayout);
        borderWidth = typedArray.getDimensionPixelSize(
                R.styleable.ClipImageLayout_clipBorderWidth, defBorderWidth);
        srcId = typedArray.getResourceId(R.styleable.ClipImageLayout_srcId, 0);
        typedArray.recycle();

        if (srcId != 0) {
            zoomImageView.setImageDrawable(getResources().getDrawable(srcId));
        }
        clipImageBorderView.setWidth(borderWidth);
        zoomImageView.setClipWidth(borderWidth);
    }

    public Bitmap clip() {
        return zoomImageView.clip();
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        clipImageBorderView.setWidth(borderWidth);
        zoomImageView.setClipWidth(borderWidth);
    }

    public void setImageBitmap(Bitmap bitmap) {
        zoomImageView.setImageBitmap(bitmap);
    }
}
