package com.zy.demo;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zy on 16-12-15.
 */
public class MultipartRequest extends Request<String> {

    private MultipartEntity entity = new MultipartEntity();
    private VolleyInterface listener;
    private List<File> mFileParts;
    private String mFilePartName;
    private Map<String, String> mParams;

    /**
     * 单个文件上传
     *
     * @param url          地址
     * @param params       参数
     * @param listener     监听
     */
    public MultipartRequest(String url, Map<String, String> params, VolleyInterface listener) {
        super(Method.POST, url, listener.errorListener());

        mFileParts = new ArrayList<File>();
        this.listener = listener;
        this.mParams = params;
        buildMultipartEntity();
    }

    /**
     * 单个文件上传
     *
     * @param url          地址
     * @param filePartName 文件夹名称
     * @param file         文件
     * @param params       参数
     * @param listener     监听
     */
    public MultipartRequest(String url, String filePartName, File file,
                            Map<String, String> params, VolleyInterface listener) {
        super(Method.POST, url, listener.errorListener());

        mFileParts = new ArrayList<File>();
        if (file != null) {
            mFileParts.add(file);
        }
        this.mFilePartName = filePartName;
        this.listener = listener;
        this.mParams = params;
        buildMultipartEntity();
    }

    /**
     * 多个文件，对应一个key
     *
     * @param url          地址
     * @param filePartName 文件夹名称
     * @param files         文件
     * @param params       参数
     * @param listener     监听
     */
    public MultipartRequest(String url, String filePartName, List<File> files,
                            Map<String, String> params, VolleyInterface listener) {
        super(Method.POST, url, listener.errorListener());
        this.mFilePartName = filePartName;
        this.listener = listener;
        this.mFileParts = files;
        this.mParams = params;
        System.out.println(filePartName + "-" + files + "-" + params);
        buildMultipartEntity();
    }

    private void buildMultipartEntity() {
        if (mFileParts != null && mFileParts.size() > 0) {
            for (File file : mFileParts) {
                entity.addPart(mFilePartName, new FileBody(file));
            }
        }

        try {
            if (mParams != null && mParams.size() > 0) {
                for (Map.Entry<String, String> entry : mParams.entrySet()) {
                    entity.addPart(entry.getKey(), new StringBody(entry.getValue(), Charset.forName("UTF-8")));
                }
            }
        } catch (UnsupportedEncodingException e) {
            VolleyLog.e("UnsupportedEncodingException");
        }
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        return bos.toByteArray();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        if (VolleyLog.DEBUG) {
            if (response.headers != null) {
                for (Map.Entry<String, String> entry : response.headers.entrySet()) {
                    VolleyLog.d(entry.getKey() + "=" + entry.getValue());
                }
            }
        }

        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        VolleyLog.d("getHeaders");
        Map<String, String> headers = super.getHeaders();

        if (headers == null || headers.equals(Collections.emptyMap())) {
            headers = new HashMap<String, String>();
        }

        return headers;
    }

    @Override
    protected void deliverResponse(String response) {
        this.listener.loadingListener().onResponse(response);
    }
}
