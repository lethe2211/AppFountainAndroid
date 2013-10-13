package com.appfountain;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/*
 * UserPageActivityで，ユーザ情報を表示するFragment
 */
public class UserInfoFragment extends Fragment {
	private static final String TAG = UserInfoFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_user_info, container, false);
	}

}
