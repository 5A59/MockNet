package com.zy.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.zy.mocknet.MockNet;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.MockConnectionFactory;
import com.zy.mocknet.application.selector.RandomSelector;
import com.zy.mocknet.common.logger.AndroidPrinter;
import com.zy.mocknet.common.logger.Logger;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String URL_MAIN = "http://127.0.0.1:8088";
    private static final String URL_TEST = URL_MAIN + "/test";

    private MockNet mockNet;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMockNet();
        initVolley();
    }

    public void initMockNet() {

        String json = "{" +
                "name: aa" +
                "}";
        byte[] con;
        try {
            con = json.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            con = json.getBytes();
        }

        mockNet = MockNet.create()
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/test", "test msg"))
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/test1", "{'msg':'json'}"));

//        mockNet = MockNet.create()
//                .addConnection(MockConnectionFactory.getInstance()
//                        .createGeneralConnection("/*", "general connection"))
//                .addConnection(MockConnectionFactory.getInstance()
//                        .createGeneralConnection(MockConnection.POST, "/*", "general connection"))
//                .addConnection(new MockConnection.Builder()
//                        .setMethod(MockConnection.GET)
//                        .setUrl("/test")
//                        .setResponseBody("text/json", "first test")
//                        .addResponseHeader("Content-Length", "" + "first test".length())
//                        .addRequestHeader("Content-Length", "" + con.length)
//                        .setVerifyHeaders(true)
//                )
//                .addConnection(new MockConnection.Builder()
//                        .setMethod(MockConnection.GET)
//                        .setUrl("/test")
//                        .setResponseBody("text/json", "second test")
//                        .addResponseHeader("Content-Length", "" + "second test".length())
//                        .addRequestHeader("Content-Length", "" + con.length)
//                        .setVerifyHeaders(true)
//                )
//                .addConnection(new MockConnection.Builder()
//                        .setMethod(MockConnection.GET)
//                        .setUrl("/test")
//                        .setResponseBody("text/json", con, con.length)
//                        .addResponseHeader("Content-Length", "" + con.length)
//                        .addRequestHeader("Content-Length", "" + con.length)
//                        .setVerifyHeaders(true)
//                )
//                .addConnection(new MockConnection.Builder()
//                        .setMethod(MockConnection.POST)
//                        .setUrl("/test")
//                        .setResponseBody("text/json", con, con.length)
//                        .addResponseHeader("Content-Length", "" + con.length)
//                        .addRequestHeader("Content-Length", "" + con.length)
//                        .setVerifyHeaders(true)
//                )
//                .setSelector(new RandomSelector());
    }

    private void initVolley() {
        Logger.init(new AndroidPrinter());
        requestQueue = Volley.newRequestQueue(this);
    }

    public void startServer(View view) {
        mockNet.start();
    }

    public void stopServer(View view) {
        mockNet.stop();
    }

    public void postForm(View view) {
        requestQueue.add(new StringRequest(Request.Method.POST, URL_MAIN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Logger.d(response);
                        toast(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Logger.d(error.toString());
                        toast("response error");
                    }
                }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> para = new HashMap<>();
                para.put("para1", "val1");
                para.put("para2", "val2");
                return para;
            }
        });
    }

    public void postMultipart(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("para1", "val1");
        params.put("para2", "val2");
        requestQueue.add(new MultipartRequest(URL_MAIN, params,
                new VolleyInterface(this) {
                    @Override
                    public void onMySuccess(String result) {
                        Logger.d(result);
                        toast(result);
                    }

                    @Override
                    public void onMyError(VolleyError error) {
                        Logger.d("error");
                        toast("error");
                    }
                }));
    }

    public void postFile(View view) {

    }

    public void get(View view) {
        String url = URL_TEST + "?para1=val1&para2=val2";
        requestQueue.add(new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Logger.d(response);
                toast(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Logger.d("error");
                toast("error");
            }
        }));
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
