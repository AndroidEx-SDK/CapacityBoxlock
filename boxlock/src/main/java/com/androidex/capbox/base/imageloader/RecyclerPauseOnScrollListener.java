package com.androidex.capbox.base.imageloader;

import android.support.v7.widget.RecyclerView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author liyp
 * @version 1.0.0
 * @description UIL加载RecyclerView辅助
 * @createTime 2015/11/24
 * @editTime
 * @editor
 */
public class RecyclerPauseOnScrollListener extends RecyclerView.OnScrollListener {
    private ImageLoader imageLoader;

    public RecyclerPauseOnScrollListener(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);

        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (imageLoader != null) {
                    imageLoader.resume();
                }
                break;

            case RecyclerView.SCROLL_STATE_SETTLING:
                imageLoader.pause();
                break;
        }
    }
}
