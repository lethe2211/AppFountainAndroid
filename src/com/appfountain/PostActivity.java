package com.appfountain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.AppAdapter;
import com.appfountain.component.AppChooseDialog;
import com.appfountain.component.AppChooseListener;
import com.appfountain.external.BaseSource;
import com.appfountain.external.DrawableUploadRequest;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionSource;
import com.appfountain.model.App;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/**
 * 質問投稿ページ
 */
public class PostActivity extends ActionBarActivity implements
		AppChooseListener {
	private static final String TAG = PostActivity.class.getSimpleName();

	public static final int BODY_RESULT = 1;

	private final String url = Common.getApiBaseUrl(this) + "question";

	private PostActivity self = this;
	private UserContainer user = null;
	private EditText titleEditText;
	private EditText bodyEditText;
	private Spinner categorySpinner;
	private ListView applicationList;
	private List<App> applications = new ArrayList<App>(3);
	private AppAdapter applicationAdapter;
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
		bodyEditText = (EditText) findViewById(R.id.post_body_text);
		categorySpinner = (Spinner) findViewById(R.id.post_category_spinner);
		okButton = (Button) findViewById(R.id.post_ok_button);
		applicationList = (ListView) findViewById(R.id.post_app_list);

		applicationAdapter = new AppAdapter(this,
				R.layout.list_item_choose_app, applications);
		applicationList.setAdapter(applicationAdapter);
	}

	// アプリ選択ボタン押下時
	public void chooseApp(View view) {
		new AppChooseDialog().show(getSupportFragmentManager(), "choose_app");
	}

	// bodyEditTextがクリックされた時に遷移
	public void onBodyClick(View view) {
		Intent intent = new Intent(this, PostBodyActivity.class);
		intent.putExtra(Intent.EXTRA_TEXT, bodyEditText.getText().toString());
		startActivityForResult(intent, BODY_RESULT); // onActivityResultで返却値を受け取るためにForResult
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String text = data.getStringExtra(Intent.EXTRA_TEXT);
		if (text != null) {
			switch (requestCode) {
			case BODY_RESULT:
				bodyEditText.setText(text);
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
						Toast.makeText(self, error.getMessage(),
								Toast.LENGTH_SHORT).show();
						okButton.setClickable(false);
						isPosting = false;
					}
				});

		queue.add(req);
		Common.initializeProgressBar(this, "投稿中...");
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
			DrawableUploadRequest<BaseSource> req = new DrawableUploadRequest<BaseSource>(
					getUploadAPIUrl(uploadApp.getPackageName()),
					BaseSource.class, uploadApp.getIcon(), headers,
					new Listener<BaseSource>() {
						@Override
						public void onResponse(BaseSource response) {
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
							Toast.makeText(self, error.getMessage(),
									Toast.LENGTH_SHORT).show();
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
			applications.add(app);
			applicationAdapter.notifyDataSetChanged();
		}
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
