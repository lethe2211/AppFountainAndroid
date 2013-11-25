package com.appfountain;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.InputJapaneseFilter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UserSource;
import com.appfountain.model.User;
import com.appfountain.util.Common;

/**
 * ログインページのための画面
 */
public class LoginActivity extends ActionBarActivity {
	private static final String TAG = LoginActivity.class.getSimpleName();

	private final String url = Common.getApiBaseUrl(this) + "user/login";
	private static final int MAX_NAME_LENGTH = 32;
	private static final int MIN_NAME_LENGTH = 4;
	private static final int MIN_PASSWORD_LENGTH = 4;

	private ActionBarActivity self = this;
	private RequestQueue queue;

	private EditText loginName;
	private EditText loginPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		// Homeボタンを押せるようにする
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		initViews();

		queue = Volley.newRequestQueue(this);
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
		// ログイン系のinitialize
		loginName = (EditText) findViewById(R.id.login_name);
		loginPassword = (EditText) findViewById(R.id.login_password);

		// ボタンのinitialize
		findViewById(R.id.login_button).setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!Common.isInternetAvailable(self)) {
							Toast.makeText(
									self,
									getString(R.string.common_internet_unavailable),
									Toast.LENGTH_SHORT).show();
							return;
						}
						loginButtonClicked(loginName.getText().toString(),
								loginPassword.getText().toString());
					}
				});

        findViewById(R.id.login_user_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(LoginActivity.this,
                        RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
	}

	private void loginButtonClicked(final String name, final String password) {
		if (!isValidInput(name, password))
			return;

		final String md5Password = Common.md5Hex(password);
		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("password", md5Password);

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(this), "true"); // 値はなんでも良い

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.POST,
				url, UserSource.class, params, headers,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						Common.closeProgressBar();
						// User情報を端末へ登録&キャッシュ
						User user = response.getUser();
						Common.setUserContainer(self, user.getId(),
								user.getName(), md5Password, user.getRk());

						// TopPageへの遷移
						Intent intent = new Intent(self, TopPageActivity.class);
						startActivity(intent);
						self.finish(); // いらない画面は終了させる
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
						clearLoginInfo();
					}
				});

		queue.add(req);
		Common.initializeProgressBar(this.getSupportFragmentManager(),
				"login...");
	}

	// 入力チェック
	private boolean isValidInput(String name, String password) {
		if (!(MIN_NAME_LENGTH <= name.length() && name.length() <= MAX_NAME_LENGTH)) {
			Toast.makeText(this,
					getString(R.string.register_name_length_invalid),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		if (password.length() < MIN_PASSWORD_LENGTH) {
			Toast.makeText(this,
					getString(R.string.register_password_too_short),
					Toast.LENGTH_SHORT).show();
			return false;
		}

		String pattern = "^[a-zA-Z0-9]*$";
		if (!name.matches(pattern) || !password.matches(pattern)) {
			Toast.makeText(this, "入力は英数字で行ってください", Toast.LENGTH_SHORT).show();
			return false;
		}

		return true;
	}

	// EditTextを全て初期化
	private void clearLoginInfo() {
		loginName.setText("");
		loginPassword.setText("");
	}
}
