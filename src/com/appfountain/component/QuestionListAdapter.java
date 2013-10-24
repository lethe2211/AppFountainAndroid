package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appfountain.R;
import com.appfountain.model.Question;

public class QuestionListAdapter extends ArrayAdapter<Question> {
	private static final String TAG = QuestionListAdapter.class.getSimpleName();
	private static final int MAX_TITLE_SIZE = 30;
	private static final int MAX_SNIPPET_SIZE = 50;

	private List<Question> questions;
	private int resource;

	public QuestionListAdapter(Context context, int resource,
			List<Question> questions) {
		super(context, resource);

		this.questions = questions;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		QuestionItemHolder holder = null;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new QuestionItemHolder();
			holder.title = (TextView) view
					.findViewById(R.id.list_item_question_title);
			holder.snippet = (TextView) view
					.findViewById(R.id.list_item_question_snippet);
			holder.created = (TextView) view
					.findViewById(R.id.list_item_question_created);

			view.setTag(holder);
		} else {
			holder = (QuestionItemHolder) view.getTag();
		}

		Question q = questions.get(position);
		holder.title.setText(q.getTitle(MAX_TITLE_SIZE));
		holder.snippet.setText(q.getBody(MAX_SNIPPET_SIZE));

		return view;
	}

	@Override
	public int getCount() {
		return questions.size();
	}

	static class QuestionItemHolder {
		TextView title;
		TextView snippet;
		TextView created;
	}
}