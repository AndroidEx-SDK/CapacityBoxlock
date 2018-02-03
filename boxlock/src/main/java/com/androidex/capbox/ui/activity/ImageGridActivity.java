package com.androidex.capbox.ui.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;

import com.androidex.capbox.R;
import com.androidex.capbox.base.BaseActivity;
import com.androidex.capbox.callback.ItemClickCallBack;
import com.androidex.capbox.data.pojo.ImageItem;
import com.androidex.capbox.ui.adapter.ImageGridRecyclerAdapter;
import com.androidex.capbox.ui.widget.SecondTitleBar;
import com.androidex.capbox.ui.widget.recyclerview.QTRecyclerView;
import com.androidex.capbox.utils.CommonKit;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

import static com.androidex.capbox.utils.Constants.EXTRA_PACKAGE_NAME;


/**
 * @author liyp
 * @version 1.0.0
 * @description 图片九宫格
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public class ImageGridActivity extends BaseActivity {
    @Bind(R.id.rl_title)
    SecondTitleBar rl_title;
    @Bind(R.id.qtRecyclerView)
    QTRecyclerView qtRecyclerView;

    ImageGridRecyclerAdapter adapter;

    Uri cameraUri = null;

    private int selectMaxCount = 1; // 选中的数量
    private boolean isCapture = true; //是否需要拍照
    private int clipWidth = 250; // 剪裁的宽度

    public static final String OUT_SELECT_IMAGE_PATH = "selectImagePath";   //选择的图片路径，外部调用
    public static final String PARAM_SELECT_MAX_COUNT = "param_select_max_count"; //选择图片的最大数量
    public static final String PARAM_IS_CAPTURE = "param_is_capture"; //是否需要拍照
    public static final String PARAM_CLIP_WIDTH = "clipWidth";  //剪裁宽度

    public static final int REQ_CAMERA = 100; // 拍照
    public static final int REQ_IMAGE_CLIP = 300; // 图片剪裁


    @Override
    public void initData(Bundle savedInstanceState) {
        Bundle params = getIntent().getExtras();
        if (params != null) {
            if (params.containsKey(PARAM_IS_CAPTURE)) {
                isCapture = params.getBoolean(PARAM_IS_CAPTURE);
            }
            if (params.containsKey(PARAM_SELECT_MAX_COUNT)) {
                selectMaxCount = params.getInt(PARAM_SELECT_MAX_COUNT);
            }
            if (params.containsKey(PARAM_CLIP_WIDTH)) {
                clipWidth = params.getInt(PARAM_CLIP_WIDTH);
            }

            initAdapter();
            loadData();
        }

    }

    private void initAdapter() {
        if (adapter == null) {
            adapter = new ImageGridRecyclerAdapter(context, isCapture);
        }
        adapter.setItemClick(new ItemClickCallBack<ImageItem>() {
            @Override
            public void onItemClick(int position, ImageItem model, int tag) {
                super.onItemClick(position, model, tag);

                switch (tag) {
                    case ImageGridRecyclerAdapter.TAG_CAPTURE:
                        //拍照
                        try {
                            cameraUri = CommonKit.getOutputMediaFileUri(context,EXTRA_PACKAGE_NAME);
                            CommonKit.startCameraActivity(context, REQ_CAMERA, cameraUri);
                        } catch (Exception e) {
                            CommonKit.showErrorShort(context, "相机出现问题,请稍后再试!");
                        }

                        break;

                    case ImageGridRecyclerAdapter.TAG_ITEM_CLICK:
                        //单击
                        if (selectMaxCount == 1) {
                            // 进入剪裁界面
                            ImageClipActivity.lauch(context, model.getPath(), clipWidth, REQ_IMAGE_CLIP);
//                            Uri newUri = Uri.parse(PhotoUtils.getPath(context, data.getData()));
//                            PhotoUtils.cropImageUri(this, newUri, cropImageUri, 1, 1, OUTPUT_X, OUTPUT_Y, CODE_RESULT_REQUEST);
                        }
                        break;

                    case ImageGridRecyclerAdapter.TAG_SELECT_CHANGE:
                        //图片选择改变
                        rl_title.setTitle("图片选择(" + adapter.getSelectedCount()
                                + "/" + selectMaxCount + ")");
                        break;


                }
            }
        });
        adapter.setSelectMaxCount(selectMaxCount);
        qtRecyclerView.gridLayoutManager(context, 3)
                .defaultNoDivider();
        qtRecyclerView.setAdapter(adapter);
        qtRecyclerView.setOnRefreshAndLoadMoreListener(new QTRecyclerView.OnRefreshAndLoadMoreListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore(int page) {
            }
        });
    }


    private void loadData() {
        new AsyncTask<Void, Void, List<ImageItem>>() {

            @Override
            protected List<ImageItem> doInBackground(Void... params) {
                List<ImageItem> pictureList = new ArrayList<ImageItem>();

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = context.getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[]{"image/jpeg", "image/png"},
                        MediaStore.Images.Media.DATE_MODIFIED);

                if (mCursor == null) {
                    return pictureList;
                }

                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    StringBuffer path = new StringBuffer("file://")
                            .append(mCursor.getString(mCursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA)));
                    pictureList.add(new ImageItem(path.toString()));
                }
                mCursor.close();

                return pictureList;
            }

            @Override
            protected void onPostExecute(List<ImageItem> result) {
                super.onPostExecute(result);
                adapter.setData(result);
            }
        }.execute();
    }

    @Override
    public void setListener() {
        rl_title.getRightIv().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {

                String photoPath = cameraUri.toString()
                        .replaceFirst("file:///", "/").trim();

                if (new File(photoPath).exists()) {
                    // 发送广播通知系统
                    sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, cameraUri));

                    adapter.addElement(isCapture ? 1 : 0, new ImageItem(cameraUri.toString()));
                }
            }
        } else if (requestCode == REQ_IMAGE_CLIP) {
            if (resultCode == Activity.RESULT_OK) {

                String filePath = data.getStringExtra(ImageClipActivity.OUT_IMAGE_PATH);      //剪裁后的文件路径

                Intent intent = new Intent(ImageGridActivity.this, getIntent()
                        .getClass());
                intent.putExtra(OUT_SELECT_IMAGE_PATH, filePath);
                setResult(Activity.RESULT_OK, intent);
                CommonKit.finishActivity(ImageGridActivity.this);
            }
        }
    }


    private void setResult() {
        Intent intent = new Intent(ImageGridActivity.this, getIntent()
                .getClass());
        intent.putStringArrayListExtra(OUT_SELECT_IMAGE_PATH,
                adapter.getSelectPath());
        setResult(Activity.RESULT_OK, intent);
        CommonKit.finishActivity(ImageGridActivity.this);
    }

//    public static void lauch(Activity activity, boolean isCapture, int selectMaxCount, int clipWidth, int requestCode) {
//        Bundle params = new Bundle();
//        params.putBoolean(PARAM_IS_CAPTURE, isCapture);
//        params.putInt(PARAM_SELECT_MAX_COUNT, selectMaxCount);
//        params.putInt(PARAM_CLIP_WIDTH, clipWidth);
//
//        CommonKit.startActivityForResult(activity, ImageGridActivity.class, params, requestCode);
//    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_image_grid;
    }
}
