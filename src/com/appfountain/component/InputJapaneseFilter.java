package com.appfountain.component;

import android.text.InputFilter;
import android.text.Spanned;

public class InputJapaneseFilter implements InputFilter {
	@Override
	public CharSequence filter(CharSequence source, int start, int end,
			Spanned dest, int dstart, int dend) {
		if (source.toString().matches("^[a-zA-Z0-9@¥.¥_¥¥-]+$")) {
			return source;
		} else {
			return "";
		}
	}
}
