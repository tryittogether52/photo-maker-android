package vn.android.photomaker;

import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.social.ConstantSocial;
import vn.android.photomaker.social.FBRequestCallback;
import vn.android.photomaker.social.FBUtils;
import vn.android.photomaker.social.TWUtils;
import vn.android.photomaker.utils.SharedPreferencesStore;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class SettingActivity extends BaseActivity implements OnClickListener,
		Session.StatusCallback, FBRequestCallback {

	private RelativeLayout rlFacebook;
	private RelativeLayout rlTwitter;
	private TextView tvFacebook;
	private TextView tvTwitter;
	private SharedPreferencesStore store;
	private UiLifecycleHelper uiHelper;

	/** URL call back twitter in screen post message. */
	public static final String URL_CALLBACK_SHARE = "oauth://setup_social";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Set layout for activity.
		setContentView(mScreenUtil.getResourceID(R.array.setting_layout));

		// Get view from layout.
		setupViews();

		// Set tab bar to layout.
		addTabbar(ConstantVariable.SETTING_TAB, ConstantVariable.TAB_MAIN, true);

		// Setup for social.
		setupSocial(savedInstanceState);

	}

	/**
	 * Get view from layout.
	 */
	private void setupViews() {

		rlFacebook = (RelativeLayout) findViewById(R.id.twitter_layout);
		rlTwitter = (RelativeLayout) findViewById(R.id.facebook_layout);
		tvFacebook = (TextView) findViewById(R.id.facebook_mail_acc);
		tvTwitter = (TextView) findViewById(R.id.twitter_mail_acc);
		rlFacebook.setOnClickListener(this);
		rlTwitter.setOnClickListener(this);

	}

	/**
	 * Setup for social.
	 */
	private void setupSocial(Bundle savedInstanceState) {

		store = new SharedPreferencesStore(this);

		// Get user data of twitter account.
		TWUtils.setKey(getString(R.string.tw_consumer_key),
				getString(R.string.tw_consumer_secret));
		if (TWUtils.isAuthenticated(this)) {
			new GetTWProfile().execute();
		}

		// initialize the UiLifecycleHelper object
		uiHelper = new UiLifecycleHelper(this, this);
		FBUtils.setupFacebook(this, this, savedInstanceState, uiHelper);

		// Get user data of FB account.
		Session session = Session.getActiveSession();
		if (session.isOpened()) {
			FBUtils.getFBProfile(this);
		}
	}

	/**
	 * This class is used for login, get information login.
	 */
	private class LoginTwitter extends AsyncTask<Void, Void, Void> {

		/** Dialog show when load token. */
		private ProgressDialog prDialog = null;

		@Override
		protected void onPreExecute() {

			if (prDialog == null) {
				prDialog = new ProgressDialog(SettingActivity.this);
				prDialog.setMessage(getString(R.string.please_wait));
				prDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				prDialog.setCancelable(true);
				prDialog.setCanceledOnTouchOutside(false);
				prDialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {
			TWUtils.login(SettingActivity.this, URL_CALLBACK_SHARE);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {

			if (prDialog != null) {
				prDialog.dismiss();
				prDialog = null;
			}
			super.onPostExecute(result);
		}
	}

	/**
	 * This class is used for login, get information login.
	 */
	private class GetTWProfile extends AsyncTask<Void, Void, String> {

		/** Dialog show when load token. */
		private ProgressDialog prDialog = null;

		@Override
		protected void onPreExecute() {

			if (prDialog == null) {
				prDialog = new ProgressDialog(SettingActivity.this);
				prDialog.setMessage(getString(R.string.please_wait));
				prDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				prDialog.setCancelable(true);
				prDialog.setCanceledOnTouchOutside(false);
				prDialog.show();
			}
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Void... params) {
			String userName = TWUtils.getUserName(SettingActivity.this);
			return userName;
		}

		@Override
		protected void onPostExecute(String result) {

			if (result != null) {
				tvTwitter.setText(result);
			}
			if (prDialog != null) {
				prDialog.dismiss();
				prDialog = null;
			}
			super.onPostExecute(result);
		}
	}

	@Override
	public void onCompleted(int action, GraphUser user, Response response) {
		switch (action) {
		case ConstantSocial.FACEBOOK_GETUSERDATA:
			// If the response is successful
			if (user != null) {
				// Set the Textview's text to the user's name.
				tvFacebook.setText(user.getName());
			}
			if (response.getError() != null) {
				// Handle errors, will do so later.
			}
			break;
		case ConstantSocial.FACEBOOK_POSTTOWALL:
			break;
		default:
			break;
		}

	}

	@Override
	public void call(Session session, SessionState state, Exception exception) {
		FBUtils.getFBProfile(this);
		store.saveString("access_token", "" + session.getAccessToken());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.facebook_layout:
			Session session = Session.getActiveSession();
			if (session.isOpened()) {
				FBUtils.logout();
				tvFacebook.setText("Account");
			} else {
				FBUtils.login(this, this);
			}
			break;
		case R.id.twitter_layout:
			if (!TWUtils.isAuthenticated(this)) {
				new LoginTwitter().execute();
			} else {
				TWUtils.logout(this);
				tvTwitter.setText("Account");
			}
			break;
		default:
			break;
		}
	}

	@Override
	protected void onNewIntent(Intent intent) {

		// Handle OAuth Callback
		Uri uri = intent.getData();
		boolean result = TWUtils.handleOAuthCallback(this, uri,
				URL_CALLBACK_SHARE);

		// if login successful.
		if (result) {
			new GetTWProfile().execute();
		}
		super.onNewIntent(intent);
	}

	@Override
	public void onResume() {
		super.onResume();
		uiHelper.onResume();
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}
            
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		uiHelper.onDestroy();
		rlFacebook = null;
		rlTwitter = null;
		tvFacebook = null;
		tvTwitter = null;
		store = null;
	}
}
