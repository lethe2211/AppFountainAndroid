package com.appfountain.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;

import com.appfountain.model.User;
import com.appfountain.model.UserContainer;

/**
 * どこでも使えるCommonクラス
 */
public class Common {
	private static final String TAG = Common.class.getSimpleName();

	private static final String USER_NAME = "user_name";
	private static final String USER_PASSWORD = "user_password";
	private static final String USER_RK = "user_rk";

	private static User _user = null;
	private static String _baseApiUrl = null;
	private static ProgressDialogFragment progressDialog;

	/**
	 * プログレスバーを表示する
	 * 
	 * @param parent
	 * @param message
	 */
	public static void initializeProgressBar(ActionBarActivity parent,
			String message) {
		progressDialog = ProgressDialogFragment.newInstance(message);
		progressDialog.show(parent.getSupportFragmentManager(),
				"progress_dialog_fragment");
	}

	/**
	 * プログレスバーを閉じる
	 */
	public static void closeProgressBar() {
		if (progressDialog != null) {
			progressDialog.dismiss();
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
	 * apiのbase urlの取得
	 * 
	 * @param context
	 * @return
	 */
	public static String getApiBaseUrl(Context context) {
		if (_baseApiUrl == null) {
			Properties props = new Properties();
			InputStream inputStream = context.getClass().getClassLoader()
					.getResourceAsStream("config.properties");
			try {
				props.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			_baseApiUrl = props.getProperty("baseapiurl");
		}
		return _baseApiUrl;
	}

	/**
	 * md5への変換
	 * 
	 * @param str
	 * @return
	 */
	public static String md5Hex(String str) {
		return new String(Hex.encodeHex(DigestUtils.md5(str)));
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
		editor.putString(USER_NAME, name);
		editor.putString(USER_PASSWORD, md5Hex(password));
		editor.putString(USER_RK, rk);
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
		_user = user;
	}

	/**
	 * キャッシュからユーザ情報を取得
	 * 
	 * @return
	 */
	public static User getUser() {
		return _user;
	}
}
