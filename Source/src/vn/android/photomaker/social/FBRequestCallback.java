package vn.android.photomaker.social;

import com.facebook.Response;
import com.facebook.model.GraphUser;

public interface FBRequestCallback {
	public void onCompleted(int action, GraphUser user, Response response);
}
