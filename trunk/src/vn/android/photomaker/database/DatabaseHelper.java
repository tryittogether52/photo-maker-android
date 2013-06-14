package vn.android.photomaker.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 
 * @author tuyendn
 * 
 */
public class DatabaseHelper {

	/** Database name */
	private static final String DATABASE_NAME = "photomaker.db";

	/** Database version */
	private static final int DATABASE_VERSION = 1;

	/********************** PICTURES ***********************/
	public static final String DB_TABLE_PICTURES = "tb_pictures";
	public static final String PIC_ID = "pic_id";
	public static final String PIC_PATH = "pic_path";
	public static final String PIC_BG = "pic_background";
	public static final String PIC_CREATEDATE = "pic_createdate";
	public static final String PIC_MODIFIEDDATE = "pic_modifieddate";
	public static final String PIC_INDEX = "pic_index";
	public static final String PIC_PAGEINDEX = "pic_pageindex";

	/********************** PART OF PICTURES ***********************/
	public static final String DB_TABLE_PICPART = "tb_picture_part";
	public static final String PART_ID = "part_id";
	public static final String PART_PATH = "part_path";
	public static final String PART_XOFF = "part_xoff";
	public static final String PART_YOFF = "part_yoff";
	public static final String PART_SCALE = "part_scale";
	public static final String PART_SCALEX = "part_scalex";
	public static final String PART_SCALEY = "part_scaley";
	public static final String PART_ANGLE = "part_angle";
	public static final String PART_ORDER = "part_order";

	/** Helper open connection. */
	protected OpenHelper openHelper;

	/** Instance for application access. */
	public static DatabaseHelper instance;

	/**
	 * Initial instance of database helper.
	 * 
	 * @param context
	 *            The context.
	 * @return A reference to instance of {@link DatabaseHelper}
	 * */
	public static synchronized DatabaseHelper getInstance(final Context context) {
		if (null == instance) {
			instance = new DatabaseHelper(context);
		}
		return instance;
	}

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The context.
	 * */
	public DatabaseHelper(Context context) {
		this.openHelper = new OpenHelper(context);
	}

	public synchronized SQLiteDatabase getHelper() {
		return openHelper.getWritableDatabase();
	}

	static class OpenHelper extends SQLiteOpenHelper {

		public OpenHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {

			StringBuilder sqlBuilder = null;

			// Create picture table.
			sqlBuilder = new StringBuilder();
			sqlBuilder.append("CREATE TABLE ");
			sqlBuilder.append(DB_TABLE_PICTURES);
			sqlBuilder.append("(");
			sqlBuilder.append(PIC_ID);
			sqlBuilder.append(" INTEGER PRIMARY KEY,");
			sqlBuilder.append(PIC_PATH);
			sqlBuilder.append(" TEXT,");
			sqlBuilder.append(PIC_BG);
			sqlBuilder.append(" INTEGER, ");
			sqlBuilder.append(PIC_CREATEDATE);
			sqlBuilder.append(" TEXT, ");
			sqlBuilder.append(PIC_MODIFIEDDATE);
			sqlBuilder.append(" TEXT, ");
			sqlBuilder.append(PIC_INDEX);
			sqlBuilder.append(" INTEGER, ");
			sqlBuilder.append(PIC_PAGEINDEX);
			sqlBuilder.append(" INTEGER");
			sqlBuilder.append(")");
			db.execSQL(sqlBuilder.toString());

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub

		}

	}
}
