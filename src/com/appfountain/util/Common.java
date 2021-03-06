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
import android.support.v4.app.FragmentManager;

import com.appfountain.R;
import com.appfountain.model.Category;
import com.appfountain.model.UserContainer;

/**
 * どこでも使えるCommonクラス
 */
public class Common {
	private static final String TAG = Common.class.getSimpleName();
	private static final int DEBUG = 0;

	private static final String USER_ID = "user_id";
	private static final String USER_NAME = "user_name";
	private static final String USER_PASSWORD = "user_password";
	private static final String USER_RK = "user_rk";

	private static UserContainer _user = null;
	private static String _baseApiUrl = null;
	private static String _baseIconUrl = null;
	private static String _postHeader = null;
	private static ProgressDialogFragment progressDialog;

	private static final List<Category> categories = new ArrayList<Category>(26) {
		{
			add(new Category(1, "APP_WIDGETS", "ウィジェット",
					R.drawable.category_app_widgets));
			add(new Category(2, "ENTERTAINMENT", "エンタテイメント",
					R.drawable.category_entertainment));
			add(new Category(3, "PERSONALIZATION", "カスタマイズ",
					R.drawable.category_personalization));
			add(new Category(4, "COMICS", "コミック", R.drawable.category_comics));
			add(new Category(5, "SHOPPING", "ショッピング",
					R.drawable.category_shopping));
			add(new Category(6, "SPORTS", "スポーツ", R.drawable.category_sports));
			add(new Category(7, "SOCIAL", "ソーシャルネットワーク",
					R.drawable.category_social));
			add(new Category(8, "TOOLS", "ツール", R.drawable.category_tools));
			add(new Category(9, "NEWS_AND_MAGAZINES", "ニュース＆雑誌",
					R.drawable.category_news_and_magazines));
			add(new Category(10, "BUSINESS", "ビジネス",
					R.drawable.category_business));
			add(new Category(11, "FINANCE", "ファイナンス",
					R.drawable.category_finance));
			add(new Category(12, "MEDIA_AND_VIDEO", "メディア＆動画",
					R.drawable.category_media_and_video));
			add(new Category(13, "LIFESTYLE", "ライフスタイル",
					R.drawable.category_lifestyle));
			add(new Category(14, "LIBRARIES_AND_DEMO", "ライブラリ＆デモ",
					R.drawable.category_libraries_and_demo));
			add(new Category(15, "APP_WALLPAPER", "ライブ壁紙",
					R.drawable.category_app_wallpaper));
			add(new Category(16, "TRANSPORTATION", "交通",
					R.drawable.category_transportation));
			add(new Category(17, "PRODUCTIVITY", "仕事効率化",
					R.drawable.category_productivity));
			add(new Category(18, "HEALTH_AND_FITNESS", "健康＆フィットネス",
					R.drawable.category_health_and_fitness));
			add(new Category(19, "PHOTOGRAPHY", "写真",
					R.drawable.category_photography));
			add(new Category(20, "MEDICAL", "医療",
					R.drawable.category_medical));
			add(new Category(21, "WEATHER", "天気",
					R.drawable.category_weather));
			add(new Category(22, "EDUCATION", "教育",
					R.drawable.category_education));
			add(new Category(23, "TRAVEL_AND_LOCAL", "旅行＆地域",
					R.drawable.category_travel_and_local));
			add(new Category(24, "BOOKS_AND_REFERENCE", "書籍＆文献",
					R.drawable.category_books_and_reference));
			add(new Category(25, "COMMUNICATION", "通信",
					R.drawable.category_communication));
			add(new Category(26, "MUSIC_AND_AUDIO", "音楽＆オーディオ",
					R.drawable.category_music_and_audio));
		}
	};

	/**
	 * プログレスバーを表示する
	 * 
	 * @param parent
	 * @param message
	 */
	public static void initializeProgressBar(FragmentManager manager,
			String message) {
		progressDialog = ProgressDialogFragment.newInstance(message);
		progressDialog.show(manager, "progress_dialog_fragment");
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
			loadConfig(context);
		}
		return _baseApiUrl;
	}

	/**
	 * icon取得用のbase urlの取得
	 * 
	 * @param context
	 * @return
	 */
	public static String getIconBaseUrl(Context context) {
		if (_baseIconUrl == null) {
			loadConfig(context);
		}
		return _baseIconUrl;
	}

	private static void loadConfig(Context context) {
		Properties props = new Properties();
		InputStream inputStream = context.getClass().getClassLoader()
				.getResourceAsStream("config.properties");
		try {
			props.load(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (DEBUG == 1) {
			_baseApiUrl = props.getProperty("debugapiurl");
			_baseIconUrl = props.getProperty("debugiconurl");
		} else {
			_baseApiUrl = props.getProperty("baseapiurl");
			_baseIconUrl = props.getProperty("baseiconurl");
		}
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
	 * @param id
	 * @param name
	 * @param password
	 * @param rk
	 */
	public static UserContainer setUserContainer(Context context, int id,
			String name, String password, String rk) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.putInt(USER_ID, id);
		editor.putString(USER_NAME, name);
		editor.putString(USER_PASSWORD, md5Hex(password));
		editor.putString(USER_RK, rk);
		editor.commit();

		_user = new UserContainer(id, name, password, rk);
		return _user;
	}

	/**
	 * Preferenceから取得
	 * 
	 * @param context
	 * @return
	 */
	public static UserContainer getUserContainer(Context context) {
		// キャッシュがあればそれを返す
		if (_user != null)
			return _user;

		// 無ければSharedPreferencesから取得
		UserContainer info = null;
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		int id = pref.getInt(USER_ID, 0);
		String name = pref.getString(USER_NAME, null);
		String password = pref.getString(USER_PASSWORD, null);
		String rk = pref.getString(USER_RK, null);
		if (name != null && password != null && rk != null)
			info = new UserContainer(id, name, password, rk);
		_user = info;
		return _user;
	}

	/**
	 * ユーザ情報を削除する
	 */
	public static void logout(Context context) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor editor = pref.edit();
		editor.remove(USER_ID);
		editor.remove(USER_NAME);
		editor.remove(USER_PASSWORD);
		editor.remove(USER_RK);
		editor.commit();
		_user = null;
	}

	/**
	 * カテゴリー一覧を取得
	 * 
	 * @return categories
	 */
	public static List<Category> getCategories() {
		return categories;
	}

	/**
	 * カテゴリを取得
	 * 
	 * @param categoryId
	 * @return
	 */
	public static Category getCategory(int categoryId) {
		return categories.get(categoryId - 1);
	}
}
