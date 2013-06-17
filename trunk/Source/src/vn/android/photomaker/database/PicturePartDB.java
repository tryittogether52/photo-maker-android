package vn.android.photomaker.database;

import java.util.ArrayList;
import java.util.List;

import vn.android.photomaker.common.ConstantVariable;
import vn.android.photomaker.entities.PicturePart;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

public class PicturePartDB {

	/** An instance variable for DatabaseContentProvider. */
	private DatabaseContentProvider provider;
	private Uri uri = Uri.parse(ConstantVariable.CONTENT_URL + "/"
			+ DatabaseHelper.DB_TABLE_PICPART);

	/**
	 * The constructor.
	 * 
	 * @param {context} The Context of application.
	 * 
	 * */
	public PicturePartDB() {
		provider = new DatabaseContentProvider();
	}

	/**
	 * Add history to database.
	 * 
	 * @param {item} Object of activity.
	 * @return
	 */
	public synchronized int add(PicturePart item) {

		int rs = -1;
		ContentValues cv = new ContentValues();
		cv.put(DatabaseHelper.PART_PATH, item.getPathImage());
		cv.put(DatabaseHelper.PART_XOFF, item.getxOff());
		cv.put(DatabaseHelper.PART_YOFF, item.getyOff());
		cv.put(DatabaseHelper.PART_SCALE, item.getScale());
		cv.put(DatabaseHelper.PART_SCALEX, item.getScaleX());
		cv.put(DatabaseHelper.PART_SCALEY, item.getScaleY());
		cv.put(DatabaseHelper.PART_ANGLE, item.getAngle());
		cv.put(DatabaseHelper.PART_ORDER, item.getOrder());

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
		int rs = provider.delete(uri, DatabaseHelper.PART_ID + "=?",
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
	 * Return all part of picture from database.
	 * 
	 * @return {@link List<PicturePart>}
	 * */
	public synchronized List<PicturePart> select() {
		List<PicturePart> list = new ArrayList<PicturePart>();
		Cursor cursor = provider.query(uri, new String[] {
				DatabaseHelper.PART_ID, DatabaseHelper.PIC_ID,
				DatabaseHelper.PART_PATH, DatabaseHelper.PART_XOFF,
				DatabaseHelper.PART_YOFF, DatabaseHelper.PART_SCALE,
				DatabaseHelper.PART_SCALEX, DatabaseHelper.PART_SCALEY,
				DatabaseHelper.PART_ANGLE, DatabaseHelper.PART_ORDER }, null,
				null, DatabaseHelper.PART_ORDER);
		if (cursor.moveToFirst()) {
			do {
				list.add(new PicturePart(cursor.getInt(0), cursor.getInt(1),
						cursor.getString(2), cursor.getFloat(3), cursor
								.getFloat(4), cursor.getFloat(5), cursor
								.getFloat(6), cursor.getFloat(7), cursor
								.getFloat(8), cursor.getInt(9)));
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
	public synchronized PicturePart select(int id) {

		PicturePart mHistoryItem = null;
		Cursor cursor = provider.query(uri, new String[] {
				DatabaseHelper.PART_ID, DatabaseHelper.PIC_ID,
				DatabaseHelper.PART_PATH, DatabaseHelper.PART_XOFF,
				DatabaseHelper.PART_YOFF, DatabaseHelper.PART_SCALE,
				DatabaseHelper.PART_SCALEX, DatabaseHelper.PART_SCALEY,
				DatabaseHelper.PART_ANGLE, DatabaseHelper.PART_ORDER },
				DatabaseHelper.PIC_ID + "=?", new String[] { "" + id }, null);

		if (cursor.moveToFirst()) {
			mHistoryItem = new PicturePart(cursor.getInt(0), cursor.getInt(1),
					cursor.getString(2), cursor.getFloat(3),
					cursor.getFloat(4), cursor.getFloat(5), cursor.getFloat(6),
					cursor.getFloat(7), cursor.getFloat(8), cursor.getInt(9));
		}
		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return mHistoryItem;
	}

}
