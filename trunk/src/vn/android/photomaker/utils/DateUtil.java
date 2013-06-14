package vn.android.photomaker.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.util.Log;

public class DateUtil {

	/** Format date google contact. */
	public static final String DATE_CONTACT = "yyyy-MM-dd";
	
	/** Format date standard. */
	public static final String DATE_FORMAT = "yyyy/MM/dd";

	/** Format date facebook. */
	public static final String DATE_FORMAT_XSD = "yyyy-MM-dd'T'HH:mm:ss";

	/**
	 * Convert string to Date object with given format
	 * 
	 * @param dateStr
	 * @param format
	 * @return Date
	 */
	public final static Date stringToDate(String dateStr, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		Date date = null;
		try {
			date = dateFormat.parse(dateStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return date;
	}

	public final static String formatDate(Date date, String format) {
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String s = "";
		try {
			s = formatter.format(date);
		} catch (Exception e) {
			Log.e("DateUtils", "format date exception.", e);
		}
		return s;
	}

	/**
	 * Format date
	 * 
	 * @param year
	 * @param month
	 * @param day
	 * @param format
	 * @return String with given format
	 */
	public final static String formatDate(int year, int month, int day,
			String format) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		SimpleDateFormat formatter = new SimpleDateFormat(format);
		String s = formatter.format(c.getTime());
		return s;
	}

	public final static String formatDateByLocale(int year, int month, int day) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, year);
		c.set(Calendar.MONTH, month);
		c.set(Calendar.DAY_OF_MONTH, day);
		Locale current_locale = Locale.getDefault();
		String country_code = current_locale.getISO3Country();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
		if (country_code.equals("USA")) {
			formatter = new SimpleDateFormat("dd/MM/yyyy");
		}

		if (country_code.equals("JPN")) {
			formatter = new SimpleDateFormat("yyyy/MM/dd");
		}
		String s = formatter.format(c.getTime());
		return s;
	}

}
