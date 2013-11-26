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

public class PostTitleActivity extends ActionBarActivity {
	private static final String TAG = PostTitleActivity.class.getSimpleName();
	private EditText titleEditText; // タイトル
	private TextView textCount; // 文字数

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_title);

		initViews();
	}

	// viewの初期化
	private void initViews() {
		titleEditText = (EditText) findViewById(R.id.post_title_body_text);
		textCount = (TextView) findViewById(R.id.post_title_text_count);

		// 前画面より質問文を受け取り，セットする
		Intent intent = getIntent();
		titleEditText.setText(intent.getStringExtra(Intent.EXTRA_TEXT + "title"));

		updateTextCount(titleEditText.getText().toString()); // 文字数もセット
		// 文字入力毎に文字数の表示を変えるイベントをつける
		titleEditText.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				updateTextCount(titleEditText.getText().toString());
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
			}
		});
	}

	private void updateTextCount(String text) {
		textCount.setText(text.length() + " characters");
	}

	// OKボタンを推した際の処理
	public void onOkClick(View view) {
		Intent intent = new Intent();
		intent.putExtra(Intent.EXTRA_TEXT + "title", titleEditText.getText().toString());
		Log.d(Intent.EXTRA_TEXT + "title", titleEditText.getText().toString());
		setResult(RESULT_OK, intent);
		finish();
	}

	// 戻るボタンの処理をフック
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.putExtra(Intent.EXTRA_TEXT + "title", titleEditText.getText()
					.toString());
			setResult(RESULT_CANCELED, intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}