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

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.Listener;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UserSource;
import com.appfountain.model.User;
import com.appfountain.util.Common;

/*
 * ユーザの新規登録のための画面
 */
public class RegisterActivity extends ActionBarActivity {
	private static final String url = "";

	private ActionBarActivity self = this;
	private RequestQueue queue;

	private EditText loginRegisterName;
	private EditText loginRegisterPassword;
	private EditText loginRegisterPasswordConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initViews();

		queue = Volley.newRequestQueue(this);
	}

	private void initViews() {
		// 登録系のinitialize
		loginRegisterName = (EditText) findViewById(R.id.login_register_name);
		loginRegisterPassword = (EditText) findViewById(R.id.login_register_password);
		loginRegisterPasswordConfirm = (EditText) findViewById(R.id.login_register_password_confirm);

		findViewById(R.id.login_register_button).setOnClickListener(
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
						registerButtonClicked(loginRegisterName.getText()
								.toString(), loginRegisterPassword.getText()
								.toString(), loginRegisterPasswordConfirm
								.getText().toString());
					}
				});
	}

	/**
	 * volleyつかって通信する
	 */
	private void registerButtonClicked(final String name,
			final String password, final String passwordConfirm) {
		if (password.equals(passwordConfirm)) {
			Toast.makeText(this, getString(R.string.register_password_invalid),
					Toast.LENGTH_SHORT).show();
			return;
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("name", name);
		params.put("password", password);

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.POST,
				url, UserSource.class, params, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						if (response.getStatus()) {
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
							clearRegisterInfo();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(self, error.getMessage(),
								Toast.LENGTH_SHORT).show();
						clearRegisterInfo();
					}
				});

		queue.add(req);
	}

	private void clearRegisterInfo() {
		loginRegisterName.setText("");
		loginRegisterPassword.setText("");
		loginRegisterPasswordConfirm.setText("");
	}
}
