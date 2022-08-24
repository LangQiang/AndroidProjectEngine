package com.lazylite.mod.imageloader.fresco.load.impl;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;

import com.facebook.binaryresource.BinaryResource;
import com.facebook.binaryresource.FileBinaryResource;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.disk.DiskCacheConfig;
import com.facebook.common.executors.UiThreadImmediateExecutorService;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.cache.DefaultCacheKeyFactory;
import com.facebook.imagepipeline.common.ImageDecodeOptions;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.core.ImagePipelineFactory;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.image.ImageInfo;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.lazylite.mod.imageloader.fresco.config.FrescoConstant;
import com.lazylite.mod.imageloader.fresco.config.ImageLoadConfig;
import com.lazylite.mod.imageloader.fresco.config.ImageViewSize;
import com.lazylite.mod.imageloader.fresco.listener.IDisplayImageListener;
import com.lazylite.mod.imageloader.fresco.listener.IDownloadImageListener;
import com.lazylite.mod.imageloader.fresco.supplier.BaseDisplayListenerSupplier;
import com.lazylite.mod.imageloader.fresco.supplier.BaseDownloadListenerSupplier;
import com.lazylite.mod.imageloader.fresco.supplier.BitmapMemoryCacheSupplier;
import com.lazylite.mod.imageloader.fresco.supplier.OkHttpNetworkFetcher;
import com.lazylite.mod.imageloader.fresco.util.FrescoUtil;
import com.lazylite.mod.log.LogMgr;
import com.lazylite.mod.utils.DeviceInfo;
import com.lazylite.mod.utils.KwDirs;

import java.io.File;

/**
 * Created by tiancheng on 2016/11/14
 */
public class FrescoImageLoader extends BaseImageLoader<SimpleDraweeView> {

    private final ImageLoadConfig mDefaultConfig;
    private boolean isShutDown;

    private static class SingletonHolder {
        private static final FrescoImageLoader INSTANCE = new FrescoImageLoader();
    }

    public static FrescoImageLoader getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private FrescoImageLoader() {
        mDefaultConfig = new ImageLoadConfig.Builder().create();
    }

    public void initialize(Context context) {

        if (context == null) {
            throw new RuntimeException("context is null");
        }

        context = !(context instanceof Application) ? context.getApplicationContext() : context;
        DiskCacheConfig diskCacheConfig = DiskCacheConfig.newBuilder(context)
                .setBaseDirectoryName(FrescoConstant.DEFAULT_DISK_CACHE_DIR_NAME)
                .setBaseDirectoryPath(new File(KwDirs.getDir(KwDirs.FRESCO)))
                .setMaxCacheSize(FrescoConstant.DEFAULT_MAX_DISK_CACHE_SIZE)
                .setMaxCacheSizeOnLowDiskSpace(FrescoConstant.DEFAULT_LOW_SPACE_DISK_CACHE_SIZE)
                .setMaxCacheSizeOnVeryLowDiskSpace(FrescoConstant.DEFAULT_VERY_LOW_SPACE_DISK_CACHE_SIZE)
                .build();

        ImagePipelineConfig pipelineConfig = ImagePipelineConfig.newBuilder(context)
                .setBitmapMemoryCacheParamsSupplier(new BitmapMemoryCacheSupplier())
                .setNetworkFetcher(new OkHttpNetworkFetcher())
                .setBitmapsConfig(FrescoConstant.DEFAULT_BITMAP_CONFIG)
                .setDownsampleEnabled(true)
                .setResizeAndRotateEnabledForNetwork(true)
                .setMainDiskCacheConfig(diskCacheConfig)
                .setResizeAndRotateEnabledForNetwork(true)
                .build();

        Fresco.initialize(context, pipelineConfig);
//        Fresco.initialize(context);
    }

    @Override
    public void load(SimpleDraweeView view, Uri uri, ImageLoadConfig config, IDisplayImageListener<ImageInfo> listener) {
        try {
            FrescoUtil.checkViewNotNull(view);
            FrescoUtil.checkUriIsLegal(uri);
        } catch (Exception e) {
            uri = Uri.parse("asset://KwException.png");
            if (!TextUtils.isEmpty(uri.getPath())){
                e.printStackTrace();
            }
        }
        display(view, uri, listener, config);
    }

    @Override
    public void load(Uri uri, int w, int h, IDownloadImageListener<Bitmap> listeren) {
        try {
            FrescoUtil.checkUriIsLegal(uri);
        } catch (Exception e) {
            uri = Uri.parse("asset://KwException.png");
            e.printStackTrace();
        }
        download(uri, listeren, w, h);
    }

    /**
     * 直接显示
     *
     * @param view
     * @param uri
     * @param listener
     * @param config
     */
    private void display(SimpleDraweeView view, Uri uri, IDisplayImageListener<ImageInfo> listener,
                         ImageLoadConfig config) {

        if (isShutDown || !Fresco.hasBeenInitialized()) {
            return;
        }

        if (config == null) {
            config = mDefaultConfig;
        }
        GenericDraweeHierarchyBuilder hierarchyBuilder = new GenericDraweeHierarchyBuilder(null);
        hierarchyBuilder.setFadeDuration(config.fadeDuration);
        hierarchyBuilder.setRoundingParams(config.roundingParams);
        hierarchyBuilder.setActualImageScaleType(config.scaleType == null ? FrescoConstant.DEFAULT_SCALE_TYPE : config.scaleType);

        if (config.loadingDrawable != null) {
            ScalingUtils.ScaleType type = getScaleType(config.loadingDrawableScaleType, config);
            hierarchyBuilder.setPlaceholderImage(config.loadingDrawable, type);
        }
        if (config.failureDrawable != null) {
            ScalingUtils.ScaleType type = getScaleType(config.failureDrawableScaleType, config);
            hierarchyBuilder.setFailureImage(config.failureDrawable, type);
        }
        if (config.pressedDrawable != null) {
            hierarchyBuilder.setPressedStateOverlay(config.pressedDrawable);
        }
        if (config.retryDrawable != null) {
            hierarchyBuilder.setRetryImage(config.retryDrawable);
        }
        if (config.overlayDrawable != null) {
            hierarchyBuilder.setOverlay(config.overlayDrawable);
        }
        if (config.progressDrawable != null) {
            hierarchyBuilder.setProgressBarImage(config.progressDrawable);
        }

        if (config.aspectRatio > 0) {
            view.setAspectRatio(config.aspectRatio);
        }

        GenericDraweeHierarchy hierarchy = hierarchyBuilder.build();

        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        requestBuilder.setAutoRotateEnabled(config.autoRotate);

        if (config.postprocessor != null) {
            requestBuilder.setPostprocessor(config.postprocessor);
        }

        //************************************按需压缩解码图片尺寸*******************************************
        int resizeWidth;
        int resizeHeight;
        if (config.resizeWidth > 0 && config.resizeHeight > 0) {
            resizeWidth = config.resizeWidth;
            resizeHeight = config.resizeHeight;
        } else {
            ImageViewSize size = FrescoUtil.getImageViewSize(view);
            resizeWidth = size.width;
            resizeHeight = size.height;
        }
        if (resizeWidth <= 0) {
            resizeWidth = (int) (DeviceInfo.WIDTH * 0.3);
        }
        if (resizeHeight <= 0) {
            resizeHeight = (int) (DeviceInfo.WIDTH * 0.3);
        }
        if (resizeWidth > 0 && resizeHeight > 0) {
            requestBuilder.setResizeOptions(new ResizeOptions(resizeWidth, resizeHeight));
        }
        requestBuilder.setImageDecodeOptions(ImageDecodeOptions.newBuilder().setDecodePreviewFrame(true).build());
        //************************************************************************************************

        ImageRequest imageRequest = requestBuilder.build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setControllerListener(BaseDisplayListenerSupplier.newInstance(listener))
                .setOldController(view.getController())
                .setAutoPlayAnimations(config.autoPlayAnimations)
                .setTapToRetryEnabled(config.tapToRetry)
                .setImageRequest(imageRequest)
                .build();

        view.setHierarchy(hierarchy);
        view.setController(controller);
    }

    private ScalingUtils.ScaleType getScaleType(ScalingUtils.ScaleType defaultType, ImageLoadConfig config) {
        ScalingUtils.ScaleType type;
        if (defaultType == null) {
            type = config.scaleType;
            if (type == null) {
                type = FrescoConstant.DEFAULT_SCALE_TYPE;
            }
        } else {
            type = defaultType;
        }
        return type;
    }

    /**
     * download
     * @param uri
     * @param listerer
     * @param w
     * @param h
     */
    private void download(final Uri uri, final IDownloadImageListener<Bitmap> listerer, int w, int h) {

        if (isShutDown || !Fresco.hasBeenInitialized()) {
            return;
        }

        ImageRequestBuilder requestBuilder = ImageRequestBuilder.newBuilderWithSource(uri);
        if (w > 0 && h > 0) {
            requestBuilder.setResizeOptions(new ResizeOptions(w, h));
        }
        ImageRequest imageRequest = requestBuilder.build();

        DataSource<CloseableReference<CloseableImage>> dataSource = Fresco.getImagePipeline().fetchDecodedImage(imageRequest, null);
        dataSource.subscribe(BaseDownloadListenerSupplier.newInstance(listerer),
                UiThreadImmediateExecutorService.getInstance());
    }

    public void shutDown() {
        isShutDown = true;
        Fresco.shutDown();
    }

    /**
     * 在listview快速滑动时
     */
    public void pause() {
        //Fresco.getImagePipeline().pause();
    }

    /**
     * 当滑动停止时
     */
    public void resume() {
        //Fresco.getImagePipeline().resume();
    }

    /**
     * 直接加载到本地disk
     * @param url
     */
    public static void prefetchToDiskCache(Uri uri) {
        if (uri == null) {
            return;
        }
        Fresco.getImagePipeline().prefetchToDiskCache(ImageRequestBuilder
                .newBuilderWithSource(uri)
                .setLowestPermittedRequestLevel(ImageRequest.RequestLevel.FULL_FETCH)
                .setProgressiveRenderingEnabled(false)
                .build(), null);
    }

    /**
     * 是否已加载到本地disk
     * @param uri
     * @return
     */
    public boolean isInDiskStorageCache(Uri uri) {
        if (uri == null) {
            return false;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
        return ImagePipelineFactory.getInstance().getMainFileCache().hasKey(cacheKey);
    }

    /**
     * 通过uri拿到本地sd卡上的cnt图片文件路径
     *
     * @param uri
     * @return
     */
    public String getDiskStorageCachePath(Uri uri) {
        return getDiskStorageCache(uri) == null ? null : getDiskStorageCache(uri).getAbsolutePath();
    }


    /**
     * 通过uri拿到本地sd卡上的cnt图片文件
     *
     * @param uri
     * @return
     */
    public File getDiskStorageCache(Uri uri) {
        if (!isInDiskStorageCache(uri)) {
            return null;
        }
        ImageRequest imageRequest = ImageRequest.fromUri(uri);
        CacheKey cacheKey = DefaultCacheKeyFactory.getInstance().getEncodedCacheKey(imageRequest, null);
        BinaryResource resource = ImagePipelineFactory.getInstance().getMainFileCache().getResource(cacheKey);
        if (resource == null) {
            return null;
        }
        return ((FileBinaryResource) resource).getFile();
    }
}
