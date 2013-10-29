package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.appfountain.R;
import com.appfountain.model.Comment;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentListAdapter.class.getSimpleName();

	private List<Comment> comments;
	private int resource;

	public CommentListAdapter(Context context, int resource,
			List<Comment> comments) {
		super(context, resource);

		this.comments = comments;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		CommentItemHolder holder = null;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new CommentItemHolder();
			holder.userName = (TextView) view
					.findViewById(R.id.list_item_comment_user_name);
			holder.created = (TextView) view
					.findViewById(R.id.list_item_comment_created);
			holder.body = (TextView) view
					.findViewById(R.id.list_item_comment_body);
			holder.usefulCount = (TextView) view
					.findViewById(R.id.list_item_comment_useful_count);

			view.setTag(holder);
		} else {
			holder = (CommentItemHolder) view.getTag();
		}

		Comment c = comments.get(position);
		holder.userName.setText(c.getUserName());
		holder.created.setText(c.getCreatedString());
		holder.body.setText(c.getBody());
		holder.usefulCount.setText("" + c.getUseful());

		return view;
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	static class CommentItemHolder {
		TextView userName;
		TextView created;
		TextView body;
		TextView usefulCount;
	}
}