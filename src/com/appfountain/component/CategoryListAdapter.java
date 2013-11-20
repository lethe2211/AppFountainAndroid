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
import com.appfountain.model.Category;

public class CategoryListAdapter extends ArrayAdapter<Category> {
	private static final String TAG = QuestionListAdapter.class.getSimpleName();

	private List<Category> categories;
	int resource;

	public CategoryListAdapter(Context context, int resource,
			List<Category> categories) {
		super(context, resource);

		this.categories = categories;
		this.resource = resource;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		CategoryItemHolder holder = null;

		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(resource, parent, false);
			holder = new CategoryItemHolder();
			holder.name = (TextView) view
					.findViewById(R.id.list_item_category_name);
			holder.icon = (ImageView) view
					.findViewById(R.id.list_item_category_icon);

			view.setTag(holder);
		} else {
			holder = (CategoryItemHolder) view.getTag();
		}

		Category c = categories.get(position);
		holder.name.setText(c.getName());
		holder.icon.setImageResource(c.getDrawableId());

		return view;
	}

	@Override
	public int getCount() {
		return categories.size();
	}

	static class CategoryItemHolder {
		TextView name;
		ImageView icon;
	}

}
