package vn.android.photomaker.utils;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vn.android.photomaker.cache.CacheUtil;
import vn.android.photomaker.cache.MemoryCache;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;


/**
 * This class is used for implement with bitmap.
 * 
 * @author tuyendn
 * 
 */
public class ImageLoader {

	private static MemoryCache memoryCache = null;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	ExecutorService executorService;
	private Context mContext;

	public ImageLoader(Context context) {
		if (memoryCache == null) {
			memoryCache = new MemoryCache();
		}
		executorService = Executors.newFixedThreadPool(5);
		this.mContext = context;
	}

	/**
	 * Load bitmap to cache.
	 * 
	 * @param {resID} ID of image.
	 * @param {key} Key in cache.
	 * @param {width} Width of image.
	 * @param {height} Height of image.
	 * @return
	 */
	public Bitmap loadImageFromResource(int resID, String key, int width,
			int height) {
		Bitmap bmp = memoryCache.get(key);
		if (bmp == null) {
			try {
				bmp = CacheUtil.getBitmapFromDiskCache(key);
				if (bmp == null) {
					if (width == -1) {
						bmp = BitmapFactory.decodeResource(
								mContext.getResources(), resID);
					} else {
						bmp = ImageUtils.decodeSampledBitmapFromResource(
								mContext.getResources(), resID, width, height);
					}
					CacheUtil.addBitmapToCache(key, bmp);
				}
				if (bmp != null) {
					memoryCache.put(key, bmp);
				}
			} catch (Throwable ex) {
				if (ex instanceof OutOfMemoryError) {
					memoryCache.clear();
					System.gc();
				}
				return null;
			}
		}
		return bmp;
	}

	/**
	 * Set background to view.
	 * 
	 * @param resID
	 * @param key
	 * @param v
	 */
	@SuppressWarnings("deprecation")
	public void setBackground(int resID, String key, View v, int width,
			int height) {
		Bitmap bmp = memoryCache.get(key);
		if (bmp == null) {
			try {
				bmp = CacheUtil.getBitmapFromDiskCache(key);
				if (bmp == null) {
					if (width == -1) {
						bmp = BitmapFactory.decodeResource(
								mContext.getResources(), resID);
					} else {
						bmp = ImageUtils.decodeSampledBitmapFromResource(
								mContext.getResources(), resID, width, height);
					}
				}
				if (bmp != null) {
					memoryCache.put(key, bmp);
				}
			} catch (Throwable ex) {
				if (ex instanceof OutOfMemoryError) {
					memoryCache.clear();
					System.gc();
				}
			}
		}
		ViewUtils.setNullLayout(v);
		v.setBackgroundDrawable(new BitmapDrawable(mContext.getResources(), bmp));
	}

	public void DisplayImage(String url, ImageView imageView) {
		imageViews.put(imageView, url);
		Bitmap bitmap = memoryCache.get(url);
		ViewUtils.setNullViewDrawable(imageView);
		if (bitmap != null)
			imageView.setImageBitmap(bitmap);
		else {
			queuePhoto(url, imageView);
			imageView.setImageBitmap(null);
		}
	}

	private void queuePhoto(String url, ImageView imageView) {
		PhotoToLoad p = new PhotoToLoad(url, imageView);
		executorService.submit(new PhotosLoader(p));
	}

	public Bitmap getBitmap(String url) {

		// from SD cache
		Bitmap b = CacheUtil.getBitmapFromDiskCache(url);
		if (b != null)
			return b;

		// from web
		try {

			// Download bitmap.
			Bitmap bitmapDownload = null;
			try {
				bitmapDownload = DownloadImage.download(url);
			} catch (Exception e) {
				bitmapDownload = null;
			}
			if (bitmapDownload != null) {
				CacheUtil.addBitmapToCache(url, bitmapDownload);
			}
			return bitmapDownload;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError) {
				memoryCache.clear();
				System.gc();
			}
			return null;
		}
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;

		public PhotoToLoad(String u, ImageView i) {
			url = u;
			imageView = i;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// Used to display bitmap in the UI thread
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null)
				photoToLoad.imageView.setImageBitmap(bitmap);
			else
				photoToLoad.imageView.setImageBitmap(null);
		}
	}

	public void clearCache() {
		memoryCache.clear();
	}

}
