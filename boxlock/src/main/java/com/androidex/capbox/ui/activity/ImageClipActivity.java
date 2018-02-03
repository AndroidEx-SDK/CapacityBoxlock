package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.ui.widget.image.ClipImageLayout;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;

import java.io.File;
import java.io.FileOutputStream;

import butterknife.Bind;

/**
 * @author liyp
 * @version 1.0.0
 * @description 图片剪裁
 * @createTime 2015/12/31
 * @editTime
 * @editor
 */
public class ImageClipActivity extends BaseActivity {
    @Bind(R.id.rl_title)
    SecondTitleBar rl_title;
    @Bind(R.id.clipImageLayout)
    ClipImageLayout clipImageLayout;

    private String imagePath;
    private int borderWidth;

    public static final String PARAM_IMAGE_PATH = "imagePath";
    public static final String PARAM_BORDER_WIDTH = "borderWidth";

    public static final String OUT_IMAGE_PATH = "outImagePath";     //剪裁后的图片路径

    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(PARAM_IMAGE_PATH)) {
                imagePath = params.getString(PARAM_IMAGE_PATH);
            }
            if (params.containsKey(PARAM_BORDER_WIDTH)) {
                borderWidth = params.getInt(PARAM_BORDER_WIDTH, 480);
            }

            clipImageLayout.setBorderWidth(borderWidth);
            if (!TextUtils.isEmpty(imagePath)) {
                imagePath = imagePath.replaceFirst("file:///", "/");
            }
            clipImageLayout.setImageBitmap(BitmapFactory.decodeFile(imagePath));
        }
    }

    @Override
    public void setListener() {
        rl_title.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(ImageClipActivity.this, getIntent().getClass());

                Bitmap clipBmp = clipImageLayout.clip();
                FileOutputStream os = null;

                try {
                    File file = CommonKit.getOutputMediaFile(context, Constants.CONFIG.IMG_CACHE_DIR);
                    os = new FileOutputStream(file);
                    clipBmp.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();

                    intent.putExtra(OUT_IMAGE_PATH, file.getAbsolutePath());

                    setResult(Activity.RESULT_OK, intent);
                    CommonKit.finishActivity(ImageClipActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                    CommonKit.finishActivity(ImageClipActivity.this);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    public static void lauch(Activity activity, String imagePath, int clipWidth, int requestCode) {
        Bundle params = new Bundle();
        params.putString(PARAM_IMAGE_PATH, imagePath);
        params.putInt(PARAM_BORDER_WIDTH, clipWidth);

        CommonKit.startActivityForResult(activity, ImageClipActivity.class, params, requestCode);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_image_clip;
    }
}
