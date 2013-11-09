package com.appfountain;

import java.io.UnsupportedEncodingException;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.UserCommentsSource;
import com.appfountain.model.Comment;
import com.appfountain.model.Question;
import com.appfountain.model.UserComment;
import com.appfountain.util.Common;

/*
 * UserPageActivityで，ユーザの質問情報を表示するFragment
 */
public class UserAnswerFragment extends Fragment {
	private static final String TAG = UserAnswerFragment.class.getSimpleName();

	private static final int FETCH_COUNT = 20;

	private int userId = -1;
	private Fragment self = this;
	private LinearLayout commentList;
	private int userCommentCount = 0;
	private List<UserComment> _userComments;
	private Boolean finished = false;

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
		View v = inflater.inflate(R.layout.fragment_user_answer, container,
				false);
		commentList = (LinearLayout) v
				.findViewById(R.id.fragment_user_answer_list_linear);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (_userComments == null) {
			userCommentCount = 0;
			fetchUserComment(0);
		} else {
			commentList.removeAllViews();
			addUserComment(_userComments);
		}
	}

	private void addUserComment(List<UserComment> moreUserComments) {
		for (UserComment uc : moreUserComments) {
			Question question = uc.getQuestion();
			List<Comment> comments = uc.getComments();
			addQuestionLayout(question);
			for (Comment comment : comments) {
				addCommentLayout(question, comment);
			}
		}
	}

	private void addQuestionLayout(Question question) {
		LayoutInflater myinflater = (LayoutInflater) getActivity()
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout questionLayout = (LinearLayout) myinflater.inflate(
				R.layout.list_item_question, null);
		setQuestionLayout(question, questionLayout);

		commentList.addView(questionLayout);
	}

	private void setQuestionLayout(Question question,
			LinearLayout questionLayout) {
		((TextView) questionLayout.findViewById(R.id.list_item_question_title))
				.setText(question.getTitle());
		if (question.isFinished())
			((TextView) questionLayout
					.findViewById(R.id.list_item_question_comment_done))
					.setText("解決済み");
		else
			((TextView) questionLayout
					.findViewById(R.id.list_item_question_comment_done))
					.setText("未解決");
		((ImageView) questionLayout
				.findViewById(R.id.list_item_question_category))
				.setImageResource(question.getCategory().getDrawableId());
		((TextView) questionLayout
				.findViewById(R.id.list_item_question_created))
				.setText(question.getCreatedString());
	}

	private void addCommentLayout(Question quesiton, Comment comment) {
		LayoutInflater myinflater = (LayoutInflater) getActivity()
				.getApplicationContext().getSystemService(
						Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout commentLayout = (LinearLayout) myinflater.inflate(
				R.layout.list_item_comment, null);
		setCommentLayout(comment, commentLayout);

		commentList.addView(commentLayout);
	}

	private void setCommentLayout(Comment comment, LinearLayout commentLayout) {
		((TextView) commentLayout
				.findViewById(R.id.list_item_comment_user_name))
				.setText(comment.getUserName());
		((TextView) commentLayout
				.findViewById(R.id.list_item_comment_created))
				.setText(comment.getCreatedString());
		((TextView) commentLayout
				.findViewById(R.id.list_item_comment_body))
				.setText(comment.getBody(20));
	}

	private void fetchUserComment(int next) {
		RequestQueue queue = Volley.newRequestQueue(this.getActivity());

		Log.d(TAG, getUrl() + "?count=" + FETCH_COUNT + "&next=" + 0);
		GsonRequest<UserCommentsSource> req = new GsonRequest<UserCommentsSource>(
				Method.GET, getUrl() + "?count=" + FETCH_COUNT + "&next="
						+ next, UserCommentsSource.class, null, null,
				new Listener<UserCommentsSource>() {
					@Override
					public void onResponse(UserCommentsSource response) {
						_userComments = response.getUserComments();
						addUserComment(_userComments);
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

	/**
	 * userのコメントした質問取得APIのURL
	 * 
	 * @param user
	 * @return
	 */
	private String getUrl() {
		return Common.getApiBaseUrl(this.getActivity()) + "user/" + userId
				+ "/questioncomments";
	}
}
