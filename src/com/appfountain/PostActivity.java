package com.appfountain;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.AppChooseDialog;
import com.appfountain.component.AppChooseListener;
import com.appfountain.external.DrawableUploadRequest;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionSource;
import com.appfountain.external.SimpleSource;
import com.appfountain.model.App;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/**
 * 質問投稿ページ
 */
public class PostActivity extends ActionBarActivity implements
		AppChooseListener {
	private static final String TAG = PostActivity.class.getSimpleName();

	public static final int TITLE_RESULT = 1;
	public static final int BODY_RESULT = 2;

	private final String url = Common.getApiBaseUrl(this) + "question";

	private PostActivity self = this;
	private UserContainer user = null;
	private EditText titleEditText;
	private EditText bodyEditText;
	private Spinner categorySpinner;
	private LinearLayout applicationList;
	private List<App> applications = new ArrayList<App>(3);
	private Button okButton;
	private Boolean isPosting = false;
	private RequestQueue queue = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		user = Common.getUserContainer(this);
		if (user == null)
			finish();

		initViews();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Homeボタンが押されたら戻る
		case android.R.id.home:
			Log.d("home", "clicked!");
			finish();
			break;

		}
		return false;
	}

	private void initViews() {
		titleEditText = (EditText) findViewById(R.id.post_title_text);
		titleEditText.clearFocus();
		bodyEditText = (EditText) findViewById(R.id.post_body_text);
		categorySpinner = (Spinner) findViewById(R.id.post_category_spinner);
		okButton = (Button) findViewById(R.id.post_ok_button);
		applicationList = (LinearLayout) findViewById(R.id.post_app_list);
	}

	// アプリ選択ボタン押下時
	public void chooseApp(View view) {
		new AppChooseDialog().show(getSupportFragmentManager(), "choose_app");
	}

	// titleEditTextがクリックされた時に遷移
	public void onTitleClick(View view) {
		Intent intent = new Intent(this, PostTitleActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT + "title", titleEditText.getText()
				.toString());
		startActivityForResult(intent, TITLE_RESULT); // onActivityResultで返却値を受け取るためにForResult
	}

	// bodyEditTextがクリックされた時に遷移
	public void onBodyClick(View view) {
		Intent intent = new Intent(this, PostBodyActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT + "body", bodyEditText.getText()
				.toString());
		startActivityForResult(intent, BODY_RESULT); // onActivityResultで返却値を受け取るためにForResult
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String text_title = data.getStringExtra(Intent.EXTRA_TEXT + "title");
		String text_body = data.getStringExtra(Intent.EXTRA_TEXT + "body");
		if (text_title != null || text_body != null) {
			switch (requestCode) {
			case TITLE_RESULT:
				Log.d("text_title", text_title);
				titleEditText.setText(text_title);
				break;
			case BODY_RESULT:
				Log.d("text_body", text_body);
				bodyEditText.setText(text_body);
				break;
			}
		}
	}

	// 投稿ボタンを推した際の処理
	public void onOkClick(View view) {
		String title = titleEditText.getText().toString();
		String body = bodyEditText.getText().toString();
		int categoryId = categorySpinner.getSelectedItemPosition();

		if (!isValidInfo(title, categoryId))
			return;

		// 連続でクリックされないようにClick出来なくする
		okButton.setClickable(false);
		// クリックイベント複数回呼ばれる事あるからフラグ立てる
		if (!isPosting) {
			isPosting = true;
			postQuestion(title, body, categoryId);
		}
	}

	private void postQuestion(String title, String body, int categoryId) {
		if (!Common.isInternetAvailable(self)) {
			Toast.makeText(self,
					getString(R.string.common_internet_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		if (queue == null)
			queue = Volley.newRequestQueue(this);

		Map<String, String> params = new HashMap<String, String>();
		params.put("title", title);
		params.put("body", body);
		params.put("category_id", "" + categoryId);
		// TODO 鯖側0始まりになおす
		for (int i = 0; i < applications.size(); ++i) {
			App app = applications.get(i);
			params.put("appname" + (i + 1), app.getName());
			params.put("apppackage" + (i + 1), app.getPackageName());
		}

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), user.getRk()); // POST時はrkをヘッダに付与

		GsonRequest<QuestionSource> req = new GsonRequest<QuestionSource>(
				Method.POST, url, QuestionSource.class, params, headers,
				new Listener<QuestionSource>() {
					@Override
					public void onResponse(QuestionSource response) {
						// 鯖にiconがupされてないアプリがあればそれをuploadしてもらう
						List<App> lackImageApps = response.getApplications();
						if (lackImageApps != null && lackImageApps.size() > 0) {
							postIcons(lackImageApps);
						} else {
							Common.closeProgressBar();
							setResult(RESULT_OK);
							finish();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Common.closeProgressBar();
						String responseBody = null;
						try {
							responseBody = new String(
									error.networkResponse.data, "utf-8");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Toast.makeText(self, responseBody, Toast.LENGTH_SHORT)
								.show();
						okButton.setClickable(false);
						isPosting = false;
					}
				});

		queue.add(req);
		Common.initializeProgressBar(this.getSupportFragmentManager(), "投稿中...");
	}

	private int index = 0;

	private void postIcons(List<App> lackImageApps) {
		if (queue == null)
			queue = Volley.newRequestQueue(this);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), user.getRk()); // POST時はrkをヘッダに付与

		final int count = lackImageApps.size();
		index = 0;
		// 鯖からの返却値であるlackImageAppsにはDrawableは含まれていない
		// lackImageAppsの情報を基に，localからDrawableのあるAppを取得してuploadする
		for (App app : lackImageApps) {
			App uploadApp = null;
			for (App localApp : applications) {
				if (localApp.getPackageName().equals(app.getPackageName())) {
					uploadApp = localApp;
					break;
				}
			}
			DrawableUploadRequest<SimpleSource> req = new DrawableUploadRequest<SimpleSource>(
					getUploadAPIUrl(uploadApp.getPackageName()),
					SimpleSource.class, uploadApp.getIcon(), headers,
					new Listener<SimpleSource>() {
						@Override
						public void onResponse(SimpleSource response) {
							index++;
							if (count >= index) {
								Common.closeProgressBar();
								setResult(RESULT_OK);
								finish();
							}
						}
					}, new ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Common.closeProgressBar();
							try {
								String responseBody = new String(
										error.networkResponse.data, "utf-8");
								Toast.makeText(self, responseBody,
										Toast.LENGTH_SHORT).show();
							} catch (UnsupportedEncodingException e) {
							}
							okButton.setClickable(false);
							isPosting = false;
						}
					});
			queue.add(req);
		}
	}

	private String getUploadAPIUrl(String packageName) {
		return Common.getApiBaseUrl(this) + "icon/" + packageName;
	}

	private boolean isValidInfo(String title, int categoryId) {
		if (title.length() < 4) {
			Toast.makeText(this, "タイトルが短すぎます", Toast.LENGTH_SHORT).show();
			return false;
		}

		if (categoryId == 0) {
			Toast.makeText(this, "カテゴリを選んでください", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	@Override
	public void onChoosed(App app) {
		if (!containApps(applications, app)) {
			if(applications.size() < 3){
			applications.add(app);
			applicationList.addView(setApplicationContainer(app));
			}else{
				Toast.makeText(self, "参考アプリは最大3個です",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private View setApplicationContainer(App app) {
		LayoutInflater myinflater = (LayoutInflater) getApplicationContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout appLayout = (RelativeLayout) myinflater.inflate(
				R.layout.list_item_choose_app, null);
		setApplicationLayout(app, appLayout);
		return appLayout;
	}

	private void setApplicationLayout(App app, RelativeLayout appLayout) {
		((ImageView) appLayout.findViewById(R.id.list_item_app_icon))
				.setImageDrawable(app.getIcon());
		((TextView) appLayout.findViewById(R.id.list_item_app_name))
				.setText(app.getName());
	}

	private boolean containApps(List<App> applications, App app) {
		String packageName = app.getPackageName();
		for (App ap : applications) {
			if (ap.getPackageName().equals(packageName))
				return true;
		}
		return false;
	}
}
