package vn.android.photomaker.social;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import twitter4j.StatusUpdate;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import vn.android.photomaker.utils.SharedPreferencesStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;


/**
 * This class contains all function execute with Twitter.
 * 
 * @author TuyenDN
 * 
 */
public class TWUtils {

	/** Variable of Twitter Object. */
	private static Twitter twitter;

	/** Variable of RequestToken Object. */
	private static RequestToken requestToken;

	/** Default of CallBack. */
	public static final String CALLBACK_URL = "oauth://sb";

	/** Consumer key */
	private static String CONSUMER_KEY = "";

	/** Consumer secret */
	private static String CONSUMER_SECRET = "";

	private static SharedPreferencesStore store;

	/**
	 * This function is used to check authenticated.
	 * 
	 * @param context
	 * @return true if authenticated otherwise false.
	 */
	public static boolean isAuthenticated(Context context) {
		if (store == null) {
			store = new SharedPreferencesStore(context);
		}
		return store.getString(ConstantSocial.PREF_KEY_TOKEN, null) != null;
	}

	/**
	 * This function is used to get information of user.
	 * 
	 * @param context
	 * @return null if not exists or error when connect to server.
	 */
	public static User getInfo(Context context) {
		settingTwitter(context);
		try {
			return twitter.showUser(twitter.getId());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * This function is used to get user name.
	 * 
	 * @param context
	 * @return null if not exists or error when connect to server.
	 */
	public static String getUserName(Context context) {
		String userName = getUserFromFile(context);
		if (userName == null) {
			settingTwitter(context);
			try {
				userName = twitter.showUser(twitter.getId()).getName();
				if (userName != null) {
					if (store == null) {
						store = new SharedPreferencesStore(context);
					}
					store.saveString(ConstantSocial.USER_TWITTER, userName);
				}
			} catch (Exception e) {
				return null;
			}
		}
		return userName;
	}

	/** Flag to check show dialog or not. */
	public static boolean showDialogGetInfor(Context context) {
		if (isAuthenticated(context)) {
			if (getUserFromFile(context) == null)
				return true;
		}
		return false;
	}

	/** Get user from file. */
	public static String getUserFromFile(Context context) {
		if (store == null) {
			store = new SharedPreferencesStore(context);
		}
		return store.getString(ConstantSocial.USER_TWITTER, null);
	}

	/**
	 * This function is used to send tweed to twitter.
	 * 
	 * @param context
	 * @param msg
	 * @param {bitmap} if the image is null, only post message to wall.
	 * @throws Exception
	 */
	public static boolean sendTweet(Context context, String msg, Bitmap bitmap) {
		Boolean result = false;
		try {
			settingTwitter(context);
			StatusUpdate status = new StatusUpdate(msg);
			if (bitmap != null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				bitmap.compress(CompressFormat.JPEG, 100, bos);
				byte[] bitmapdata = bos.toByteArray();
				ByteArrayInputStream bs = new ByteArrayInputStream(bitmapdata);
				status.setMedia("", bs);
			}
			twitter.updateStatus(status);
			result = true;
		} catch (Exception e) {
			result = false;
			Log.e("sendTweet:", e.getMessage());
		}
		return result;
	}

	/**
	 * This function is used to setting Twitter.
	 * 
	 * @param context
	 */
	public static void settingTwitter(Context context) {

		if (store == null) {
			store = new SharedPreferencesStore(context);
		}
		String token = store.getString(ConstantSocial.PREF_KEY_TOKEN, "");
		String secret = store.getString(ConstantSocial.PREF_KEY_SECRET, "");
		ConfigurationBuilder confbuilder = new ConfigurationBuilder();
		Configuration conf = confbuilder.setOAuthConsumerKey(CONSUMER_KEY)
				.setOAuthConsumerSecret(CONSUMER_SECRET)
				.setOAuthAccessToken(token).setOAuthAccessTokenSecret(secret)
				.build();
		twitter = new TwitterFactory(conf).getInstance();
	}

	/**
	 * This function is used to authentication twitter.
	 * 
	 * @param context
	 */
	public static boolean login(Activity context, String callBackURL) {

		if (callBackURL == null) {
			callBackURL = CALLBACK_URL;
		}
		ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
		configurationBuilder.setOAuthConsumerKey(CONSUMER_KEY);
		configurationBuilder.setOAuthConsumerSecret(CONSUMER_SECRET);
		Configuration configuration = configurationBuilder.build();
		twitter = new TwitterFactory(configuration).getInstance();

		try {

			requestToken = twitter.getOAuthRequestToken(callBackURL);
			Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken
					.getAuthenticationURL() + "&force_login=true"));
			context.startActivity(i);
		} catch (Exception e) {
			Log.e("Error TW:", "" + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * This function is used to handle OAuth callback.
	 * 
	 * @return
	 */
	public static boolean handleOAuthCallback(Context context, Uri uri,
			String callBackURL) {

		if (callBackURL == null) {
			callBackURL = CALLBACK_URL;
		}
		boolean result = false;
		if (uri != null && uri.toString().startsWith(callBackURL)) {
			String verifier = uri
					.getQueryParameter(ConstantSocial.IEXTRA_OAUTH_VERIFIER);
			try {
				AccessToken accessToken = (new TWAccessToken(twitter,
						verifier, requestToken)).execute().get();
				if (store == null) {
					store = new SharedPreferencesStore(context);
				}
				store.saveString(ConstantSocial.PREF_KEY_TOKEN, accessToken.getToken());
				store.saveString(ConstantSocial.PREF_KEY_SECRET,
						accessToken.getTokenSecret());
				result = true;
			} catch (Exception e) {
				Log.e("Exception", "" + e.getMessage());
				result = false;
			}
		}
		return result;
	}

	/**
	 * This function is used to remove Token, secret from SharedPreferences.
	 */
	public static void logout(Context context) {
		// Clear Cookies.
		if (store == null) {
			store = new SharedPreferencesStore(context);
		}
		store.delete(ConstantSocial.PREF_KEY_TOKEN);
		store.delete(ConstantSocial.PREF_KEY_SECRET);
		store.delete(ConstantSocial.USER_TWITTER);
	}

	/**
	 * Method used to set ConsumerId for twitter.
	 * 
	 * @param CONSUMER_KEY
	 * @param CONSUMER_SECRET
	 */
	public static void setKey(String consumer_key, String consumer_secret) {
		CONSUMER_KEY = consumer_key;
		CONSUMER_SECRET = consumer_secret;
	}

}
