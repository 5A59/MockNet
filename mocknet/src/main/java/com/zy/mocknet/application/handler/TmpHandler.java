package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.server.bean.Headers;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;
import com.zy.mocknet.server.bean.ResponseBody;

import java.io.UnsupportedEncodingException;

/**
 * Created by zy on 17-3-17.
 */
public class TmpHandler extends Handler {

    @Override
    public Response handle(Request request, HandlerChain chain, int index) {
        Response response = new Response();
        response.setStatusCode(200);
        response.setReasonPhrase("OK");
        response.setHttpVersion("http/1.1");

        String content = "<html>" +
                "<h1>" +
                "TMP PAGE" +
                "</h1>" +
                "</html>";

        Headers headers = new Headers();
        headers.addHeader("Content-Type", "text");
        headers.addHeader("Content-Length", "" + content.length());
        response.setHeaders(headers);

        ResponseBody body = new ResponseBody();
        body.setContentType("text; charset=utf-8");
        try {
            body.setContent(content.getBytes("utf-8"), content.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.setBody(body);
        return response;
    }
}
