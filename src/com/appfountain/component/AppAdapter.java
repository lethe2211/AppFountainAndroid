package com.appfountain.component;

import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.appfountain.R;
import com.appfountain.model.App;

public class AppAdapter extends ArrayAdapter<App> {
	private static final String TAG = AppAdapter.class.getSimpleName();

	private List<App> applications;
	private int resource;

	public AppAdapter(Context context, int resource, List<App> applications) {
		super(context, resource);

		this.applications = applications;
		this.resource = resource;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		AppItemHolder holder = null;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new AppItemHolder();
			holder.name = (TextView) view.findViewById(R.id.list_item_app_name);
			holder.icon = (ImageView) view
					.findViewById(R.id.list_item_app_icon);

			view.setTag(holder);
		} else {
			holder = (AppItemHolder) view.getTag();
		}

		App app = applications.get(position);
		holder.name.setText(app.getName());
		holder.icon.setImageDrawable(app.getIcon());

		return view;
	}

	@Override
	public int getCount() {
		return applications.size();
	}

	static class AppItemHolder {
		ImageView icon;
		TextView name;
	}
}
