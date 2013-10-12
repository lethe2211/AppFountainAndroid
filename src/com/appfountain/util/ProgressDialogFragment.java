package com.appfountain.util;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class ProgressDialogFragment extends DialogFragment {
	 
	  private static final String KEY_PDIAG_MSG = "pdiag_msg";
	 
		public static ProgressDialogFragment newInstance(String msg) {
		  ProgressDialogFragment fragment = new ProgressDialogFragment();
			Bundle args = new Bundle();
			args.putString(KEY_PDIAG_MSG, msg);
			fragment.setArguments(args);
			return fragment;
		}
	 
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			ProgressDialog pdiag = new ProgressDialog(getActivity());
			pdiag.setMessage(getArguments().getString(KEY_PDIAG_MSG));
			pdiag.setIndeterminate(true);
			pdiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pdiag.setCancelable(false);
			return pdiag;
		}
	}