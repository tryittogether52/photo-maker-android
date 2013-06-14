package vn.android.photomaker.social;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import vn.android.photomaker.utils.SharedPreferencesStore;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;

public class FBUtils {

	private static SharedPreferencesStore store;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");

	/**
	 * This function is used for setup FB in first run.
	 * 
	 * @param mActivity
	 * @param savedInstanceState
	 * @param statusCallback
	 */
	public static void setupFacebook(Activity mActivity, Session.StatusCallback statusCallback,
			Bundle savedInstanceState, UiLifecycleHelper uiHelper) {

		if (store == null)
			store = new SharedPreferencesStore(mActivity);
		String access_token = store.getString(ConstantSocial.FB_TOKEN_STORE, null);
		Session session = Session.getActiveSession();
		if (session == null) {
			if (access_token != null) {
				session = new Session(mActivity);
				store.delete(ConstantSocial.FB_TOKEN_STORE);
				AccessToken accessToken = AccessToken
						.createFromExistingAccessToken(access_token, null,
								null, null, null);
				session.open(accessToken, statusCallback);
				Session.setActiveSession(session);
			} else {
				uiHelper.onCreate(savedInstanceState);
			}
		}
		
	}

	/**
	 * This function is used for login to FB.
	 * 
	 * @param mActivity
	 * @param statusCallback
	 */
	public static void login(Activity mActivity,
			Session.StatusCallback statusCallback) {
		Session session = Session.getActiveSession();
		if (!session.isOpened() && !session.isClosed()) {
			session.openForRead(new Session.OpenRequest(mActivity)
					.setCallback(statusCallback));
		} else {
			Session.openActiveSession(mActivity, true, statusCallback);
		}
	}

	/**
	 * This function is used for logout FB.
	 */
	public static void logout() {
		Session session = Session.getActiveSession();
		if (!session.isClosed()) {
			session.closeAndClearTokenInformation();
		}
		store.delete(ConstantSocial.FB_TOKEN_STORE);
	}

	/**
	 * This function is used to post image to wall.
	 * 
	 * @param {context}
	 * @param {message} Comment
	 * @param {bitmap} image. if the image is null, only post message to wall.
	 * @return true if successful otherwise false.
	 */
	public static void postToWall(Activity mActivity, String message,
			String picturePath, String link, String appName,
			String description, final FBRequestCallback callback) {

		Session session = Session.getActiveSession();
		if (session != null) {
			
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						mActivity, PERMISSIONS);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}
			
			Bundle postParams = new Bundle();
			postParams.putString("message", message);
			postParams.putString("picture", picturePath);
			postParams.putString("link", link);
			postParams.putString("name", appName);
			postParams.putString("description", description);

			Request.Callback reqCallback = new Request.Callback() {

				@Override
				public void onCompleted(Response response) {
					callback.onCompleted(ConstantSocial.FACEBOOK_POSTTOWALL, null,
							response);
				}
			};

			Request request = new Request(session, "me/feed", postParams,
					HttpMethod.POST, reqCallback);

			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();
		}
	}

	/**
	 * Make an API call to get user data and define a new callback to handle the
	 * response.
	 * 
	 * @param session
	 */
	public static void getFBProfile(final FBRequestCallback callback) {

		final Session session = Session.getActiveSession();
		Request request = Request.newMeRequest(session,
				new Request.GraphUserCallback() {

					@Override
					public void onCompleted(GraphUser user, Response response) {
						callback.onCompleted(ConstantSocial.FACEBOOK_GETUSERDATA,
								user, response);
						Log.e("Response", "" + response);
					}
				});
		request.executeAsync();
	}

	private static boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}
}
