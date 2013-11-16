package com.appfountain.component;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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
import com.appfountain.QuestionDetailActivity;
import com.appfountain.R;
import com.appfountain.UserPageActivity;
import com.appfountain.external.GsonRequest;
import com.appfountain.external.SimpleSource;
import com.appfountain.model.Comment;
import com.appfountain.model.Question;
import com.appfountain.model.UserContainer;
import com.appfountain.util.Common;

public class CommentListAdapter extends ArrayAdapter<Comment> {
	private static final String TAG = CommentListAdapter.class.getSimpleName();

	private List<Comment> comments;
	private QuestionDetailActivity parent;
	private RequestQueue queue;
	private Question question;
	private Boolean isQuestionAuthor;

	public CommentListAdapter(QuestionDetailActivity parent, int resource,
			List<Comment> comments, Question question, Boolean isQuestionAuthor) {
		super(parent, resource);

		this.parent = parent;
		this.comments = comments;
		this.question = question;
		this.isQuestionAuthor = isQuestionAuthor;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		Comment comment = comments.get(position);
		if (comment.isReply())
			return 1;
		else
			return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup vg) {
		View view = convertView;
		final CommentItemHolder holder;

		final Comment c = comments.get(position);

		if (view == null) {
			holder = new CommentItemHolder();
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (c.isReply()) {
				view = inflater.inflate(R.layout.list_item_comment_reply, vg,
						false);
				holder.userName = (TextView) view
						.findViewById(R.id.list_item_comment_reply_user_name);
				holder.created = (TextView) view
						.findViewById(R.id.list_item_comment_reply_created);
				holder.body = (TextView) view
						.findViewById(R.id.list_item_comment_reply_body);
				holder.upCount = (TextView) view
						.findViewById(R.id.list_item_comment_reply_up_count);
				holder.personButton = (LinearLayout) view
						.findViewById(R.id.list_item_comment_reply_button_person);
				holder.replyButton = (LinearLayout) view
						.findViewById(R.id.list_item_comment_reply_button_reply);
				holder.upButton = (LinearLayout) view
						.findViewById(R.id.list_item_comment_reply_button_star);
				holder.upImage = (ImageView) view
						.findViewById(R.id.list_item_comment_reply_button_useful_image);
				holder.upText = (TextView) view
						.findViewById(R.id.list_item_comment_button_useful_text);
				holder.usefulContainer = (LinearLayout) view
						.findViewById(R.id.question_detail_useful_comment_container);
				holder.referUserName = (TextView) view
						.findViewById(R.id.list_item_comment_reply_refer_user_name);
			} else {
				view = inflater.inflate(R.layout.list_item_comment, vg, false);
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
				holder.upText = (TextView) view
						.findViewById(R.id.list_item_comment_button_useful_text);
				holder.usefulContainer = (LinearLayout) view
						.findViewById(R.id.question_detail_useful_comment_container);
			}
			view.setTag(holder);
		} else {
			holder = (CommentItemHolder) view.getTag();
		}

		holder.userName.setText(c.getUserName());
		holder.created.setText(c.getCreatedString());
		holder.body.setText(c.getBody());
		holder.upCount.setText("" + c.getUp());
		if (isQuestionAuthor) {
			holder.upText.setText("Useful");
			if (c.isUseful()) {
				holder.upImage
						.setImageResource(R.drawable.question_detail_comment_useful);
			} else {
				holder.upImage
						.setImageResource(R.drawable.question_detail_comment_unuseful);
			}
		} else {
			holder.upText.setText("Star");
			if (c.isUpEvaluation()) {
				holder.upImage.setImageResource(R.drawable.comment_star);
			} else {
				holder.upImage.setImageResource(R.drawable.comment_star_null);
			}
		}

		if (c.isUseful()) {
			holder.usefulContainer.setVisibility(View.VISIBLE);
		} else {
			holder.usefulContainer.setVisibility(View.GONE);
		}

		if (c.isReply()) {
			holder.referUserName.setText(">> " + c.getReferCommentUserName()
					+ " への返信");
		}

		holder.personButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// コメントしたユーザ情報表示
				Intent intent = new Intent(parent, UserPageActivity.class);
				intent.putExtra(Intent.EXTRA_UID, c.getUserId());
				parent.startActivity(intent);
			}
		});

		holder.replyButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				parent.moveReplyComment(c);
			}
		});

		holder.upButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				UserContainer user = Common.getUserContainer(parent);
				// ログイン済みの場合のみ変更可能
				if (user == null) {
					Toast.makeText(parent, "ログインして下さい", Toast.LENGTH_SHORT)
							.show();
				} else {
					if (isQuestionAuthor) {
						usefulEvaluate(c, holder);
					} else {
						commentEvaluate(c, holder);
					}
				}
			}
		});

		return view;
	}

	// 投稿者以外の評価
	private void commentEvaluate(final Comment c, final CommentItemHolder holder) {
		// localの値の更新
		c.evaluate();

		// 変更のrequest投げる
		if (queue == null)
			queue = Volley.newRequestQueue(parent);

		Map<String, String> params = new HashMap<String, String>();
		params.put("value", c.isUpEvaluation() ? "up" : "none");

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(parent),
				Common.getUserContainer(parent).getRk());

		GsonRequest<SimpleSource> req = new GsonRequest<SimpleSource>(
				Method.POST, getCommentEvaluateURL(c), SimpleSource.class,
				params, headers, new Listener<SimpleSource>() {
					@Override
					public void onResponse(SimpleSource response) {
						// 画像の変更, 値の増減
						if (c.isUpEvaluation()) {
							holder.upImage
									.setImageResource(R.drawable.comment_star);
							holder.upCount.setText(c.incrementUp() + "");
						} else {
							holder.upImage
									.setImageResource(R.drawable.comment_star_null);
							holder.upCount.setText(c.decrementUp() + "");
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String responseBody = new String(
									error.networkResponse.data, "utf-8");
							Toast.makeText(parent, responseBody,
									Toast.LENGTH_SHORT).show();
						} catch (UnsupportedEncodingException e) {
						}
					}
				});
		queue.add(req);
	}

	private String getCommentEvaluateURL(Comment c) {
		return Common.getApiBaseUrl(parent) + "comment/" + c.getId()
				+ "/evaluate";
	}

	// 投稿者の評価
	private void usefulEvaluate(final Comment c, final CommentItemHolder holder) {
		// localの値の更新
		c.usefulEvaluate();

		// 変更のrequest投げる
		if (queue == null)
			queue = Volley.newRequestQueue(parent);

		Map<String, String> params = new HashMap<String, String>();
		params.put("useful", c.isUseful() ? "true" : "false");
		params.put("question_id", "" + question.getId());
		params.put("comment_author", c.getUserName());

		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(parent),
				Common.getUserContainer(parent).getRk());

		GsonRequest<SimpleSource> req = new GsonRequest<SimpleSource>(
				Method.POST, getUsefulEvaluateURL(c), SimpleSource.class,
				params, headers, new Listener<SimpleSource>() {
					@Override
					public void onResponse(SimpleSource response) {
						// 画像の変更, 値の増減
						if (c.isUseful()) {
							holder.upImage
									.setImageResource(R.drawable.question_detail_comment_useful);
						} else {
							holder.upImage
									.setImageResource(R.drawable.question_detail_comment_unuseful);
						}
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String responseBody = new String(
									error.networkResponse.data, "utf-8");
							Toast.makeText(parent, responseBody,
									Toast.LENGTH_SHORT).show();
						} catch (UnsupportedEncodingException e) {
						}
					}
				});
		queue.add(req);

		// Usefulの変化により質問が解決済み/未解決かを送信する
		if (c.isUseful())
			sendFinishQuestion(c, true);
		else if (!finishQuestion(comments)) {
			sendFinishQuestion(c, false);
		}
	}

	private String getUsefulEvaluateURL(Comment c) {
		return Common.getApiBaseUrl(parent) + "comment/" + c.getId()
				+ "/useful";
	}

	// Usefulなコメントが存在するか
	private boolean finishQuestion(List<Comment> comments) {
		for (Comment c : comments) {
			if (c.isUseful())
				return true;
		}
		return false;
	}

	// 質問が解決済み/未解決か送信
	private void sendFinishQuestion(Comment c, boolean isFinished) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.getPostHeader(parent),
				Common.getUserContainer(parent).getRk());

		GsonRequest<SimpleSource> req = new GsonRequest<SimpleSource>(
				Method.POST, getFinishQuestionURL(isFinished),
				SimpleSource.class, null, headers,
				new Listener<SimpleSource>() {
					@Override
					public void onResponse(SimpleSource response) {
						// do nothing
					}
				}, new ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						try {
							String responseBody = new String(
									error.networkResponse.data, "utf-8");
							Toast.makeText(parent, responseBody,
									Toast.LENGTH_SHORT).show();
						} catch (UnsupportedEncodingException e) {
						}
					}
				});
		queue.add(req);
	}

	private String getFinishQuestionURL(boolean isFinished) {
		String url = Common.getApiBaseUrl(parent) + "question/"
				+ question.getId();
		return isFinished ? url + "/finish" : url + "/unfinish";
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
		TextView upText;
		LinearLayout usefulContainer;
		TextView referUserName;
	}
}
