package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class PostBodyActivity extends ActionBarActivity {
	private static final String TAG = PostBodyActivity.class.getSimpleName();
	private EditText bodyEditText; // 質問詳細
	private TextView textCount; // 文字数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_body);

		initViews();
	}

	// viewの初期化
	private void initViews() {
		bodyEditText = (EditText) findViewById(R.id.post_body_body_text);
		textCount = (TextView) findViewById(R.id.post_body_text_count);

		// 前画面より質問文を受け取り，セットする
		Intent intent = getIntent();
		bodyEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT + "body"));

		updateTextCount(bodyEditText.getText().toString()); // 文字数もセット
		// 文字入力毎に文字数の表示を変えるイベントをつける
		bodyEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateTextCount(bodyEditText.getText().toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void updateTextCount(String text) {
		textCount.setText(text.length() + " words");
	}

	// OKボタンを推した際の処理
	public void onOkClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT + "body", bodyEditText.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	// 戻るボタンの処理をフック
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_TEXT + "body", bodyEditText.getText()
					.toString());
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
