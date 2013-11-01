package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.appfountain.R;
import com.appfountain.model.App;
import com.appfountain.util.Common;

public class QuestionAppAdapter extends ArrayAdapter<App> {
	private static final String TAG = QuestionAppAdapter.class.getSimpleName();

	private List<App> apps;
	private int resource;
	private Context context;
	private ImageLoader imageLoader;

	public QuestionAppAdapter(Context context, int resource, List<App> apps,
			ImageLoader imageLoader) {
		super(context, resource);

		this.apps = apps;
		this.resource = resource;
		this.imageLoader = imageLoader;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final QuestionAppItemHolder holder;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new QuestionAppItemHolder();
			holder.name = (TextView) view
					.findViewById(R.id.list_item_question_app_name);
			holder.icon = (NetworkImageView) view
					.findViewById(R.id.list_item_question_app_network_image_view);

			view.setTag(holder);
		} else {
			holder = (QuestionAppItemHolder) view.getTag();
		}

		final App app = apps.get(position);
		holder.name.setText(app.getName());
		holder.icon.setImageUrl(getImageResourceURL(app), imageLoader);

		return view;
	}

	private String getImageResourceURL(App app) {
		return Common.getIconBaseUrl(context) + app.getPackageName() + ".png";
	}

	@Override
	public int getCount() {
		return apps.size();
	}

	static class QuestionAppItemHolder {
		NetworkImageView icon;
		TextView name;
	}
}
