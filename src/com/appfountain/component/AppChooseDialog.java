package com.appfountain.component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.appfountain.R;
import com.appfountain.model.App;

public class AppChooseDialog extends DialogFragment {
	private static final String TAG = AppChooseDialog.class.getSimpleName();

	private List<App> applications;
	private AppChooseListener parent;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setCancelable(true);
		int style = DialogFragment.STYLE_NORMAL, theme = 0;
		setStyle(style, theme);

		applications = getInstalledApps();
		parent = (AppChooseListener) getActivity();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle("参考アプリを選択");
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.dialog_choose_app, null);
		ListView appView = (ListView) view
				.findViewById(R.id.dialog_choose_app_list);
		appView.setAdapter(new AppAdapter(this.getActivity(),
				R.layout.list_item_app, applications));
		appView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long id) {
				parent.onChoosed(applications.get(position));
				dismiss();
			}
		});

		builder.setView(view);
		return builder.create();
	}

	private List<App> getInstalledApps() {
		List<App> apps = new ArrayList<App>();
		PackageManager pm = this.getActivity().getPackageManager();
		List<ApplicationInfo> applicationInfo = pm
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo info : applicationInfo) {
			if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM
					|| info.packageName.startsWith("com.example"))
				continue;
			try {
				apps.add(new App(info.loadLabel(pm).toString(),
						info.packageName, pm
								.getApplicationIcon(info.packageName)));
			} catch (NameNotFoundException e) {
			}
		}
		Collections.sort(apps, new Comparator<App>() {
			@Override
			public int compare(App lhs, App rhs) {
				return lhs.getName().compareTo(rhs.getName());
			}
		});
		return apps;
	}
}
