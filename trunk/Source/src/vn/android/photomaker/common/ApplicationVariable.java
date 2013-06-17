package vn.android.photomaker.common;

import java.io.File;

import android.app.Application;
import android.os.Environment;

public class ApplicationVariable extends Application {

	public int PictureID = -1;

	@Override
	public void onCreate() {

		File f = new File(Environment.getExternalStorageDirectory()
				+ ConstantVariable.FOLDER);
		if (!f.exists()) {
			f.mkdir();
		}
		f = new File(Environment.getExternalStorageDirectory()
				+ ConstantVariable.FOLDER_CAMERA);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	public int getPictureID() {
		return PictureID;
	}

	public void setPictureID(int pictureID) {
		PictureID = pictureID;
	}

}
