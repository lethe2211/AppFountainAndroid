package com.appfountain.util;

import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Pair;

import com.appfountain.model.User;
import com.appfountain.model.UserContainer;

/**
 * どこでも使えるCommonクラス
 */
public class Common {
	private static final String USER_NAME = "user_name";
	private static final String USER_PASSWORD = "user_password";
	private static final String USER_RK = "user_rk";

	public static User user = null;
	public static ProgressDialog progressBar;

	/**
	 * プログレスバーを表示する
	 * 
	 * @param parent
	 * @param message
	 */
	public static void initializeProgressBar(Activity parent, String message) {
		final ProgressDialog progressDialog = new ProgressDialog(parent);
		progressDialog.setMessage(message);
		progressDialog.setCancelable(false);
		progressBar = progressDialog;
		progressBar.show();
	}

	/**
	 * プログレスバーを閉じる
	 */
	public static void closeProgressBar() {
		if (progressBar != null && progressBar.isShowing()) {
			progressBar.hide();
		}
	}

	/**
	 * インターネットが利用可能か調べる
	 * 
	 * @param context
	 * @return
	 */
	public static Boolean isInternetAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm == null)
			return false;

		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni != null && ni.isAvailable() && ni.isConnected())
			return true;
		return false;
	}

	/**
	 * md5への変換
	 * 
	 * @param str
	 * @return
	 */
	public static String md5Hex(String str) {
		return DigestUtils.md5Hex(str);
	}

	/**
	 * Preferenceへの保存
	 * 
	 * @param context
	 * @param name
	 * @param password
	 * @param rk
	 */
	public static void registerUser(Context context, String name,
			String password, String rk) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putString(USER_NAME, user.getName());
		editor.putString(USER_PASSWORD, md5Hex(password));
		editor.commit();
	}

	/**
	 * Preferenceから取得（name及びpasswordのみ）
	 * 
	 * @param context
	 * @return
	 */
	public static UserContainer fetchUser(Context context) {
		UserContainer info = null;
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		String name = pref.getString(USER_NAME, null);
		String password = pref.getString(USER_PASSWORD, null);
		String rk = pref.getString(USER_RK, null);
		if (name != null && password != null && rk != null)
			info = new UserContainer(name, password, rk);
		return info;
	}

	/**
	 * キャッシュへの保存
	 * 
	 * @param user
	 */
	public static void setUser(User user) {
		Common.user = user;
	}

	/**
	 * キャッシュからユーザ情報を取得
	 * 
	 * @return
	 */
	public static User getUser() {
		return user;
	}
}
