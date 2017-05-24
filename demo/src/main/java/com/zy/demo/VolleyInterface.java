package com.zy.demo;
import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import android.content.Context;

/**
 * Created by zy on 16-12-15.
 */

public abstract class VolleyInterface {
    private  Context mContext;
    public static Listener<String> mListener;
    public static ErrorListener mErrorListener;

    public VolleyInterface(Context context) {
        this.mContext = context;
        this.mListener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                onMySuccess(response);
            }
        };
        this.mErrorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onMyError(error);
            }
        };
    }

    public abstract void onMySuccess(String result);
    public abstract void onMyError(VolleyError error);

    public Listener<String> loadingListener() {
        return mListener;
    }

    public ErrorListener errorListener() {
        return mErrorListener;

    }
}
