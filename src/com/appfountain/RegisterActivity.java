package com.appfountain;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.appfountain.util.Common;

/*
 * ユーザの新規登録のための画面
 */
public class RegisterActivity extends ActionBarActivity {
	private ActionBarActivity self = this;
	private EditText loginRegisterName;
	private EditText loginRegisterPassword;
	private EditText loginRegisterPasswordConfirm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		initViews();
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

	private void registerButtonClicked(String name, String password,
			String passwordConfirm) {
		// TODO Auto-generated method stub

	}
}
