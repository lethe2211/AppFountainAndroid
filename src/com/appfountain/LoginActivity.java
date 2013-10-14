package com.appfountain;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
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

		initViews();

		queue = Volley.newRequestQueue(this);
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
									Toast.LENGTH_LONG).show();
							return;
						}
						loginButtonClicked(loginName.getText().toString(),
								loginPassword.getText().toString());
					}
				});
	}

	private void loginButtonClicked(final String name, final String password) {
		if (!isValidInput(name, password))
			return;

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("password", Common.md5Hex(password));

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.POST,
				url, UserSource.class, params, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						Common.closeProgressBar();
						if (response.isSuccess()) {
							// User情報を端末へ登録&キャッシュ
							User user = response.getUser();
							Common.registerUser(self, name, password,
									user.getRk());
							Common.setUser(user);

							// TopPageへの遷移
							Intent intent = new Intent(self,
									TopPageActivity.class);
							startActivity(intent);
							self.finish(); // いらない画面は終了させる
						} else {
							Toast.makeText(self, response.getMessage(),
									Toast.LENGTH_SHORT).show();
							clearLoginInfo();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Common.closeProgressBar();
						Toast.makeText(self, error.getMessage(),
								Toast.LENGTH_SHORT).show();
						clearLoginInfo();
					}
				});

		queue.add(req);
		Common.initializeProgressBar(this, "login...");
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

		return true;
	}

	// EditTextを全て初期化
	private void clearLoginInfo() {
		loginName.setText("");
		loginPassword.setText("");
	}
}
