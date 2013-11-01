package com.appfountain.external;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

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
	}

	@Override
	public String getBodyContentType() {
		return "image/png";
	}

	@Override
	public byte[] getBody() throws AuthFailureError {
		Bitmap bitmap = ((BitmapDrawable) image).getBitmap();
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
	}
}