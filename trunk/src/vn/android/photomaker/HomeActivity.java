package vn.android.photomaker;

import vn.android.photomaker.adapter.PageAdapter;
import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.database.PictureDB;
import vn.android.photomaker.gridpage.OnItemGridClickListener;
import vn.android.photomaker.gridpage.OnPageChangeListener;
import vn.android.photomaker.gridpage.PagedDragDropGrid;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class HomeActivity extends BaseActivity implements OnClickListener {

	/** Variable of GridView. */
	private PagedDragDropGrid gridview;

	/** Size of page. */
	private int pageSize;

	/** Layout of page horizontal. */
	private LinearLayout mPage;

	/** Variable of page adapter. */
	private PageAdapter adapter;

	/** Variable execute with database. */
	private PictureDB db;

	/** Circle size of page view. */
	private int circleSize;

	/** Circle margin size of page view. */
	private int circleMargin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout to activity.
		setContentView(mScreenUtil.getResourceID(R.array.home_layout));

		// Find views in layout.
		setupViews();

		// Set tab bar to layout.
		addTabbar(ConstantVariable.HOME_TAB, ConstantVariable.TAB_MAIN, true);
	}

	@Override
	protected void onResume() {
		loadData();
		super.onResume();
	}

	/**
	 * Find views in layout.
	 */
	private void setupViews() {

		// Create connection to database.
		db = new PictureDB();

		gridview = (PagedDragDropGrid) findViewById(R.id.gridview);
		gridview.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageChange(int page) {
				setView(page);
			}
		});
		gridview.setOnItemGridClickListener(new OnItemGridClickListener() {

			@Override
			public void OnItemGridClick(int albumID) {
				// startAlbumView(albumID);
			}
		});
		mPage = (LinearLayout) findViewById(R.id.page);
		mPage.removeAllViews();

		// Get size from dimension.
		circleSize = Math.round(mScreenUtil.convertDpToPixel(mScreenUtil
				.convertSize(6)));
		circleMargin = Math.round(mScreenUtil.convertDpToPixel(mScreenUtil
				.convertSize(2)));
	}

	/**
	 * This function is used for page navigator.
	 * 
	 * @param {currentPage} index of current page.
	 */
	private void setView(int currentPage) {

		mPage.removeAllViews();
		pageSize = adapter.pageCount();
		for (int i = 0; i < pageSize; ++i) {
			ImageView imageView = new ImageView(this);
			LayoutParams params = new LayoutParams(circleSize, circleSize);
			params.rightMargin = circleMargin;
			imageView.setLayoutParams(params);
			if (currentPage == i) {
				imageView.setBackgroundResource(R.drawable.circle_current);
			} else {
				imageView.setBackgroundResource(R.drawable.circle);
			}
			mPage.addView(imageView);
		}
	}

	/**
	 * Method used to load data from service.
	 * 
	 * @param totalRecord
	 */
	private void loadData() {

		adapter = new PageAdapter(this, mScreenUtil);
		gridview.setAdapter(adapter);
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		// Get current page.
		if (hasFocus) {
			if (appVariale.getPictureID() != -1) {
				int pageIndex = db.getPage(appVariale.getPictureID());
				gridview.scrollToPage(pageIndex);
			} else {
				gridview.scrollToPage(0);
			}
		}
		super.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.home, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.create_new:
			Intent i = new Intent(this, PhotoActivity.class);
			startActivityWithAnimation(i);
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
