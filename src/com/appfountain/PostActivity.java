package com.appfountain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.appfountain.component.AppChooseDialog;
import com.appfountain.component.AppChooseListener;
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
	private Spinner appSpinner;
	private ListView applicationList;
	private List<App> applications = new ArrayList<App>(3);
	private Button okButton;
	private Boolean isPosting = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post);

		user = Common.getUserContainer(this);
		if (user == null)
			finish();

		initViews();
	}

	private void initViews() {
		titleEditText = (EditText) findViewById(R.id.post_title_text);
		bodyEditText = (EditText) findViewById(R.id.post_body_text);
		categorySpinner = (Spinner) findViewById(R.id.post_category_spinner);
		okButton = (Button) findViewById(R.id.post_ok_button);
		applicationList = (ListView) findViewById(R.id.post_app_list);
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
		RequestQueue queue = Volley.newRequestQueue(this);

		Map<String, String> params = new HashMap<String, String>();
		params.put("title", title);
		params.put("body", body);
		params.put("category_id", "" + categoryId);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), user.getRk()); // POST時はrkをヘッダに付与

		GsonRequest<QuestionSource> req = new GsonRequest<QuestionSource>(
				Method.POST, url, QuestionSource.class, params, headers,
				new Listener<QuestionSource>() {
					@Override
					public void onResponse(QuestionSource response) {
						Common.closeProgressBar();
						if (response.isSuccess()) {
							// TopPageへの遷移
							Intent intent = new Intent(self,
									TopPageActivity.class);
							startActivity(intent);
							self.finish();
						} else {
							Toast.makeText(self, response.getMessage(),
									Toast.LENGTH_SHORT).show();
							okButton.setClickable(true);
							isPosting = false;
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
		// TODO Auto-generated method stub
		Log.d(TAG, app.getName());
	}
}
