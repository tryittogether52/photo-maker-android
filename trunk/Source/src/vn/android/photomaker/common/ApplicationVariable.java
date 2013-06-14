package vn.android.photomaker.common;

import android.app.Application;

public class ApplicationVariable extends Application {

	public int PictureID = -1;

	@Override
	public void onCreate() {

	}

	public int getPictureID() {
		return PictureID;
	}

	public void setPictureID(int pictureID) {
		PictureID = pictureID;
	}

}
