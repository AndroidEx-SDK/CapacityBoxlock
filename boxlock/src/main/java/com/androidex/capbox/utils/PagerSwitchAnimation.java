package com.androidex.capbox.utils;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager切换动画
 * Created by liyp on 16/11/27.
 */

public class PagerSwitchAnimation {

    private static PagerSwitchAnimation animation;

    private PagerSwitchAnimation() {

    }

    public static PagerSwitchAnimation Instance() {
        if (animation == null) {
            animation = new PagerSwitchAnimation();
        }
        return animation;
    }

    /**
     * 页面切换时的动画
     * <p>
     * #{ViewPagerTransforms}项目为我们提供了一些现成的PageTransformer来实现一些动画
     */
    public class MyPageTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            RLog.e("position =" + position);
            if (position <= -1) {
                page.setAlpha(1);
            } else if (position <= 0) {
                //ViewPager正在滑动时，页面左边的View       -1～0
                page.setAlpha(1);
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);
            } else if (position < 1) {
                //ViewPager正在滑动时，页面右边的View       0～1
                page.setAlpha(1 - position);//- position
                page.setTranslationX(0);
                page.setScaleX((float) (1 - position * 0.8));
                page.setScaleY((float) (1 - position * 0.8));
            } else if (position == 1) {
                page.setAlpha(1);//- position
                page.setTranslationX(0);
                page.setScaleX(1);
                page.setScaleY(1);
            } else {
                page.setAlpha(0);

            }
        }
    }

}
