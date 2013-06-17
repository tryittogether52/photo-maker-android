package vn.android.photomaker.entities;

import java.util.Date;

import vn.android.photomaker.utils.DateUtil;

public class Picture implements Comparable<Picture> {

	private int id;
	private String path;
	private int background;
	private Date createDate;
	private Date modifiedDate;
	private int index;
	private int pageIndex;

	public Picture(String path, int background, Date createDate,
			Date modifiedDate, int index, int pageIndex) {
		this.path = path;
		this.background = background;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
		this.index = index;
		this.pageIndex = pageIndex;
	}

	public Picture(int id, String path, int background, String createDate,
			String modifiedDate, int index, int pageIndex) {
		super();
		this.id = id;
		this.path = path;
		this.background = background;
		Date date;
		if (createDate != null && createDate.length() > 0) {
			date = DateUtil.stringToDate(createDate, DateUtil.DATE_FORMAT);
			this.createDate = date;
		}
		if (modifiedDate != null && modifiedDate.length() > 0) {
			date = DateUtil.stringToDate(modifiedDate, DateUtil.DATE_FORMAT);
			this.modifiedDate = date;
		}
		this.index = index;
		this.pageIndex = pageIndex;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getBackground() {
		return background;
	}

	public void setBackground(int background) {
		this.background = background;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	@Override
	public int compareTo(Picture another) {
		if (this.index > another.getIndex()) {
			return 1;
		} else if (this.index < another.getIndex()) {
			return -1;
		} else
			return 0;
	}

}
