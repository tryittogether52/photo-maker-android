/**
 * 12/04/2013.
 * */
package vn.android.photomaker.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * This class contain all functions for process with image.
 * */
public class ImageUtils {

	/**
	 * Crop image in center of origin image
	 * 
	 * @param {src} origin bitmap
	 * @param {reqWidth} requested width
	 * @param {reqHeight} requested height
	 * @return image has been crop
	 */
	public static Bitmap cropCenterBitmap(Bitmap src, int reqWidth,
			int reqHeight) {
		int fromX, fromY;
		int wOrgBmp = src.getWidth();
		int hOrgBmp = src.getHeight();
		int minWidth, minHeight;

		if (wOrgBmp <= hOrgBmp) {
			minWidth = reqWidth < wOrgBmp ? reqWidth : wOrgBmp;
			fromX = (wOrgBmp - minWidth) / 2;

			minHeight = (int) (minWidth * ((float) reqHeight / reqWidth));
			fromY = (hOrgBmp - minHeight) / 2;

		} else {
			minHeight = reqHeight < hOrgBmp ? reqHeight : hOrgBmp;
			fromY = (hOrgBmp - minHeight) / 2;

			minWidth = (int) (minHeight * ((float) reqWidth / reqHeight));
			fromX = (wOrgBmp - minWidth) / 2;
		}

		if (fromX < 0)
			fromX = 0;
		if (fromY < 0)
			fromY = 0;

		if (minHeight + fromY > hOrgBmp) {
			fromY = 0;
			minHeight = hOrgBmp;
		}
		if (minWidth + fromX > wOrgBmp) {
			fromX = 0;
			minWidth = wOrgBmp;
		}

		return Bitmap.createBitmap(src, fromX, fromY, minWidth, minHeight);
	}

	/**
	 * Calculate an inSampleSize for use in a {@link BitmapFactory.Options}
	 * object when decoding bitmaps using the decode* methods from
	 * {@link BitmapFactory}. This implementation calculates the closest
	 * inSampleSize that will result in the final decoded bitmap having a width
	 * and height equal to or larger than the requested width and height. This
	 * implementation does not ensure a power of 2 is returned for inSampleSize
	 * which can be faster when decoding but results in a larger bitmap which
	 * isn't as useful for caching purposes.
	 * 
	 * @param options
	 *            An options object with out* params already populated (run
	 *            through a decode* method with inJustDecodeBounds==true
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return The value to be used for inSampleSize
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		// Raw height and width of image.
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;
	}

	/**
	 * Decode and sample down a bitmap from resources to the requested width and
	 * height.
	 * 
	 * @param res
	 *            The resources object containing the image data
	 * @param resId
	 *            The resource id of the image data
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromResource(Resources res,
			int resId, int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions.
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeResource(res, resId, options);

		// Calculate inSampleSize.
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set.
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeResource(res, resId, options);
	}

	/**
	 * Decode and sample down a bitmap from image path to the requested width
	 * and
	 * 
	 * @param pathImage
	 *            path to image file
	 * @param reqWidth
	 *            The requested width of the resulting bitmap
	 * @param reqHeight
	 *            The requested height of the resulting bitmap
	 * @return A bitmap sampled down from the original with the same aspect
	 *         ratio and dimensions that are equal to or greater than the
	 *         requested width and height
	 */
	public static Bitmap decodeSampledBitmapFromFileName(String pathImage,
			int reqWidth, int reqHeight) {

		// First decode with inJustDecodeBounds=true to check dimensions.
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(pathImage, options);

		// Calculate inSampleSize.
		options.inSampleSize = calculateInSampleSize(options, reqWidth,
				reqHeight);

		// Decode bitmap with inSampleSize set.
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathImage, options);
	}

	/**
	 * decodes image and scales it to reduce memory consumption
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(File f) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			FileInputStream stream1 = new FileInputStream(f);
			BitmapFactory.decodeStream(stream1, null, o);
			stream1.close();

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = 70;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			if (scale >= 2) {
				scale /= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			FileInputStream stream2 = new FileInputStream(f);
			Bitmap bitmap = BitmapFactory.decodeStream(stream2, null, o2);
			stream2.close();
			return bitmap;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
