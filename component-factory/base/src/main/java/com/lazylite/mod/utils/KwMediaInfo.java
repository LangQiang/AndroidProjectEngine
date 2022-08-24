package com.lazylite.mod.utils;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import com.lazylite.mod.utils.toast.KwToast;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author 李建衡：jianheng.li@kuwo.cn
 */

public class KwMediaInfo {
    public static List<String> getAudioTypes() {
        List<String> sMusicExtensions = new Vector<String>();

        try {
            Class<?> fileCls = Class.forName("android.media.MediaFile");
            Field field = fileCls.getDeclaredField("sFileTypeMap");
            field.setAccessible(true);
            Object obj = field.get(null);
            Map<String, Object> fileTypeMap = (Map<String, Object>) obj;

            field = fileCls.getDeclaredField("FIRST_AUDIO_FILE_TYPE");
            field.setAccessible(true);
            obj = field.get(null);
            int firstAudioFileType = (Integer) obj;

            field = fileCls.getDeclaredField("LAST_AUDIO_FILE_TYPE");
            field.setAccessible(true);
            obj = field.get(null);
            int lastAudioFileType = (Integer) obj;

            Field typeField = null;
            Field mimeField = null;

            for (Map.Entry<String, Object> entry : fileTypeMap.entrySet()) {
                String fileType = entry.getKey();
                Object o = entry.getValue();
                if (typeField == null || mimeField == null) {
                    typeField = o.getClass().getDeclaredField("fileType");
                    typeField.setAccessible(true);
                    mimeField = o.getClass().getDeclaredField("mimeType");
                    mimeField.setAccessible(true);
                }

                obj = typeField.get(o);
                int type = (Integer) obj;
                if (type < firstAudioFileType || type > lastAudioFileType)
                    continue;

                String ft = fileType.toLowerCase();

                // 李建衡：wav格式的支持不好，播放效果差。屏蔽 wanbing，增加屏蔽amr
                if (!"wav".equals(ft) && !".wav".equals(ft) && !"amr".equals(ft) && !".amr".equals(ft)) {
                    sMusicExtensions.add(ft);
                }
            }

            return sMusicExtensions;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Intent getOpenCameraIntent(Context context, String tmpPicFile) {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//		Uri imageUri = Uri.fromFile(new File(tmpPicFile));
        Uri imageUri = KwFileUtils.getUriForFile(context, new File(tmpPicFile));
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        ResolveInfo reInfo = context.getPackageManager().resolveActivity(
                openCameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (reInfo == null) {
            KwToast.show("请先安装相机");
            return null;
        }
        return openCameraIntent;
    }

    /**
     * 启动系统相册选择图片,兼容4.4以上的变化
     *
     * @return
     */
    public static Intent getAlbumPictureSelectIntent(Context context) {
        Intent openAlbumIntent = new Intent();
        openAlbumIntent.setType("image/*");
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT || DeviceInfo.isV5System()) {  //小米的5.0系统，也必须用get_content才是相册，下面的不支持，会打开文件管理器
            if (DeviceInfo.isV5System() == false) {
                openAlbumIntent.addCategory(Intent.CATEGORY_OPENABLE); //小米手机，加上这个category，会弹出应用选择界面，去掉会自动进相册，所以去掉
            }
            openAlbumIntent.setAction(Intent.ACTION_GET_CONTENT);
        } else {
            openAlbumIntent.addCategory(Intent.CATEGORY_OPENABLE);
            openAlbumIntent.setAction(Intent.ACTION_OPEN_DOCUMENT);//4.4以后的版本使用此Action
        }
        ResolveInfo reInfo = context.getPackageManager().resolveActivity(
                openAlbumIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (reInfo == null) {
            KwToast.show("请先安装相册");
            return null;
        }
        return openAlbumIntent;
    }

    /**
     * 获取系统相册返回的图片文件路径信息
     *
     * @param context
     * @param uri：传入的相册中的ＵＲＩ
     * @return
     */
    public static String getAlbumPictureFilePath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     *                      [url=home.php?mod=space&uid=7300]@return[/url] The value of
     *                      the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri,
                                        String selection, String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
