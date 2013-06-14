package vn.android.photomaker.database;

import java.util.ArrayList;
import java.util.List;

import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.entities.Picture;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class PictureDB {

	/** An instance variable for DatabaseContentProvider. */
	private DatabaseContentProvider provider;
	private Uri uri = Uri.parse(ConstantVariable.CONTENT_URL + "/"
			+ DatabaseHelper.DB_TABLE_PICTURES);

	/**
	 * The constructor.
	 * 
	 * @param {context} The Context of application.
	 * 
	 * */
	public PictureDB() {
		provider = new DatabaseContentProvider();
	}

	/**
	 * Add history to database.
	 * 
	 * @param {item} Object of activity.
	 * @return
	 */
	public synchronized int add(Picture item) {

		int rs = -1;
		ContentValues cv = new ContentValues();
		// put data.
		Uri result = provider.insert(uri, cv);
		String id = result.getLastPathSegment();
		try {
			rs = Integer.parseInt(id);
		} catch (Exception e) {
			rs = -1;
		}
		return rs;
	}

	/**
	 * Delete picture.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized boolean delete(int id) {
		boolean result = false;
		int rs = provider.delete(uri, DatabaseHelper.PIC_ID + "=?",
				new String[] { "" + id });
		if (rs != 0)
			result = true;
		return result;
	}

	/**
	 * Delete all picture.
	 * 
	 * @return
	 */
	public synchronized boolean delete() {

		boolean result = false;
		int rs = provider.delete(uri, null, null);
		if (rs != 0)
			result = true;
		return result;
	}

	/**
	 * Return all picture from database.
	 * 
	 * @return {@link List<Picture>}
	 * */
	public synchronized List<Picture> select() {
		List<Picture> list = new ArrayList<Picture>();
		Cursor cursor = provider.query(uri, new String[] {
				DatabaseHelper.PIC_ID, DatabaseHelper.PIC_PATH,
				DatabaseHelper.PIC_BG, DatabaseHelper.PIC_CREATEDATE,
				DatabaseHelper.PIC_MODIFIEDDATE, DatabaseHelper.PIC_INDEX,
				DatabaseHelper.PIC_PAGEINDEX }, null, null,
				DatabaseHelper.PIC_ID + " DESC");
		if (cursor.moveToFirst()) {
			do {
				list.add(new Picture(cursor.getInt(0), cursor.getString(1),
						cursor.getInt(2), null, cursor.getString(3), cursor
								.getString(4), cursor.getInt(5), cursor
								.getInt(6)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * Select picture by id.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized Picture select(int id) {

		Picture mHistoryItem = null;
		Cursor cursor = provider.query(uri, new String[] {
				DatabaseHelper.PIC_ID, DatabaseHelper.PIC_PATH,
				DatabaseHelper.PIC_BG, DatabaseHelper.PIC_CREATEDATE,
				DatabaseHelper.PIC_MODIFIEDDATE, DatabaseHelper.PIC_INDEX,
				DatabaseHelper.PIC_PAGEINDEX }, DatabaseHelper.PIC_ID + "=?",
				new String[] { "" + id }, DatabaseHelper.PIC_ID + " DESC");
		if (cursor.moveToFirst()) {
			mHistoryItem = new Picture(cursor.getInt(0), cursor.getString(1),
					cursor.getInt(2), null, cursor.getString(3),
					cursor.getString(4), cursor.getInt(5), cursor.getInt(6));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return mHistoryItem;
	}

	/**
	 * Select picture by page.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized List<Picture> selectAll(int pageIndex) {

		List<Picture> list = new ArrayList<Picture>();
		Cursor cursor = provider.query(uri, new String[] {
				DatabaseHelper.PIC_ID, DatabaseHelper.PIC_PATH,
				DatabaseHelper.PIC_BG, DatabaseHelper.PIC_CREATEDATE,
				DatabaseHelper.PIC_MODIFIEDDATE, DatabaseHelper.PIC_INDEX,
				DatabaseHelper.PIC_PAGEINDEX }, DatabaseHelper.PIC_PAGEINDEX
				+ "=?", new String[] { "" + pageIndex }, DatabaseHelper.PIC_ID
				+ " DESC");
		if (cursor.moveToFirst()) {
			do {
				list.add(new Picture(cursor.getInt(0), cursor.getString(1),
						cursor.getInt(2), null, cursor.getString(3), cursor
								.getString(4), cursor.getInt(5), cursor
								.getInt(6)));
			} while (cursor.moveToNext());
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return list;
	}

	/**
	 * Get page of id.
	 * 
	 * @param id
	 * @return
	 */
	public int getPage(int id) {
		int result = 0;
		List<Picture> list = this.select();
		int i;
		for (i = 1; i <= list.size(); ++i) {
			Picture a = list.get(i - 1);
			if (a.getId() == id)
				break;
		}
		if (i == list.size() + 1) {
			return result;
		}
		result = (int) Math.floor((double) i / ConstantVariable.PAGE_SIZE);
		if (i % ConstantVariable.PAGE_SIZE == 0) {
			--result;
		}
		return result;
	}
}
