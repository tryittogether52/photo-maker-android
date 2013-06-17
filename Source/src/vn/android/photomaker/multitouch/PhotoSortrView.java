package vn.android.photomaker.multitouch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vn.android.photomaker.R;
import vn.android.photomaker.entities.PicturePart;
import vn.android.photomaker.multitouch.MultiTouchController.MultiTouchObjectCanvas;
import vn.android.photomaker.multitouch.MultiTouchController.PointInfo;
import vn.android.photomaker.multitouch.MultiTouchController.PositionAndScale;
import vn.android.photomaker.utils.ImageUtils;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

public class PhotoSortrView extends View implements
		MultiTouchObjectCanvas<PhotoSortrView.Img> {

	private ArrayList<Img> mImages = new ArrayList<Img>();
	private MultiTouchController<Img> multiTouchController = new MultiTouchController<Img>(
			this);
	private PointInfo currTouchPoint = new PointInfo();
	private static final int UI_MODE_ROTATE = 1, UI_MODE_ANISOTROPIC_SCALE = 2;
	private int mUIMode = UI_MODE_ROTATE;
	private Paint mLinePaintTouchPointCircle = new Paint();

	public PhotoSortrView(Context context) {
		this(context, null);
	}

	public ArrayList<Img> getmImages() {
		return mImages;
	}

	public void setmImages(ArrayList<Img> mImages) {
		this.mImages = mImages;
	}

	public PhotoSortrView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PhotoSortrView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * initial multi touch.
	 * 
	 * @param context
	 */
	private void init(Context context) {
		mLinePaintTouchPointCircle.setColor(Color.YELLOW);
		mLinePaintTouchPointCircle.setStrokeWidth(5);
		mLinePaintTouchPointCircle.setStyle(Style.STROKE);
		mLinePaintTouchPointCircle.setAntiAlias(true);
		setBackgroundResource(R.drawable.bg_photo_edit);
	}

	/**
	 * Restore position image when edit.
	 * 
	 * @param context
	 * @param listPart
	 */
	public void restore(Context context, List<PicturePart> listPart) {
		Resources res = context.getResources();
		File f = null;
		Bitmap bm = null;
		PositionAndScale objPosAndScale = new PositionAndScale();

		for (PicturePart picture : listPart) {
			f = new File(picture.getPathImage());
			bm = ImageUtils.decodeFile(f);
			mImages.add(new Img(picture.getPathImage(), bm, res));
			objPosAndScale.set(picture.getxOff(), picture.getyOff(),
					picture.getScale(), picture.getScaleX(),
					picture.getScaleY(), picture.getAngle());
			mImages.get(mImages.size() - 1).setPos(objPosAndScale);
		}
		invalidate();
	}

	/**
	 * Add image to view.
	 * 
	 * @param context
	 * @param mbitmap
	 */
	public void addImage(Context context, String path) {

		Resources res = context.getResources();

		File f = new File(path);
		Bitmap bm = ImageUtils.decodeFile(f);
		mImages.add(new Img(path, bm, res));
		mImages.get(mImages.size() - 1).load(res);
		invalidate();
	}

	/**
	 * Called by activity's onPause() method to free memory used for loading the
	 * images
	 */
	public void unloadImages() {
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).unload();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int n = mImages.size();
		for (int i = 0; i < n; i++)
			mImages.get(i).draw(canvas);
	}

	public void trackballClicked() {
		mUIMode = (mUIMode + 1) % 3;
		invalidate();
	}

	/** Pass touch events to the MT controller */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return multiTouchController.onTouchEvent(event);
	}

	/**
	 * Get the image that is under the single-touch point, or return null
	 * (canceling the drag op) if none
	 */
	public Img getDraggableObjectAtPoint(PointInfo pt) {
		float x = pt.getX(), y = pt.getY();
		int n = mImages.size();
		for (int i = n - 1; i >= 0; i--) {
			Img im = mImages.get(i);
			if (im.containsPoint(x, y))
				return im;
		}
		return null;
	}

	/**
	 * Select an object for dragging. Called whenever an object is found to be
	 * under the point (non-null is returned by getDraggableObjectAtPoint()) and
	 * a drag operation is starting. Called with null when drag op ends.
	 */
	public void selectObject(Img img, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		if (img != null) {
			// Move image to the top of the stack when selected
			mImages.remove(img);
			mImages.add(img);
		} else {
			// Called with img == null when drag stops.
		}
		invalidate();
	}

	// ----------------------------------------------------------------------------------------------

	public class Img {

		private String path;

		private Bitmap mBitmap;

		private Drawable drawable;

		private boolean firstLoad;

		private int width, height, displayWidth, displayHeight;

		private float centerX, centerY, scaleX, scaleY, angle;

		private float minX, maxX, minY, maxY;

		private static final float SCREEN_MARGIN = 100;

		public Img(String path, Bitmap mBitmap, Resources res) {
			this.path = path;
			this.mBitmap = mBitmap;
			this.firstLoad = true;
			getMetrics(res);
		}

		private void getMetrics(Resources res) {
			DisplayMetrics metrics = res.getDisplayMetrics();
			this.displayWidth = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
					.max(metrics.widthPixels, metrics.heightPixels) : Math.min(
					metrics.widthPixels, metrics.heightPixels);
			this.displayHeight = res.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE ? Math
					.min(metrics.widthPixels, metrics.heightPixels) : Math.max(
					metrics.widthPixels, metrics.heightPixels);
		}

		/** Called by activity's onResume() method to load the images */
		public void load(Resources res) {

			getMetrics(res);
			this.drawable = new BitmapDrawable(res, mBitmap);
			this.width = drawable.getIntrinsicWidth();
			this.height = drawable.getIntrinsicHeight();

			float cx, cy, sx, sy;
			if (firstLoad) {
				cx = SCREEN_MARGIN
						+ (float) (Math.random() * (displayWidth - 2 * SCREEN_MARGIN));
				cy = SCREEN_MARGIN
						+ (float) (Math.random() * (displayHeight - 2 * SCREEN_MARGIN));
				float sc = (float) (Math.max(displayWidth, displayHeight)
						/ (float) Math.max(width, height) * Math.random() * 0.3 + 0.2);
				sx = sy = sc;
				firstLoad = false;
			} else {

				// Reuse position and scale information if it is available
				// FIXME this doesn't actually work because the whole activity
				// is torn down and re-created on rotate
				cx = this.centerX;
				cy = this.centerY;
				sx = this.scaleX;
				sy = this.scaleY;
				// Make sure the image is not off the screen after a screen
				// rotation
				if (this.maxX < SCREEN_MARGIN)
					cx = SCREEN_MARGIN;
				else if (this.minX > displayWidth - SCREEN_MARGIN)
					cx = displayWidth - SCREEN_MARGIN;
				if (this.maxY > SCREEN_MARGIN)
					cy = SCREEN_MARGIN;
				else if (this.minY > displayHeight - SCREEN_MARGIN)
					cy = displayHeight - SCREEN_MARGIN;
			}
			setPos(cx, cy, sx, sy, 0.0f);
		}

		/**
		 * Called by activity's onPause() method to free memory used for loading
		 * the images
		 */
		public void unload() {
			this.drawable = null;
		}

		/** Set the position and scale of an image in screen coordinates */
		public boolean setPos(PositionAndScale newImgPosAndScale) {
			return setPos(
					newImgPosAndScale.getXOff(),
					newImgPosAndScale.getYOff(),
					(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
							.getScaleX() : newImgPosAndScale.getScale(),
					(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0 ? newImgPosAndScale
							.getScaleY() : newImgPosAndScale.getScale(),
					newImgPosAndScale.getAngle());
		}

		/** Set the position and scale of an image in screen coordinates */
		private boolean setPos(float centerX, float centerY, float scaleX,
				float scaleY, float angle) {
			float ws = (width / 2) * scaleX, hs = (height / 2) * scaleY;
			float newMinX = centerX - ws, newMinY = centerY - hs, newMaxX = centerX
					+ ws, newMaxY = centerY + hs;
			if (newMinX > displayWidth - SCREEN_MARGIN
					|| newMaxX < SCREEN_MARGIN
					|| newMinY > displayHeight - SCREEN_MARGIN
					|| newMaxY < SCREEN_MARGIN)
				return false;
			this.centerX = centerX;
			this.centerY = centerY;
			this.scaleX = scaleX;
			this.scaleY = scaleY;
			this.angle = angle;
			this.minX = newMinX;
			this.minY = newMinY;
			this.maxX = newMaxX;
			this.maxY = newMaxY;
			return true;
		}

		/** Return whether or not the given screen coords are inside this image */
		public boolean containsPoint(float scrnX, float scrnY) {
			// FIXME: need to correctly account for image rotation
			return (scrnX >= minX && scrnX <= maxX && scrnY >= minY && scrnY <= maxY);
		}

		public void draw(Canvas canvas) {
			canvas.save();
			float dx = (maxX + minX) / 2;
			float dy = (maxY + minY) / 2;
			drawable.setBounds((int) minX, (int) minY, (int) maxX, (int) maxY);
			canvas.translate(dx, dy);
			canvas.rotate(angle * 180.0f / (float) Math.PI);
			canvas.translate(-dx, -dy);
			drawable.draw(canvas);
			canvas.restore();
		}

		public String getPath() {
			return path;
		}

		public void setPath(String path) {
			this.path = path;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		public float getCenterX() {
			return centerX;
		}

		public float getCenterY() {
			return centerY;
		}

		public float getScaleX() {
			return scaleX;
		}

		public float getScaleY() {
			return scaleY;
		}

		public float getAngle() {
			return angle;
		}

		public float getMinX() {
			return minX;
		}

		public float getMaxX() {
			return maxX;
		}

		public float getMinY() {
			return minY;
		}

		public float getMaxY() {
			return maxY;
		}
	}

	@Override
	public boolean pointInObjectGrabArea(PointInfo touchPoint, Img obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void getPositionAndScale(Img img, PositionAndScale objPosAndScaleOut) {
		objPosAndScaleOut.set(img.getCenterX(), img.getCenterY(),
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) == 0,
				(img.getScaleX() + img.getScaleY()) / 2,
				(mUIMode & UI_MODE_ANISOTROPIC_SCALE) != 0, img.getScaleX(),
				img.getScaleY(), (mUIMode & UI_MODE_ROTATE) != 0,
				img.getAngle());
	}

	@Override
	public boolean setPositionAndScale(Img img,
			PositionAndScale newObjPosAndScale, PointInfo touchPoint) {
		currTouchPoint.set(touchPoint);
		boolean ok = img.setPos(newObjPosAndScale);
		if (ok)
			invalidate();
		return ok;
	}
}