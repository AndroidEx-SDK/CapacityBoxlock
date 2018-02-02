package com.androidex.capbox.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.androidex.capbox.R;

/**
 * 圆形图片
 *
 * @author Alan
 */
public class RoundImageView extends ImageView {
    private int mBorderThickness = 0;
    private Context mContext;
    private int defaultColor = 0xFFFE9BB7;//圆形图片外面的圆环填充颜色
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;

    private int defaultWidth = 0;
    private int defaultHeight = 0;

    public RoundImageView(Context context) {
        super(context);
        mContext = context;

    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        setCustomAttributes(attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        setCustomAttributes(attrs);
    }

    private void setCustomAttributes(AttributeSet attrs) {
        TypedArray a = mContext.obtainStyledAttributes(attrs,
                R.styleable.roundedimageview);
        mBorderThickness = a.getDimensionPixelSize(
                R.styleable.roundedimageview_border_thickness, 5);
        mBorderOutsideColor = a
                .getColor(R.styleable.roundedimageview_border_outside_color,
                        defaultColor);
        mBorderInsideColor = a.getColor(
                R.styleable.roundedimageview_border_inside_color, defaultColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = null;
        if (drawable instanceof BitmapDrawable) {
            b = ((BitmapDrawable) drawable).getBitmap();
        } else {
            b = Bitmap
                    .createBitmap(
                            getWidth(),
                            getHeight(),
                            drawable.getOpacity() != PixelFormat.OPAQUE ? Config.ARGB_8888
                                    : Config.RGB_565);
            Canvas canvas1 = new Canvas(b);
            // canvas.setBitmap(bitmap);
            drawable.setBounds(0, 0, getWidth(), getHeight());
            drawable.draw(canvas1);
        }
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();

        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }

        int radius = 0;
        // if (mBorderInsideColor != defaultColor
        // && mBorderOutsideColor != defaultColor) {//
        // 瀹氫箟鐢讳袱涓竟妗嗭紝鍒嗗埆涓哄鍦嗚竟妗嗗拰鍐呭渾杈规
        // radius = (defaultWidth < defaultHeight ? defaultWidth
        // : defaultHeight) / 2 - 2 * mBorderThickness;
        //
        // drawCircleBorder(canvas, radius + mBorderThickness / 2,
        // mBorderInsideColor);
        //
        // drawCircleBorder(canvas, radius + mBorderThickness
        // + mBorderThickness / 2, mBorderOutsideColor);
        // } else if (mBorderInsideColor != defaultColor
        // && mBorderOutsideColor == defaultColor) {
        // radius = (defaultWidth < defaultHeight ? defaultWidth
        // : defaultHeight) / 2 - mBorderThickness;
        // drawCircleBorder(canvas, radius + mBorderThickness / 2,
        // mBorderInsideColor);
        // } else if (mBorderInsideColor == defaultColor
        // && mBorderOutsideColor != defaultColor) {
        radius = (defaultWidth < defaultHeight ? defaultWidth : defaultHeight)
                / 2 - mBorderThickness;
        drawCircleBorder(canvas, radius + mBorderThickness / 2,
                mBorderOutsideColor);
        // } else {// 娌℃湁杈规
        // radius = (defaultWidth < defaultHeight ? defaultWidth
        // : defaultHeight) / 2;
        // }
        Bitmap roundBitmap = getCroppedRoundBitmap(bitmap, radius);
        canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius, defaultHeight
                / 2 - radius, null);
    }

    /**
     * 鑾峰彇瑁佸壀鍚庣殑鍦嗗舰鍥剧墖
     *
     * @param radius 鍗婂緞
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp, int radius) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 涓轰簡闃叉瀹介珮涓嶇浉绛夛紝閫犳垚鍦嗗舰鍥剧墖鍙樺舰锛屽洜姝ゆ埅鍙栭暱鏂瑰舰涓浜庝腑闂翠綅缃渶澶х殑姝ｆ柟褰㈠浘鐗�
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 楂樺ぇ浜庡
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 鎴彇姝ｆ柟褰㈠浘鐗�
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 瀹藉ぇ浜庨珮
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap鍥炴敹(recycle瀵艰嚧鍦ㄥ竷灞�枃浠禭ML鐪嬩笉鍒版晥鏋�
        // bmp.recycle();
        // squareBitmap.recycle();
        // scaledSrcBmp.recycle();
        bmp = null;
        squareBitmap = null;
        scaledSrcBmp = null;
        return output;
    }

    /**
     * 杈圭紭鐢诲渾
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 鍘婚敮榻� */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
		/* 璁剧疆paint鐨勩�style銆�负STROKE锛氱┖蹇� */
        paint.setStyle(Paint.Style.STROKE);
		/* 璁剧疆paint鐨勫妗嗗搴� */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

}
