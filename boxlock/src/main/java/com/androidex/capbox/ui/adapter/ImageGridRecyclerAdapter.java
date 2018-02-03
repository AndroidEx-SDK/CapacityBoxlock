package com.androidex.capbox.ui.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.androidex.capbox.R;
import com.androidex.capbox.base.RecyclerAdapter;
import com.androidex.capbox.base.imageloader.UILKit;
import com.androidex.capbox.data.pojo.ImageItem;
import com.androidex.capbox.ui.widget.SquareImageView;
import com.androidex.capbox.utils.CommonKit;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author liyp
 * @version 1.0.0
 * @description 图片九宫格
 * @createTime 2015/12/23
 * @editTime
 * @editor
 */
public class ImageGridRecyclerAdapter extends RecyclerAdapter<ImageItem, ImageGridRecyclerAdapter.ViewHolder> {
    private boolean isCapture = false;    //是否需要拍照
    private int selectMaxCount = 1;     //选择的最大图片数量

    private List<ImageItem> selectedImageList = new ArrayList<ImageItem>();   //选择的图片

    private static final String ACTION_CAPTURE = "capture";  //拍照

    public static final int TAG_SELECT_CHANGE=0;    //选择的图片改变
    public static final int TAG_CAPTURE=1;  //拍照
    public static final int TAG_ITEM_CLICK=2;   //单击了照片


    public ImageGridRecyclerAdapter(Context context, boolean isCapture) {
        super(context);
        this.isCapture = isCapture;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_image_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ImageItem item = getDataSource().get(position);

        if (ACTION_CAPTURE.equals(item.getPath())) {
            //拍照
            UILKit.loadPicLocal("drawable://" + R.mipmap.ic_camera_red, holder.iv_img);
            setGone(holder.cb_checked);
        } else {
            UILKit.loadPicLocal(item.getPath(),holder.iv_img);
//            UILKit.getLoader().loadImage(item.getPath(),new ImageSize(holder.iv_img.getWidth(),holder.iv_img.getWidth()),UILKit.getPicOptions(),new SimpleImageLoadingListener(){
//
//                @Override
//                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                    super.onLoadingComplete(imageUri, view, loadedImage);
//                    holder.iv_img.setImageBitmap(loadedImage);
//                }
//            });
            setVisible(holder.cb_checked);
        }

        if (selectMaxCount==1){
            setGone(holder.cb_checked);
        }

        holder.cb_checked.setChecked(item.isChecked());

        holder.cb_checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!item.isChecked()) {
                    //图片未选中
                    if (getSelectedCount() + 1 <= selectMaxCount) {
                        //可以被选中
                        item.setIsChecked(true);
                        addAnimation(holder.cb_checked);
                        holder.cb_checked.setChecked(true);
                        addSelectImage(item);
                    } else {
                        //图片不能被选中
                        CommonKit.showErrorShort(context, "最多选择" + selectMaxCount + "张图片");
                        item.setIsChecked(false);
                        removeSelectImage(item);
                        holder.cb_checked.setChecked(false);
                    }
                } else {
                    //图片被选中
                    item.setIsChecked(false);
                    removeSelectImage(item);
                    holder.cb_checked.setChecked(false);
                }

                if (getItemClick() != null) {
                    getItemClick().onItemClick(position, item, TAG_SELECT_CHANGE);
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getItemClick()!=null){
                    if (ACTION_CAPTURE.equals(item.getPath())){
                        getItemClick().onItemClick(position,item,TAG_CAPTURE);
                    }else{
                        getItemClick().onItemClick(position,item,TAG_ITEM_CLICK);
                    }
                }
            }
        });
    }

    @Override
    public void setData(List<ImageItem> data) {
        getDataSource().clear();
        if (data != null) {
            getDataSource().addAll(data);
        }
        if (isCapture) {
            getDataSource().add(0, new ImageItem(ACTION_CAPTURE));
        }
        notifyDataSetChanged();
    }

    /**
     * 获取选中的数量
     *
     * @return
     */
    public int getSelectedCount() {
        return selectedImageList == null ? 0 : selectedImageList.size();
    }

    /**
     * 获取选择的图片
     *
     * @return
     */
    public List<ImageItem> getSelectedImageList() {
        return selectedImageList == null ? new ArrayList<ImageItem>() : selectedImageList;
    }

    /**
     * 获取选择的图片路径
     *
     * @return
     */
    public ArrayList<String> getSelectPath() {
        ArrayList<String> imagePathes = new ArrayList<String>();

        if (getDataSource() != null && getDataSource().size() > 0) {
            for (ImageItem item : getDataSource()) {
                if (item.isChecked()) {
                    imagePathes.add(item.getPath());
                }
            }
        }
        return imagePathes;
    }

    /**
     * 获取全部的图片路径
     *
     * @return
     */
    public ArrayList<String> getTotalPaths() {
        ArrayList<String> imagePaths = new ArrayList<String>();

        if (getDataSource() != null && getDataSource().size() > 0) {
            for (ImageItem item : getDataSource()) {
                if (!ACTION_CAPTURE.equals(item.getPath())) {
                    imagePaths.add(item.getPath());
                }
            }
        }

        return imagePaths;
    }

    /**
     * 设置最大选择数量
     *
     * @param selectMaxCount
     */
    public void setSelectMaxCount(int selectMaxCount) {
        this.selectMaxCount = selectMaxCount;
    }

    /**
     * 动画效果
     *
     * @param view
     */
    private void addAnimation(View view) {
        float[] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f,
                1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
        AnimatorSet set = new AnimatorSet();
        set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules),
                ObjectAnimator.ofFloat(view, "scaleY", vaules));
        set.setDuration(150);
        set.start();
    }

    /**
     * 加入选中的图片
     *
     * @param item
     */
    private void addSelectImage(ImageItem item) {
        if (selectedImageList == null) {
            selectedImageList = new ArrayList<ImageItem>();
        }
        if (!selectedImageList.contains(item)) {
            selectedImageList.add(item);
        }
    }

    /**
     * 取消选中的图片
     *
     * @param item
     */
    private void removeSelectImage(ImageItem item) {
        if (selectedImageList == null) {
            selectedImageList = new ArrayList<ImageItem>();
        }
        if (selectedImageList.contains(item)) {
            selectedImageList.remove(item);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.iv_img)
        SquareImageView iv_img;
        @Bind(R.id.cb_checked)
        CheckBox cb_checked;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
