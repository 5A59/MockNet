package com.zy.mocknet;

import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.MockConnectionFactory;
import com.zy.mocknet.application.selector.RandomSelector;

import java.io.UnsupportedEncodingException;

public class Main {

    public static void main(String[] args) {

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

        MockNet.create()
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection("/*", "general connection"))
                .addConnection(MockConnectionFactory.getInstance()
                        .createGeneralConnection(MockConnection.POST, "/*", "general connection"))
                .addConnection(new MockConnection.Builder()
                        .setMethod(MockConnection.GET)
                        .setUrl("/test")
                        .setResponseBody("text/json", "first test")
                        .addResponseHeader("Content-Length", "" + "first test".length())
                        .addRequestHeader("Content-Length", "" + con.length)
                        .setVerifyHeaders(true)
                )
                .addConnection(new MockConnection.Builder()
                        .setMethod(MockConnection.GET)
                        .setUrl("/test")
                        .setResponseBody("text/json", "second test")
                        .addResponseHeader("Content-Length", "" + "second test".length())
                        .addRequestHeader("Content-Length", "" + con.length)
                        .setVerifyHeaders(true)
                )
                .addConnection(new MockConnection.Builder()
                        .setMethod(MockConnection.GET)
                        .setUrl("/test")
                        .setResponseBody("text/json", con, con.length)
                        .addResponseHeader("Content-Length", "" + con.length)
                        .addRequestHeader("Content-Length", "" + con.length)
                        .setVerifyHeaders(true)
                )
                .addConnection(new MockConnection.Builder()
                        .setMethod(MockConnection.POST)
                        .setUrl("/test")
                        .setResponseBody("text/json", con, con.length)
                        .addResponseHeader("Content-Length", "" + con.length)
                        .addRequestHeader("Content-Length", "" + con.length)
                        .setVerifyHeaders(true)
                )
                .setSelector(new RandomSelector())
                .start();
    }
}
