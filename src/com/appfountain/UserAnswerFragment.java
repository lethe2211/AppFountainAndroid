package com.appfountain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * UserPageActivityで，ユーザの質問情報を表示するFragment
 */
public class UserAnswerFragment extends Fragment {
	private static final String TAG = UserAnswerFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater
				.inflate(R.layout.fragment_user_answer, container, false);
	}

}
