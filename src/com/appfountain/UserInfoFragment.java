package com.appfountain;

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
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/*
 * UserPageActivityで，ユーザ情報を表示するFragment
 */
public class UserInfoFragment extends Fragment {
	private static final String TAG = UserInfoFragment.class.getSimpleName();

	private Fragment self = this;
	private UserContainer userContainer = null;

	private TextView name;
	private TextView created;
	private TextView totalQuestionCount;
	private TextView totalCommentCount;
	private TextView usefulCount;
	private TextView upCount;
	private TextView downCount;
	private Boolean hasLoaded = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		userContainer = Common.getUserContainer(this.getActivity());
		if (userContainer == null)
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
		downCount = (TextView) v
				.findViewById(R.id.fragment_user_info_down_value);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (!hasLoaded)
			loadUserInfo();
	}

	private void loadUserInfo() {
		RequestQueue queue = Volley.newRequestQueue(this.getActivity());

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.GET,
				getUrl(userContainer.getId()), UserSource.class, null, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						if (response.isSuccess()) {
							showUserInfo(response.getUser());
							hasLoaded = true;
						} else {
							Toast.makeText(self.getActivity(),
									response.getMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(self.getActivity(), error.getMessage(),
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
		downCount.setText(user.getDown() + " 回");
	}
}
