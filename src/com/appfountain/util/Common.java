package com.appfountain.util;

import org.apache.commons.codec.digest.DigestUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * どこでも使えるCommonクラス プログレスバー，インターネットのチェック
 */
public class Common {
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

	public static String md5Hex(String str) {
		return DigestUtils.md5Hex(str);
	}
}
