/**
 * 
 */
package vn.android.photomaker.cache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * @author TuyenDN
 * 
 */
public class CacheUtil {

	private static DiskLruCache mDiskCache;
	private final int DISK_CACHE_SIZE = 1024 * 1024 * 50; // 50MB
	private final String DISK_CACHE_SUBDIR = "photomaker_caches";

	/** Constructor. */
	public CacheUtil(Context context) {
		File cacheDir = getCacheDir(context, DISK_CACHE_SUBDIR);
		mDiskCache = DiskLruCache.openCache(context, cacheDir, DISK_CACHE_SIZE);
	}

	/** Clear cache. */
	public static void disCache() {
		if (mDiskCache != null) {
			mDiskCache.clearCache();
			mDiskCache = null;
			System.gc();
		}
	}

	/**
	 * Add bimap to cache.
	 */
	public static void addBitmapToCache(String key, Bitmap bitmap) {

		if (mDiskCache != null) {
			// Add to memory cache as before
			if (getBitmapFromDiskCache(key) == null) {
				mDiskCache.put(key, bitmap);
				return;
			}

			// Also add to disk cache
			if (!mDiskCache.containsKey(key)) {
				mDiskCache.put(key, bitmap);
				return;
			}
		}
	}

	public static void removeCache(String key) {
		if (mDiskCache.containsKey(key))
			mDiskCache.remove(key);
	}

	/**
	 * Get bitmap from disk cache.
	 */
	public static Bitmap getBitmapFromDiskCache(String key) {
		try {
			return mDiskCache.get(key);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates a unique subdirectory of the designated app cache directory.
	 * Tries to use external but if not mounted, falls back on internal storage.
	 */
	public static File getCacheDir(Context context, String uniqueName) {
		// Check if media is mounted or storage is built-in, if so, try and use
		// external cache dir
		// otherwise use internal cache dir
		String cachePath = Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED ? context
				.getExternalCacheDir().getPath() : context.getCacheDir()
				.getPath();

		return new File(cachePath + File.separator + uniqueName);
	}
}
