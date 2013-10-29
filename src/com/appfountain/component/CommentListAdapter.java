package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appfountain.R;
import com.appfountain.UserPageActivity;
import com.appfountain.model.Comment;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentListAdapter.class.getSimpleName();

	private List<Comment> comments;
	private int resource;
	private Context context;

	public CommentListAdapter(Context context, int resource,
			List<Comment> comments) {
		super(context, resource);

		this.context = context;
		this.comments = comments;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final CommentItemHolder holder;

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
			holder.personButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_person);
			holder.replyButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_reply);
			holder.usefulButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_star);

			view.setTag(holder);
		} else {
			holder = (CommentItemHolder) view.getTag();
		}

		final Comment c = comments.get(position);
		holder.userName.setText(c.getUserName());
		holder.created.setText(c.getCreatedString());
		holder.body.setText(c.getBody());
		holder.usefulCount.setText("" + c.getUseful());

		holder.personButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// コメントしたユーザ情報表示
				Intent intent = new Intent(context, UserPageActivity.class);
				intent.putExtra(Intent.EXTRA_UID, c.getUserId());
				context.startActivity(intent);
			}
		});

		holder.replyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO implement this
				Log.d(TAG, "reply button clicked");
			}
		});

		holder.usefulButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO implement this
				Log.d(TAG, "useful button clicked");
			}
		});

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
		LinearLayout personButton;
		LinearLayout replyButton;
		LinearLayout usefulButton;
	}
}
