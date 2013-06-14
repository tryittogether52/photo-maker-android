/**
 * 
 */
package vn.android.photomaker.utils;

import vn.android.photomaker.common.ConstantVariable;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * @author TuyenDN.
 * 
 */
public class SharedPreferencesStore {

	/** Variable of SharedPreferences. */
	private SharedPreferences prefs;

	public SharedPreferencesStore(Context context) {
		this.prefs = context.getSharedPreferences(ConstantVariable.PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	/**
	 * This function is used for save data to SharedPreferences.
	 * 
	 * @param {key} Key of data.
	 * @param value
	 * @return true if save successful otherwise return false.
	 */
	public boolean saveString(String key, String value) {
		Editor editor = this.prefs.edit();
		editor.putString(key, value);
		return editor.commit();
	}

	/**
	 * This function is used for get data from SharedPreferences.
	 * 
	 * @param {key} Key of data.
	 * @return null if not existed otherwise return String value.
	 */
	public String getString(String key, String defaultValue) {
		return this.prefs.getString(key, defaultValue);
	}

	public boolean delete(String key) {
		Editor editor = this.prefs.edit();
		editor.remove(key);
		return editor.commit();
	}

	/**
	 * This function write an integer value into share preferences.
	 * 
	 * @param key
	 * @param nValue
	 */
	public void saveInt(String key, int nValue) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putInt(key, nValue);
		editor.commit();
	}

	/**
	 * This function get an integer value from share preference.
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key, int defaultValue) {
		int nValue = defaultValue;
		try {
			nValue = prefs.getInt(key, defaultValue);
		} catch (Exception e) {
			nValue = defaultValue;
		}
		return nValue;
	}

	/**
	 * This function write an long value into share preferences.
	 * 
	 * @param key
	 * @param nValue
	 */
	public void saveLong(String key, long nValue) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putLong(key, nValue);
		editor.commit();
	}

	/**
	 * This function get an long value from share preference.
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(String key, long defaultValue) {
		long nValue = defaultValue;
		try {
			nValue = prefs.getLong(key, defaultValue);
		} catch (Exception e) {
			nValue = defaultValue;
		}
		return nValue;
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		boolean res = defaultValue;
		try {
			res = prefs.getBoolean(key, defaultValue);
		} catch (Exception e) {
			res = defaultValue;
		}
		return res;
	}

	public void saveBoolean(String key, boolean stt) {
		Editor edit = prefs.edit();
		edit.putBoolean(key, stt);
		edit.commit();
	}
}
