package com.appfountain;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UserSource;
import com.appfountain.model.User;
import com.appfountain.util.Common;

/*
 * UserPageActivityで，ユーザ情報を表示するFragment
 */
public class UserRankingFragment extends Fragment {
	private static final String TAG = UserRankingFragment.class.getSimpleName();

	private Fragment self = this;
	private int userId = -1;
	private User user = null;

	private TextView name;
	private TextView created;
	private TextView totalQuestionCount;
	private TextView totalCommentCount;
	private TextView usefulCount;
	private TextView upCount;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userId = getArguments().getInt(Intent.EXTRA_UID);
		if (userId < 0)
			this.getActivity().finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater
				.inflate(R.layout.fragment_user_info, container, false);

		name = (TextView) v.findViewById(R.id.fragment_user_info_name_value);
		created = (TextView) v
				.findViewById(R.id.fragment_user_info_created_value);
		totalQuestionCount = (TextView) v
				.findViewById(R.id.fragment_user_info_total_question_value);
		totalCommentCount = (TextView) v
				.findViewById(R.id.fragment_user_info_total_comment_value);
		usefulCount = (TextView) v
				.findViewById(R.id.fragment_user_info_useful_comment_value);
		upCount = (TextView) v.findViewById(R.id.fragment_user_info_up_value);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (userId < 0)
			this.getActivity().finish();

		if (user == null)
			loadUserInfo(userId);
		else
			showUserInfo(user);
	}

	private void loadUserInfo(int userId) {
		if (!Common.isInternetAvailable(self.getActivity())) {
			Toast.makeText(
					self.getActivity(),
					getString(R.string.common_internet_unavailable),
					Toast.LENGTH_SHORT).show();
			return;
		}
		RequestQueue queue = Volley.newRequestQueue(this.getActivity());

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.GET,
				getUrl(userId), UserSource.class, null, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						user = response.getUser();
						showUserInfo(user);
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						String responseBody = null;
						try {
							responseBody = new String(
									error.networkResponse.data, "utf-8");
						} catch (Exception e) {
							e.printStackTrace();
						}
						Toast.makeText(self.getActivity(), responseBody,
								Toast.LENGTH_SHORT).show();
					}
				});
		queue.add(req);
	}

	private String getUrl(int id) {
		return Common.getApiBaseUrl(this.getActivity()) + "user/info?id=" + id;
	}

	private void showUserInfo(User user) {
		name.setText(user.getName());
		created.setText(user.getCreatedString());
		totalQuestionCount.setText(user.getQuestionCount() + " 件");
		totalCommentCount.setText(user.getCommentCount() + " 件");
		usefulCount.setText(user.getUsefulCount() + " 回");
		upCount.setText(user.getUp() + " 回");
	}
}
