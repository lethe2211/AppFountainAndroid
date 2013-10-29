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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appfountain.R;
import com.appfountain.UserPageActivity;
import com.appfountain.model.Comment;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentListAdapter.class.getSimpleName();
	private static final int UNUSEFUL = 0;
	private static final int USEFUL = 1;

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
			holder.usefulImage = (ImageView) view
					.findViewById(R.id.list_item_comment_button_useful_image);
			holder.usefulImage.setTag(UNUSEFUL);

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
				// TODO implement this 画像の変更, 値の増減 + 通信
				Log.d(TAG, "useful button clicked");

				// 画像の変更, 値の増減
				if ((Integer) holder.usefulImage.getTag() == UNUSEFUL) {
					holder.usefulImage
							.setImageResource(R.drawable.comment_star);
					holder.usefulImage.setTag(USEFUL);
					holder.usefulCount.setText(Integer
							.parseInt(holder.usefulCount.getText().toString())
							+ 1 + "");
				} else {
					holder.usefulImage
							.setImageResource(R.drawable.comment_star_null);
					holder.usefulImage.setTag(UNUSEFUL);
					holder.usefulCount.setText(Integer
							.parseInt(holder.usefulCount.getText().toString())
							- 1 + "");
				}
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
		ImageView usefulImage;
	}
}
