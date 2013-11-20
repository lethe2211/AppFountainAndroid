package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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
			holder.finished = (ImageView) view
					.findViewById(R.id.list_item_question_done);
			holder.category = (ImageView) view
					.findViewById(R.id.list_item_question_category);
			holder.commentCount = (TextView) view
					.findViewById(R.id.list_item_question_comment_count_value);
			holder.created = (TextView) view
					.findViewById(R.id.list_item_question_created);

			view.setTag(holder);
		} else {
			holder = (QuestionItemHolder) view.getTag();
		}

		Question q = questions.get(position);
		holder.title.setText(q.getTitle(MAX_TITLE_SIZE));
		holder.category.setImageResource(q.getCategory().getDrawableId());
		holder.commentCount.setText(q.getCommentCount() + "");
		holder.created.setText(q.getCreatedString());
		if (q.isFinished())
			holder.finished
					.setImageResource(R.drawable.top_page_question_sloved);
		else
			holder.finished
					.setImageResource(R.drawable.top_page_question_unsloved);

		return view;
	}

	@Override
	public int getCount() {
		return questions.size();
	}

	static class QuestionItemHolder {
		TextView title;
		ImageView category;
		TextView commentCount;
		TextView created;
		ImageView finished;
	}
}
