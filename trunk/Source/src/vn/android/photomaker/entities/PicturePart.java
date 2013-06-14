package vn.android.photomaker.entities;

public class PicturePart {

	private int pictureID;
	private String pathImage;
	private float xOff;
	private float yOff;
	private float scale;
	private float scaleX;
	private float scaleY;
	private float angle;
	private int order;

	public PicturePart(String pathImage, float xOff, float yOff, float scale,
			float scaleX, float scaleY, float angle, int order) {
		this.pathImage = pathImage;
		this.xOff = xOff;
		this.yOff = yOff;
		this.scale = scale;
		this.scaleX = scaleX;
		this.scaleY = scaleY;
		this.angle = angle;
		this.order = order;
	}

	public int getPictureID() {
		return pictureID;
	}

	public void setPictureID(int pictureID) {
		this.pictureID = pictureID;
	}

	public String getPathImage() {
		return pathImage;
	}

	public void setPathImage(String pathImage) {
		this.pathImage = pathImage;
	}

	public float getxOff() {
		return xOff;
	}

	public void setxOff(float xOff) {
		this.xOff = xOff;
	}

	public float getyOff() {
		return yOff;
	}

	public void setyOff(float yOff) {
		this.yOff = yOff;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public float getScaleX() {
		return scaleX;
	}

	public void setScaleX(float scaleX) {
		this.scaleX = scaleX;
	}

	public float getScaleY() {
		return scaleY;
	}

	public void setScaleY(float scaleY) {
		this.scaleY = scaleY;
	}

	public float getAngle() {
		return angle;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

}
