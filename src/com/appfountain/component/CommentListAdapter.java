package com.appfountain.component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.Volley;
import com.appfountain.R;
import com.appfountain.UserPageActivity;
import com.appfountain.external.BaseSource;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.QuestionsSource;
import com.appfountain.model.Comment;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentListAdapter.class.getSimpleName();

	private List<Comment> comments;
	private int resource;
	private Context context;
	private RequestQueue queue;

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
			holder.upCount = (TextView) view
					.findViewById(R.id.list_item_comment_up_count);
			holder.personButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_person);
			holder.replyButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_reply);
			holder.upButton = (LinearLayout) view
					.findViewById(R.id.list_item_comment_button_star);
			holder.upImage = (ImageView) view
					.findViewById(R.id.list_item_comment_button_useful_image);

			view.setTag(holder);
		} else {
			holder = (CommentItemHolder) view.getTag();
		}

		final Comment c = comments.get(position);
		holder.userName.setText(c.getUserName());
		holder.created.setText(c.getCreatedString());
		holder.body.setText(c.getBody());
		holder.upCount.setText("" + c.getUp());
		if (c.isUpEvaluation()) {
			holder.upImage.setImageResource(R.drawable.comment_star);
		} else {
			holder.upImage.setImageResource(R.drawable.comment_star_null);
		}

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
				// TODO 返信付きコメント画面への遷移
				Log.d(TAG, "reply button clicked");
			}
		});

		holder.upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UserContainer user = Common.getUserContainer(context);
				// ログイン済みの場合のみ変更可能
				if (user == null) {
					Toast.makeText(context, "ログインして下さい", Toast.LENGTH_SHORT)
							.show();
				} else {
					// 画像の変更, 値の増減
					if (c.isUpEvaluation()) {
						holder.upImage
								.setImageResource(R.drawable.comment_star_null);
						holder.upCount.setText(Integer.parseInt(holder.upCount
								.getText().toString()) - 1 + "");
					} else {
						holder.upImage
								.setImageResource(R.drawable.comment_star);
						holder.upCount.setText(Integer.parseInt(holder.upCount
								.getText().toString()) + 1 + "");
					}
					commentEvaluate(c);
				}
			}
		});

		return view;
	}

	private void commentEvaluate(Comment c) {
		// localの値の更新
		c.evaluate();

		// 変更のrequest投げる
		if (queue == null)
			queue = Volley.newRequestQueue(context);

		Map<String, String> params = new HashMap<String, String>();
		params.put("value", c.isUpEvaluation() ? "up" : "none");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(context),
				Common.getUserContainer(context).getRk());

		GsonRequest<BaseSource> req = new GsonRequest<BaseSource>(Method.POST,
				getCommentEvaluateURL(c), BaseSource.class, params, headers,
				new Listener<BaseSource>() {
					@Override
					public void onResponse(BaseSource response) {
						// do nothing
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(context, error.getMessage(),
								Toast.LENGTH_SHORT).show();
					}
				});
		queue.add(req);
	}

	private String getCommentEvaluateURL(Comment c) {
		return Common.getApiBaseUrl(context) + "comment/" + c.getId()
				+ "/evaluate";
	}

	@Override
	public int getCount() {
		return comments.size();
	}

	static class CommentItemHolder {
		TextView userName;
		TextView created;
		TextView body;
		TextView upCount;
		LinearLayout personButton;
		LinearLayout replyButton;
		LinearLayout upButton;
		ImageView upImage;
	}
}
