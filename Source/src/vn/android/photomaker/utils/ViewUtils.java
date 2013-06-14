package vn.android.photomaker.utils;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.ImageView;

/**
 * This class contain util function for view.
 * 
 */
public class ViewUtils {

	/**
	 * This function used for release memory, override by child activity.
	 * 
	 * @param activity
	 *            The context.
	 * @param viewID
	 *            The id of root view.
	 * */

	public static void cleanupView(Activity activity, int viewID) {
		try {
			View view = activity.findViewById(viewID);
			if (view != null) {
				unbindViewReferences(view);
				if (view instanceof ViewGroup)
					unbindViewGroupReferences((ViewGroup) view);
			}
			System.gc();
		} catch (Throwable e) {

			// whatever exception is thrown just ignore it because a crash is
			// always worse than this method not doing what it's supposed to do
		}
	}

	/**
	 * This method used for unbind reference to view group.
	 * 
	 * @param viewGroup
	 *            The group of views.
	 * */
	private static void unbindViewGroupReferences(ViewGroup viewGroup) {
		int nrOfChildren = viewGroup.getChildCount();
		for (int i = 0; i < nrOfChildren; i++) {
			View view = viewGroup.getChildAt(i);
			unbindViewReferences(view);
			if (view instanceof ViewGroup)
				unbindViewGroupReferences((ViewGroup) view);
		}
		try {
			viewGroup.removeAllViews();
		} catch (Throwable mayHappen) {

			// AdapterViews, ListViews and potentially other ViewGroups don't
			// support the removeAllViews operation
		}
	}

	/**
	 * This method used for unbind reference to one view.
	 * 
	 * @param viewGroup
	 *            The view.
	 * */
	private static void unbindViewReferences(View view) {

		// set all listeners to null (not every view and not every API level
		// supports the methods)
		try {
			view.setOnClickListener(null);
		} catch (Throwable mayHappen) {
		}

		try {
			view.setOnCreateContextMenuListener(null);
		} catch (Throwable mayHappen) {
		}

		try {
			view.setOnFocusChangeListener(null);
		} catch (Throwable mayHappen) {
		}

		try {
			view.setOnKeyListener(null);
		} catch (Throwable mayHappen) {
		}

		try {
			view.setOnLongClickListener(null);
		} catch (Throwable mayHappen) {
		}

		try {
			view.setOnClickListener(null);
		} catch (Throwable mayHappen) {
		}

		// set background to null
		setNullLayout(view);
		if (view instanceof ImageView) {
			ImageView imageView = (ImageView) view;
			if (imageView != null) {
				setNullViewDrawable(imageView);
			}
		}

		// destroy webview
		if (view instanceof WebView) {
			((WebView) view).destroy();
		}
	}

	/**
	 * Set null view.
	 * 
	 * @param pview
	 */
	public static void setNullLayout(View pview) {
		if (pview != null) {
			pview.setBackgroundDrawable(null);
			pview.setBackgroundResource(0);
			pview.destroyDrawingCache();
		}
	}

	/**
	 * Set null Image view.
	 * 
	 * @param view
	 *            The {@link ImageView}
	 */
	public static void setNullViewDrawable(ImageView view) {

		// remove resource of View.
		if (view != null) {

			if (view.getDrawable() != null) {
				view.getDrawable().setCallback(null);
			}
			if (view.getBackground() != null) {
				view.getBackground().setCallback(null);
			}
			view.setImageDrawable(null);
			view.getResources().flushLayoutCache();
			view.destroyDrawingCache();
			view.setBackgroundDrawable(null);
			view.setBackgroundResource(0);
			view = null;
		}
	}
	/**
	 * This function hides virtual keyboard after done something on the view
	 * @param activity
	 * @param view
	 */
	public static void hideVirtualKeyboard(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
	}
	/**
	 * This function shows virtual keyboard after that edit text show
	 * @param activity
	 * @param view
	 */
	public static void showVirtualKeyboard(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInputFromInputMethod(view.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED);
	}
}
