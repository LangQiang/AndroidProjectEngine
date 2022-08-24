package com.lazylite.mod.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.example.basemodule.R;
import com.lazylite.mod.App;
import com.lazylite.mod.log.LogMgr;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

// by hongze
public class ScreenUtility {
	public static int TITLE_BAR_DP = 25;

	/**
	 * 将需要的字体大px转化为sp
	 * 
	 * @param size
	 * @return
	 */
	public static float px2sp(Context context, float size) {
		if (size <= 0) {
			size = 15;
		}
		float realSize = (float) (size * (getDensity(context) - 0.1));
		return realSize;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		float scale = getDensity(context);
		return (int) (dpValue * scale + 0.5f);
	}

	public static int dip2px(float dpValue) {
	    return dip2px(App.getInstance(), dpValue);
    }

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		float scale = getDensity(context);
		return (int) (pxValue / scale + 0.5f);
	}

	
	/**
	 * 将需要的字体大sp转化为
	 * 
	 * @param size
	 * @return
	 */
	public static int sp2px(Context context, float size) {
		float scale = getDensity(context);
		if (size <= 0) {
			size = 15;
		}
		
        return (int) (size * scale + 0.5f);  
	}
	
	/**
	 * 将bitmap转换为圆角返回
	 * @param bitmap
	 * @param roundPixels 圆角度数
	 * @return
	 */
	public static Bitmap getRoundCornerImage(Bitmap bitmap, int roundPixels) {
		if (bitmap == null){
			return null;
		}
		Bitmap roundConcerImage = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(roundConcerImage);
		Paint paint = new Paint();
		Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		RectF rectF = new RectF(rect);
		paint.setAntiAlias(true);
		canvas.drawRoundRect(rectF, roundPixels, roundPixels, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, null, rect, paint);
		return roundConcerImage;
	}
	
	/**
	 * 构建 1123KB/4567KB 样式的已下载大小样式.
	 * @param
	 * @return
	 */
	public static String setCurrentSizeStyleKB(long currentSize, long totalSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(currentSize / 1024);
		sb.append("KB/");
		sb.append(totalSize / 1024);
		sb.append("KB");
		return sb.toString();
	}
	
	/**
	 * 构建 1.1M/4.5M 样式的已下载大小样式.
	 * @param
	 * @return
	 */
	public static String setCurrentSizeStyleMB(long currentSize, long totalSize) {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("%1$.2f", (float)(currentSize)/1024/1024))
		.append("M/")
		.append(String.format("%1$.2f", (float)(totalSize)/1024/1024))
		.append("M");
		
		return sb.toString();
	}

	public static int convertDpToPixelInt(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int px = (int) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	public static float convertDpToPixel(Context context, float dp) {

		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		float px = (float) (dp * (metrics.densityDpi / 160f));
		return px;
	}

	private static MediaScannerConnection msc = null;
	//保存到相册后，通知系统扫描相册，这样就能立马在相册里看到图片啦，第三方文件管理的应用可能不及时
	public static void sendScanBroadcast(final Context context, File file){
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
			new SingleMediaScanner(context, file);
        }else{
        	context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
        }
	}

	public static boolean saveBitmapForShare(Bitmap bitmap, String path) {
		final File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
		try {
			FileOutputStream fs = new FileOutputStream(file);
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fs);
			fs.flush();
			fs.close();
			return true;
		} catch (Exception e) {
		}
		return false;
	}

	public static Bitmap getViewSnapshot(View view) {
		if (view == null) {
			return null;
		}
		Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);

		Canvas c = new Canvas(bitmap);
		c.translate(-view.getScrollX(), -view.getScrollY());
		view.draw(c);
		return bitmap;
	}

	//通知相册只扫描这一个文件就可以
	public static class SingleMediaScanner implements MediaScannerConnectionClient {
		public Context mContext ;
		private MediaScannerConnection mMs;
		private File mFile;

		public SingleMediaScanner(Context context, File f) {
		    mFile = f;
		    mContext = context;
		    mMs = new MediaScannerConnection(context, this);
		    mMs.connect();
		}

		@Override
		public void onMediaScannerConnected() {
		    mMs.scanFile(mFile.getAbsolutePath(), null);
		}

		@Override
		public void onScanCompleted(String path, Uri uri) {
			mContext.sendBroadcast(new Intent(android.hardware.Camera.ACTION_NEW_PICTURE, uri));
			mContext.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE", uri));
		    mMs.disconnect();
			mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(mFile)));
		}

	}
	

	/**
	 * 缩放bitmap
	 *
	 * @param f 缩放的比例
	 * @param bitmap
	 * @return
	 */
	public static Bitmap postScale(Bitmap bitmap,float f) {
		Matrix matrix = new Matrix();
		matrix.postScale(f, f); //长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}



	public static Bitmap doBlur(Bitmap sentBitmap, int radius ,float smallScale,float bigScale) {
		if (radius < 1) {
			return null;
		}
		Bitmap bitmap = postScale(sentBitmap, smallScale);
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		int[] pix = new int[w * h];
		bitmap.getPixels(pix, 0, w, 0, 0, w, h);

		int wm = w - 1;
		int hm = h - 1;
		int wh = w * h;
		int div = radius + radius + 1;

		int r[] = new int[wh];
		int g[] = new int[wh];
		int b[] = new int[wh];
		int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
		int vmin[] = new int[Math.max(w, h)];

		int divsum = (div + 1) >> 1;
		divsum *= divsum;
		int dvCount = 256 * divsum;
		int dv[] = new int[dvCount];
		for (i = 0; i < 256 * divsum; i++) {
			dv[i] = (i / divsum);
		}

		yw = yi = 0;

		int[][] stack = new int[div][3];
		int stackpointer;
		int stackstart;
		int[] sir;
		int rbs;
		int r1 = radius + 1;
		int routsum, goutsum, boutsum;
		int rinsum, ginsum, binsum;

		for (y = 0; y < h; y++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			for (i = -radius; i <= radius; i++) {
				p = pix[yi + Math.min(wm, Math.max(i, 0))];
				sir = stack[i + radius];
				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);
				rbs = r1 - Math.abs(i);
				rsum += sir[0] * rbs;
				gsum += sir[1] * rbs;
				bsum += sir[2] * rbs;
				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}
			}
			stackpointer = radius;

			for (x = 0; x < w; x++) {
				if (rsum >= dvCount || gsum >= dvCount || bsum >= dvCount) {//判断防止越界
					if (bitmap != null && !bitmap.isRecycled()) {
						bitmap.recycle();
						bitmap = null;
					}
					return null;
				}
				r[yi] = dv[rsum];
				g[yi] = dv[gsum];
				b[yi] = dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (y == 0) {
					vmin[x] = Math.min(x + radius + 1, wm);
				}
				p = pix[yw + vmin[x]];

				sir[0] = (p & 0xff0000) >> 16;
				sir[1] = (p & 0x00ff00) >> 8;
				sir[2] = (p & 0x0000ff);

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[(stackpointer) % div];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi++;
			}
			yw += w;
		}
		for (x = 0; x < w; x++) {
			rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
			yp = -radius * w;
			for (i = -radius; i <= radius; i++) {
				yi = Math.max(0, yp) + x;

				sir = stack[i + radius];

				sir[0] = r[yi];
				sir[1] = g[yi];
				sir[2] = b[yi];

				rbs = r1 - Math.abs(i);

				rsum += r[yi] * rbs;
				gsum += g[yi] * rbs;
				bsum += b[yi] * rbs;

				if (i > 0) {
					rinsum += sir[0];
					ginsum += sir[1];
					binsum += sir[2];
				} else {
					routsum += sir[0];
					goutsum += sir[1];
					boutsum += sir[2];
				}

				if (i < hm) {
					yp += w;
				}
			}
			yi = x;
			stackpointer = radius;
			for (y = 0; y < h; y++) {
				// Preserve alpha channel: ( 0xff000000 & pix[yi] )
				pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16) | (dv[gsum] << 8) | dv[bsum];

				rsum -= routsum;
				gsum -= goutsum;
				bsum -= boutsum;

				stackstart = stackpointer - radius + div;
				sir = stack[stackstart % div];

				routsum -= sir[0];
				goutsum -= sir[1];
				boutsum -= sir[2];

				if (x == 0) {
					vmin[y] = Math.min(y + r1, hm) * w;
				}
				p = x + vmin[y];

				sir[0] = r[p];
				sir[1] = g[p];
				sir[2] = b[p];

				rinsum += sir[0];
				ginsum += sir[1];
				binsum += sir[2];

				rsum += rinsum;
				gsum += ginsum;
				bsum += binsum;

				stackpointer = (stackpointer + 1) % div;
				sir = stack[stackpointer];

				routsum += sir[0];
				goutsum += sir[1];
				boutsum += sir[2];

				rinsum -= sir[0];
				ginsum -= sir[1];
				binsum -= sir[2];

				yi += w;
			}
		}

		bitmap.setPixels(pix, 0, w, 0, 0, w, h);

		Bitmap resultBitmap = postScale(bitmap,bigScale);
		if(bitmap != null && !bitmap.isRecycled()){
			bitmap.recycle();
			bitmap = null;
		}
		return resultBitmap;
	}

	public static final int MEASURE_TYPE_WIDHT = 0;
	public static final int MEASURE_TYPE_HEIGHT = 1;

	/**
	 * 获取控件宽度
	 */
	public static float getViewRealDimens(View view, int type) {
		view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
		switch (type) {
			case MEASURE_TYPE_HEIGHT:
				return view.getMeasuredHeight();
			case MEASURE_TYPE_WIDHT:
				return view.getMeasuredWidth();
			default:
				return 0;
		}
	}

	public static void setBackgroundAlpha(Activity activity, float bgAlpha) {
		if (activity == null) {
			return;
		}
		WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
		lp.alpha = bgAlpha; //0.0-1.0
		activity.getWindow().setAttributes(lp);
	}


	//返回的是dp
	public static int getTitleBarHeightDP(Context context) {
		int height = TITLE_BAR_DP;
		if (context != null) {
			try {
				int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
				if (resourceId > 0) {
					int dimensionPixelSize = context.getResources().getDimensionPixelSize(resourceId);
					height = ScreenUtility.px2dip(context, dimensionPixelSize);
				}
			} catch (Exception e) {
				height = TITLE_BAR_DP;
			}
			if (hasNotchInScreen(context)) {
				//hw返回的是px 转一下dp
				int hwStatusBarHeight = getStatusBarHeight(context);
				return hwStatusBarHeight > 0 ? px2dip(context, hwStatusBarHeight) : height;
			}
		}
		return height;
	}

	//华为手机判断是不是挖孔手机
	public static boolean hasNotchInScreen(Context context) {
		boolean ret = false;
		try {
			ClassLoader cl = context.getClassLoader();
			Class HwNotchSizeUtil = cl.loadClass("com.huawei.android.util.HwNotchSizeUtil");
			Method get = HwNotchSizeUtil.getMethod("hasNotchInScreen");
			ret = (boolean) get.invoke(HwNotchSizeUtil);
		} catch (ClassNotFoundException e) {
			LogMgr.e("ScreenUtility", "hasNotchInScreen ClassNotFoundException");
		} catch (NoSuchMethodException e) {
			LogMgr.e("ScreenUtility", "hasNotchInScreen NoSuchMethodException");
		} catch (Exception e) {
			LogMgr.e("ScreenUtility", "hasNotchInScreen Exception");
		} finally {
			return ret;
		}
	}

	//获取状态栏高度
	private static int getStatusBarHeight(Context context) {
		int result = 0;
		int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
		if (resourceId > 0) {
			result = context.getResources().getDimensionPixelSize(resourceId);
		}
		return result;
	}


	//CDUtil中拷贝过来的 暂时保持之前的调用防止 之后统一
	public static int getStatusHeight (Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}


	public static int getBottomHeightPx(Context context){
		return context.getResources().getDimensionPixelSize(R.dimen.lrlite_base_bottom_height);
	}

	private static float sDensity;

	public static float getDensity(Context context) {
		if (sDensity == 0) {
			try {
				DisplayMetrics dm = new DisplayMetrics();
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
					((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRealMetrics(dm);
				} else {
					((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(dm);
				}
				sDensity = dm.density;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return sDensity;
	}

	//保存bitmap到手机
	public static String addBitmapToAlbum(Context context, Bitmap bm) {
		if(bm==null||context==null){
			return null;
		}
		String uriStr = null;
		try {
			uriStr = MediaStore.Images.Media.insertImage(context.getContentResolver(), bm, UUID.randomUUID().toString(), "");
		}catch (Exception e){  //有些手机上会禁止此操作权限，造成安全
			return null;
		}
		if(uriStr == null){
			return null;
		}
		final String picPath  = KwMediaInfo.getAlbumPictureFilePath(context,Uri.parse(uriStr));
		if(picPath == null) {
			return null;
		}
		try{
			sendScanBroadcast(context,new File(picPath));  //发送系统广播，通知相册更新

			ContentResolver contentResolver = context.getContentResolver();
			ContentValues values = new ContentValues(4);
			values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
			values.put(MediaStore.Images.Media.ORIENTATION, 0);
			values.put(MediaStore.Images.Media.DATA, picPath);
			Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			return picPath;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static boolean addBitmapToAlbum(Context context, String path) {
		if(path==null||context==null){
			return false;
		}
		String uriStr = null;
		try {
			uriStr = MediaStore.Images.Media.insertImage(context.getContentResolver(), path, UUID.randomUUID().toString(), "元惜分享");
		}catch (Exception e){  //有些手机上会禁止此操作权限，造成安全
			return false;
		}
		if(uriStr == null){
			return false;
		}
		final String picPath  = KwMediaInfo.getAlbumPictureFilePath(context,Uri.parse(uriStr));
		if(picPath == null) {
			return false;
		}
		try{
			sendScanBroadcast(context,new File(picPath));  //发送系统广播，通知相册更新

//			ContentResolver contentResolver = context.getContentResolver();
//			ContentValues values = new ContentValues(4);
//			values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
//			values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
//			values.put(MediaStore.Images.Media.ORIENTATION, 0);
//			values.put(MediaStore.Images.Media.DATA, picPath);
//			Uri uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
			return true;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 判断是否有NavigationBar
	 *
	 * @param activity
	 * @return
	 */
	public static boolean checkHasNavigationBar(Activity activity) {
		WindowManager windowManager = activity.getWindowManager();
		Display d = windowManager.getDefaultDisplay();

		DisplayMetrics realDisplayMetrics = new DisplayMetrics();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			d.getRealMetrics(realDisplayMetrics);
		}

		int realHeight = realDisplayMetrics.heightPixels;
		int realWidth = realDisplayMetrics.widthPixels;

		DisplayMetrics displayMetrics = new DisplayMetrics();
		d.getMetrics(displayMetrics);

		int displayHeight = displayMetrics.heightPixels;
		int displayWidth = displayMetrics.widthPixels;

		return (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
	}

}
