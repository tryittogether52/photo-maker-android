package vn.android.photomaker;

import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.multitouch.PhotoSortrView;
import vn.android.photomaker.utils.ImageUtils;
import vn.android.photomaker.utils.ScreenUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

public class PhotoActivity extends SherlockActivity implements OnClickListener,
		AnimationListener {

	/** Parameters */
	private boolean menuOut = false;
	private AnimParams animParams = new AnimParams();
	protected ScreenUtil mScreenUtil;

	/** View of layout */
	private View menu;
	private PhotoSortrView mContent;
	private View mnImportPicture;
	private View mnImportText;
	private View mnChangeBackground;

	/** Show dialog. */
	private AlertDialog alertDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create screen utility.
		if (mScreenUtil == null) {
			mScreenUtil = new ScreenUtil(this);
		}

		// Set layout to activity.
		setContentView(mScreenUtil.getResourceID(R.array.photo_layout));

		// Find view in layout.
		setupViews();

	}

	/**
	 * Find view in layout.
	 */
	private void setupViews() {

		menu = findViewById(R.id.menu);
		mContent = (PhotoSortrView) findViewById(R.id.app);
		mnImportPicture = findViewById(R.id.import_photo);
		mnImportText = findViewById(R.id.import_text);
		mnChangeBackground = findViewById(R.id.change_background);
		mnImportPicture.setOnClickListener(this);
		mnImportText.setOnClickListener(this);
		mnChangeBackground.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.make_photo, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public void onBackPressed() {
		if (menuOut) {
			slideMenu();
		} else {
			// finishActivityWithAnimation(true);
			super.onBackPressed();
		}
	}

	static class AnimParams {

		int left, right, top, bottom;

		void init(int left, int top, int right, int bottom) {
			this.left = left;
			this.top = top;
			this.right = right;
			this.bottom = bottom;
		}
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		menuOut = !menuOut;
		if (!menuOut) {
			menu.setVisibility(View.INVISIBLE);
		}
		layoutApp(menuOut);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.import_item:
			slideMenu();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	void layoutApp(boolean menuOut) {
		mContent.layout(animParams.left, animParams.top, animParams.right,
				animParams.bottom);
		mContent.clearAnimation();
	}

	private void slideMenu() {
		Animation anim;
		int w = mContent.getMeasuredWidth();
		int h = mContent.getMeasuredHeight();
		int left = (int) (mContent.getMeasuredWidth() * 0.65);
		if (!menuOut) {
			anim = new TranslateAnimation(0, left, 0, 0);
			menu.setVisibility(View.VISIBLE);
			animParams.init(left, 0, left + w, h);
		} else {
			anim = new TranslateAnimation(0, -left, 0, 0);
			animParams.init(0, 0, w, h);
		}
		anim.setDuration(500);
		anim.setAnimationListener(this);
		anim.setFillAfter(true);
		mContent.startAnimation(anim);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.import_photo:
			CharSequence[] list = new CharSequence[2];
			list[0] = getString(R.string.gallery);
			list[1] = getString(R.string.camera);
			createDialog(list, getString(R.string.select_from));
			slideMenu();
			break;
		case R.id.import_text:
			break;
		case R.id.change_background:
			break;
		default:
			break;
		}
	}

	/** Create dialog. */
	private void createDialog(final CharSequence[] items, String title) {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setCancelable(false);
		builder.setPositiveButton(getString(R.string.bt_cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
						slideMenu();
					}
				});
		builder.setItems(items, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				Intent intent;
				switch (item) {
				case 0:
					// Start gallery.
					intent = new Intent(
							Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					startActivityForResult(Intent.createChooser(intent,
							getString(R.string.select_picture)),
							ConstantVariable.SELECT_PHOTOLIBRARY);
					break;

				case 1:
					// Start camera.
					Intent cameraIntent = new Intent(
							android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(cameraIntent,
							ConstantVariable.TAKE_PICTURE);
					break;
				default:
					break;
				}
			}
		});
		alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			switch (requestCode) {
			case ConstantVariable.SELECT_PHOTOLIBRARY:
				Uri selectedImageUri = data.getData();
				mContent.addImage(getApplicationContext(),
						ImageUtils.getRealPathFromURI(this, selectedImageUri));
				break;
			case ConstantVariable.TAKE_PICTURE:
				Bundle extras = data.getExtras();
				if (extras != null) {
					Bitmap bitmap = extras.getParcelable("data");
					String path = ImageUtils.saveBitmapToFile(
							Environment.getExternalStorageDirectory()
									+ ConstantVariable.FOLDER, bitmap);
					mContent.addImage(getApplicationContext(), path);
				}
				break;
			default:
				break;
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		menu = null;
		mContent = null;
		animParams = null;
		mnImportPicture = null;
		mnImportText = null;
		mnChangeBackground = null;
	}
}
