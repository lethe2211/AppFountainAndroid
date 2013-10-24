package com.appfountain.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
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

import com.appfountain.model.Category;
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
	private static String _postHeader = null;
	private static ProgressDialogFragment progressDialog;

	private static final List<Category> categories = new ArrayList<Category>(26) {
		{
			add(new Category(1, "APP_WIDGETS", "ウィジェット"));
			add(new Category(2, "ENTERTAINMENT", "エンタテイメント"));
			add(new Category(3, "PERSONALIZATION", "カスタマイズ"));
			add(new Category(4, "COMICS", "コミック"));
			add(new Category(5, "SHOPPING", "ショッピング"));
			add(new Category(6, "SPORTS", "スポーツ"));
			add(new Category(7, "SOCIAL", "ソーシャルネットワーク"));
			add(new Category(8, "TOOLS", "ツール"));
			add(new Category(9, "NEWS_AND_MAGAZINES", "ニュース＆雑誌"));
			add(new Category(10, "BUSINESS", "ビジネス"));
			add(new Category(11, "FINANCE", "ファイナンス"));
			add(new Category(12, "MEDIA_AND_VIDEO", "メディア＆動画"));
			add(new Category(13, "LIFESTYLE", "ライフスタイル"));
			add(new Category(14, "LIBRARIES_AND_DEMO", "ライブラリ＆デモ"));
			add(new Category(15, "APP_WALLPAPER", "ライブ壁紙"));
			add(new Category(16, "TRANSPORTATION", "交通"));
			add(new Category(17, "PRODUCTIVITY", "仕事効率化"));
			add(new Category(18, "HEALTH_AND_FITNESS", "健康＆フィットネス"));
			add(new Category(19, "PHOTOGRAPHY", "写真"));
			add(new Category(20, "MEDICAL", "医療"));
			add(new Category(21, "WEATHER", "天気"));
			add(new Category(22, "EDUCATION", "教育"));
			add(new Category(23, "TRAVEL_AND_LOCAL", "旅行＆地域"));
			add(new Category(24, "BOOKS_AND_REFERENCE", "書籍＆文献"));
			add(new Category(25, "COMMUNICATION", "通信"));
			add(new Category(26, "MUSIC_AND_AUDIO", "音楽＆オーディオ"));
		}
	};

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
	 * post headerのキー取り出す
	 * 
	 * @return
	 */
	public static String getPostHeader(Context context) {
		if (_postHeader == null) {
			Properties props = new Properties();
			InputStream inputStream = context.getClass().getClassLoader()
					.getResourceAsStream("config.properties");
			try {
				props.load(inputStream);
			} catch (IOException e) {
				e.printStackTrace();
			}
			_postHeader = props.getProperty("postheader");
		}
		return _postHeader;
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

	/**
	 * カテゴリー一覧を取得
	 * 
	 * @return categories
	 */
	public static List<Category> getCategories() {
		return categories;
	}
}
