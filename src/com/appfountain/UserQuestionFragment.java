package com.appfountain;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.appfountain.component.EndlessScrollFragment;
import com.appfountain.component.QuestionListAdapter;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Question;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

/*
 * UserPageActivityで，ユーザの質問情報を表示するFragment
 */
public class UserQuestionFragment extends EndlessScrollFragment {
	private static final String TAG = UserQuestionFragment.class
			.getSimpleName();

	private UserContainer userContainer = null;
	private Fragment self = this;
	private ListView questionListView;
	private boolean inError = false;
	private List<Question> questions = new ArrayList<Question>();
	private QuestionListAdapter questionListAdapter;

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
		View v = inflater.inflate(R.layout.fragment_user_question, container,
				false);

		questionListView = (ListView) v
				.findViewById(R.id.fragment_user_question_list);
		questionListAdapter = new QuestionListAdapter(this.getActivity(),
				R.layout.list_item_question, questions);
		questionListView.setAdapter(questionListAdapter);
		questionListView.setOnScrollListener(this);

		return v;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (hasNext() && !inError) {
			loadPage();
		}
	}

	@Override
	protected void loadPage() {
		RequestQueue queue = Volley.newRequestQueue(this.getActivity());

		int next = questions.size();
		GsonRequest<QuestionsSource> req = new GsonRequest<QuestionsSource>(
				Method.GET, getUrl(userContainer.getId()) + "?count=20&next="
						+ next, QuestionsSource.class, null, null,
				new Listener<QuestionsSource>() {
					@Override
					public void onResponse(QuestionsSource response) {
						if (response.isSuccess()) {
							if (response.getQuestions().isEmpty())
								finishLoading();
							questions.addAll(response.getQuestions());
							questionListAdapter.notifyDataSetChanged();
						} else {
							Toast.makeText(self.getActivity(),
									response.getMessage(), Toast.LENGTH_SHORT)
									.show();
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						showErrorMessage();
					}
				});
		queue.add(req);
	}

	private void showErrorMessage() {
		inError = true;

		Toast.makeText(this.getActivity(), "Error occurd\nSomething wrong...",
				Toast.LENGTH_SHORT).show();
	}

	/**
	 * userの質問取得APIのURL
	 * 
	 * @param userId
	 * @return
	 */
	private String getUrl(int userId) {
		return Common.getApiBaseUrl(this.getActivity()) + "user/" + userId
				+ "/question";
	}
}
