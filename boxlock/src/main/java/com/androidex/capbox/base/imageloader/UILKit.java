package com.androidex.capbox.base.imageloader;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.widget.ImageView;

import com.androidex.capbox.BuildConfig;
import com.androidex.capbox.data.net.OkHttpImageDownloader;
import com.androidex.capbox.data.net.base.RequestClient;
import com.androidex.capbox.utils.CommonKit;
import com.androidex.capbox.utils.Constants;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * @author liyp
 * @version 1.0.0
 * @description
 * @createTime 2015/11/24
 * @editTime
 * @editor
 */
public class UILKit {

    private static UILKit instance = null;

    private static DisplayImageOptions headOptions = null;
    private static DisplayImageOptions productOptions = null;
    private static DisplayImageOptions productBannerOptions = null;
    private static DisplayImageOptions luckyOptions = null;
    private static DisplayImageOptions picOptions = null;

    /**
     * 初始化（外部调用）
     *
     * @param context
     */
    public static void init(Context context) {
        if (instance == null) {
            synchronized (UILKit.class) {
                if (instance == null) {
                    instance = new UILKit(context);
                }
            }
        }
    }

    /**
     * 初始化
     *
     * @param context
     */
    private UILKit(Context context) {
        File cacheDir = CommonKit.getDiskCacheDir(context, Constants.CONFIG.IMG_CACHE_DIR);

        ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context)
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(10 * 1024 * 1024)
                .diskCache(new UnlimitedDiskCache(cacheDir))
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .diskCacheSize(50 * 1024 * 1024) // 50 MiB
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .imageDownloader(new OkHttpImageDownloader(context, RequestClient.getInstance().getOkHttpClient()));
        if (BuildConfig.DEBUG) {
//            config.writeDebugLogs();
        }

        ImageLoader.getInstance().init(config.build());


//        headOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_user_head_not_login)
//                .showImageForEmptyUri(R.mipmap.ic_user_head_sample1)
//                .showImageOnFail(R.mipmap.ic_user_head_sample1)
//                .cacheOnDisk(true).cacheInMemory(true)
//                .resetViewBeforeLoading(true)
//                .displayer(new FadeInBitmapDisplayer(500)).build();
//
//        productOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_product_logo_sample1)
//                .showImageForEmptyUri(R.mipmap.ic_product_logo_sample1)
//                .showImageOnFail(R.mipmap.ic_product_logo_sample1)
//                .cacheOnDisk(true).cacheInMemory(true)
//                .resetViewBeforeLoading(true)
//                .displayer(new FadeInBitmapDisplayer(500)).build();
//
//        productBannerOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_banner_example_1)
//                .showImageForEmptyUri(R.mipmap.ic_banner_example_1)
//                .showImageOnFail(R.mipmap.ic_banner_example_1)
//                .cacheOnDisk(true).cacheInMemory(true)
//                .resetViewBeforeLoading(true)
//                .displayer(new FadeInBitmapDisplayer(500)).build();
//
//        luckyOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_product_logo_sample1)
//                .showImageForEmptyUri(R.mipmap.ic_product_logo_sample1)
//                .showImageOnFail(R.mipmap.ic_product_logo_sample1)
//                .cacheOnDisk(true).cacheInMemory(true)
//                .resetViewBeforeLoading(true)
//                .displayer(new FadeInBitmapDisplayer(500)).build();
//
//        picOptions = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_pic_thumb)
//                .showImageForEmptyUri(R.mipmap.ic_pic_thumb)
//                .showImageOnFail(R.mipmap.ic_pic_thumb)
//                .imageScaleType(ImageScaleType.EXACTLY)
//                .cacheOnDisk(false).cacheInMemory(false)
//                .resetViewBeforeLoading(true)
//                .displayer(new FadeInBitmapDisplayer(500)).build();
    }


    public static ImageLoader getLoader() {
        return ImageLoader.getInstance();
    }

    public static DisplayImageOptions getHeadOptions() {
        return headOptions;
    }

    public static DisplayImageOptions getProductOptions() {
        return productOptions;
    }

    public static DisplayImageOptions getLuckyOptions() {
        return luckyOptions;
    }

    public static DisplayImageOptions getPicOptions() {
        return picOptions;
    }

    public static void loadHead(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(getWrapperPath(path), imageView, headOptions);
    }

    public static void loadHeadLocal(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(path, imageView, headOptions);
    }

    public static void loadProduct(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(getWrapperPath(path), imageView, productOptions);
    }

    public static void loadProductBanner(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(getWrapperPath(path), imageView, productBannerOptions);
    }

    public static void loadLuckyImg(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(getWrapperPath(path), imageView, luckyOptions);
    }

    public static void downloadLuckyImg(String path, ImageLoadingListener listener) {
        ImageLoader.getInstance().loadImage(getWrapperPath(path), luckyOptions, listener);
    }

    public static void loadPicLocal(String path, ImageView imageView) {
        ImageLoader.getInstance().displayImage(path, imageView, picOptions);
    }


    public static Bitmap getBitmap(String path) {
        return ImageLoader.getInstance().loadImageSync(path);
    }

    private static String getWrapperPath(String path) {
        if (!TextUtils.isEmpty(path) && !path.contains(Constants.CONFIG.APP_BASIC_SERVER)) {
            return new StringBuilder(Constants.CONFIG.APP_BASIC_SERVER).append(path).toString();
        }
        return path;
    }

    /**
     * 高效加载大图
     *
     * @param res
     * @param resId
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res,
                                                         int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * 高效加载大图
     *
     * @param pathName
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap decodeSampledBitmapFromFile(String pathName,
                                                     int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, options);
    }

    public static Bitmap decodeSampleByteArray(byte[] data, int reqWidth,
                                               int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * 将bitmap转化成byte数组
     *
     * @param bmp
     * @return
     */
    public static byte[] getBitmapData(Bitmap bmp) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

}
