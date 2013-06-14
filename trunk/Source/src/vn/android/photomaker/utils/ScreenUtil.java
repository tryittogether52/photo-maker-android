package vn.android.photomaker.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

public class ScreenUtil {

	private Context mContext;
	private DisplayMetrics displayMetrics = new DisplayMetrics();

	private float maxWidth = 320;
	private float baseWidth = 480;

	private static float radioWidthDp = 1.0f;

	public static float getRadioWidthDp() {
		return radioWidthDp;
	}

	public static void setRadioWidthDp(float radioWidthDp) {
		ScreenUtil.radioWidthDp = radioWidthDp;
	}

	public ScreenUtil(Activity pActivity) {
		pActivity.getWindowManager().getDefaultDisplay()
				.getMetrics(displayMetrics);
		mContext = pActivity;
		int w = (int) ((float) displayMetrics.widthPixels / displayMetrics.density);
		int h = (int) ((float) displayMetrics.heightPixels / displayMetrics.density);
		int tmp = 0;
		if (w > h) {
			tmp = w;
			w = h;
			h = tmp;
		}
		if (w >= 1080) {
			maxWidth = 1080;
		} else if (w >= 800) {
			maxWidth = 800;
		} else if (w >= 720) {
			maxWidth = 720;
		} else if (w >= 600) {
			maxWidth = 600;
		} else if (w >= 480) {
			maxWidth = 480;
		} else if (w >= 360) {
			maxWidth = 360;
		} else if (w >= 320) {
			maxWidth = 320;
		}
		radioWidthDp = baseWidth / maxWidth;
	}

	public DisplayMetrics getDisplay() {
		return this.displayMetrics;
	}

	public float convertDpToPixel(float dp) {
		float px = dp * (getDisplay().density) + 0.5f;
		return px;
	}

	public int getResourceID(int pArrayResource) {
		int index = 0;
		switch ((int) this.maxWidth) {
		case 1080:
			index = 6;
			break;
		case 800:
			index = 5;
			break;
		case 720:
			index = 4;
			break;
		case 600:
			index = 3;
			break;
		case 480:
			index = 2;
			break;
		case 360:
			index = 1;
			break;
		case 320:
			index = 0;
			break;
		}
		return mContext.getResources().obtainTypedArray(pArrayResource)
				.getResourceId(index, 0);
	}

	public float getMaxWidth() {
		return this.maxWidth;
	}

	public float convertSize(float size) {
		float cvSize = (maxWidth / baseWidth) * size;
		return cvSize;
	}

	public int getReadWidth() {
		return (int) convertDpToPixel(convertSize(displayMetrics.widthPixels));
	}
}
