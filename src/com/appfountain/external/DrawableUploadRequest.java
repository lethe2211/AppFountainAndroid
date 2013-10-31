package com.appfountain.external;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;

public class DrawableUploadRequest<T> extends GsonRequest<T> {
	private static final String TAG = DrawableUploadRequest.class
			.getSimpleName();

	private final Drawable image;

	public DrawableUploadRequest(String url, Class<T> clazz, Drawable image,
			Map<String, String> headers, Response.Listener<T> listener,
			Response.ErrorListener errorListener) {
		super(Method.POST, url, clazz, null, headers, listener, errorListener);

		this.image = image;
		Log.w(TAG, "constructor");
		Log.w(TAG, "constructor" + image.toString() + " hogehoge");
	}

	@Override
	public String getBodyContentType() {
		return "image/png";
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		Log.w(TAG, "getBody" + image.toString() + " hogehoge");
		Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}