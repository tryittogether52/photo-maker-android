package vn.android.photomaker;

import vn.android.photomaker.common.ApplicationVariable;
import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.utils.ScreenUtil;
import vn.android.photomaker.utils.SharedPreferencesStore;
import vn.android.photomaker.utils.ViewUtils;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.actionbarsherlock.app.SherlockActivity;

public class BaseActivity extends SherlockActivity implements OnTouchListener {

	/** List of buttons on the tab bar. */
	private ImageButton listTab[] = new ImageButton[4];

	/** Handle of Tab bar. */
	protected LinearLayout llMainTabbarPanel;

	/** Variable of ScreenUtil. */
	protected ScreenUtil mScreenUtil;

	/** Variable of SharedPreferences. */
	protected SharedPreferencesStore sharedStore;

	private int mTab = 0;

	protected ApplicationVariable appVariale;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set no animation.
		getWindow().setWindowAnimations(Intent.FLAG_ACTIVITY_NO_ANIMATION);

		if (appVariale == null) {
			appVariale = (ApplicationVariable) getApplicationContext();
		}
		// Create screen utility.
		if (mScreenUtil == null) {
			mScreenUtil = new ScreenUtil(this);
		}

		// create instance of share preference.
		if (sharedStore == null) {
			sharedStore = new SharedPreferencesStore(this);
		}
	}

	/**
	 * Get handle of button on the tab bar and set background for tab bar.
	 * 
	 * @param {flag} Flag to check show main tab or detail tab.
	 * @param {isShowTab} Flag to check show tab or not.
	 */
	protected void addTabbar(int tab, int flag, boolean isShowTab) {

		if (isShowTab) {

			switch (flag) {
			case ConstantVariable.TAB_MAIN:
				llMainTabbarPanel = (LinearLayout) findViewById(R.id.main_tab);
				llMainTabbarPanel.setVisibility(View.VISIBLE);
				listTab[0] = (ImageButton) findViewById(R.id.attacktabbar_btn_1);
				listTab[1] = (ImageButton) findViewById(R.id.attacktabbar_btn_2);
				listTab[2] = (ImageButton) findViewById(R.id.attacktabbar_btn_3);
				listTab[3] = (ImageButton) findViewById(R.id.attacktabbar_btn_4);
				// Add listener for button of tab bar.
				for (int i = 0; i < listTab.length; i++) {
					listTab[i].setOnTouchListener(this);
				}

				// Set active tab.
				mTab = tab;
				switch (tab) {
				case ConstantVariable.HOME_TAB:
					setBackgroundTap(0);
					break;
				case ConstantVariable.SETTING_TAB:
					setBackgroundTap(1);
					break;
				case ConstantVariable.HISTORY_TAB:
					setBackgroundTap(2);
					break;
				case ConstantVariable.INFOR_TAB:
					setBackgroundTap(3);
					break;
				default:
					break;
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		Intent i = null;
		if (event.getAction() == MotionEvent.ACTION_UP) {
			switch (v.getId()) {
			case R.id.attacktabbar_btn_1:
				if (mTab == ConstantVariable.HOME_TAB)
					return false;
				setBackgroundTap(0);
				i = new Intent(this, HomeActivity.class);
				startActivity(i);
				finish();
				return true;
			case R.id.attacktabbar_btn_2:
				if (mTab == ConstantVariable.SETTING_TAB)
					return false;
				setBackgroundTap(1);
				i = new Intent(this, SettingActivity.class);
				startActivity(i);
				finish();
				return true;
			case R.id.attacktabbar_btn_3:
				if (mTab == ConstantVariable.HISTORY_TAB)
					return false;
				setBackgroundTap(2);
				i = new Intent(this, FavoritesActivity.class);
				startActivity(i);
				finish();
				return true;
			case R.id.attacktabbar_btn_4:
				if (mTab == ConstantVariable.INFOR_TAB)
					return false;
				setBackgroundTap(3);
				i = new Intent(this, InforActivity.class);
				startActivity(i);
				finish();
				return true;
			default:
				return false;
			}
		}
		return false;
	}

	/** Set background when press. */
	protected void setBackgroundTap(int id) {
		for (int i = 0; i < listTab.length; ++i) {
			if (id == i) {
				listTab[i].setBackgroundResource(R.drawable.tab_selected);
			} else {
				listTab[i].setBackgroundResource(0);
			}
		}
	}

	/**
	 * Method used to start an activity with animation.
	 * 
	 * @param intent
	 *            Intent want to start.
	 */
	protected final void startActivityWithAnimation(Intent intent) {
		startActivity(intent);
		overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
	}

	/**
	 * Method used to finish an activity with animation.
	 * 
	 * @param flag
	 *            true if want to add animation effect.
	 */
	protected final void finishActivityWithAnimation(boolean flag) {
		finish();
		if (flag) {
			overridePendingTransition(R.anim.slide_in_left,
					R.anim.slide_out_right);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Remove all view in layout.
		ViewUtils.cleanupView(this, R.id.root);
		llMainTabbarPanel = null;
		mScreenUtil = null;
		sharedStore = null;
		listTab = null;
		System.gc();
	}
}
