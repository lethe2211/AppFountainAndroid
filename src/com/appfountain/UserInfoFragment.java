package com.appfountain;

import java.util.HashMap;
import java.util.Map;

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

	private final String url = Common.getApiBaseUrl(this.getActivity())
			+ "user/info";

	private Fragment self = this;
	private UserContainer userContainer = null;

	private TextView name;
	private TextView created;
	private TextView totalQuestionCount;
	private TextView totalCommentCount;
	private TextView usefulCount;
	private TextView upCount;
	private TextView downCount;

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

		loadUserInfo();

		return v;
	}

	private void loadUserInfo() {
		RequestQueue queue = Volley.newRequestQueue(this.getActivity());

		Map<String, String> params = new HashMap<String, String>(1);
		params.put("id", "" + userContainer.getId());

		GsonRequest<UserSource> req = new GsonRequest<UserSource>(Method.GET,
				url, UserSource.class, params, null,
				new Listener<UserSource>() {
					@Override
					public void onResponse(UserSource response) {
						if (response.isSuccess()) {
							showUserInfo(response.getUser());
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

	private void showUserInfo(User user) {
		name.setText(user.getName());
		created.setText(user.getCreatedString());
		// TODO まだ鯖側実装してない
		// totalQuestionCount(user.get)
		// totalComme
		usefulCount.setText("" + user.getUsefulCount());
		upCount.setText("" + user.getUp());
		downCount.setText("" + user.getDown());
	}
}
