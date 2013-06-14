package vn.android.photomaker.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DatabaseContentProvider extends ContentProvider {

	/** An instance variable for DatabaseHelper. */
	private DatabaseHelper databaseHelper;

	/** An instance variable for SQLiteDatabase. */
	private static SQLiteDatabase mDB;

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// Enable foreign key constraints.
		if (!mDB.isReadOnly()) {
			mDB.execSQL("PRAGMA foreign_keys = ON;");
		}
		String tableName = uri.getLastPathSegment();
		int result = mDB.delete(tableName, selection, selectionArgs);
		if (getContext() != null)
			getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		String tableName = uri.getLastPathSegment();
		long id = mDB.insert(tableName, null, values);
		if (getContext() != null)
			getContext().getContentResolver().notifyChange(uri, null);
		return Uri.parse(uri.toString() + "/" + id);
	}

	@Override
	public boolean onCreate() {
		databaseHelper = new DatabaseHelper(getContext());
		mDB = databaseHelper.getHelper();
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String tableName = uri.getLastPathSegment();
		return mDB.query(tableName, projection, selection, selectionArgs, null,
				null, sortOrder);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		String tableName = uri.getLastPathSegment();
		int result = mDB.update(tableName, values, selection, selectionArgs);
		if (getContext() != null)
			getContext().getContentResolver().notifyChange(uri, null);
		return result;
	}

}
