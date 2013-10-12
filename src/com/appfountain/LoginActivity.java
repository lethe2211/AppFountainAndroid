package com.appfountain;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.appfountain.util.Common;

/**
 * ログインページのための画面
 */
public class LoginActivity extends ActionBarActivity {
	private ActionBarActivity self = this;
	private EditText loginLoginName;
	private EditText loginLoginPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		initViews();
	}

	private void initViews() {
		// ログイン系のinitialize
		loginLoginName = (EditText) findViewById(R.id.login_login_name);
		loginLoginPassword = (EditText) findViewById(R.id.login_login_password);

		// ボタンのinitialize
		findViewById(R.id.login_login_button).setOnClickListener(
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
						loginButtonClicked(loginLoginName.getText().toString(),
								loginLoginPassword.getText().toString());
					}
				});
	}

	private void loginButtonClicked(String name, String password) {
		// TODO Auto-generated method stub

	}
}
