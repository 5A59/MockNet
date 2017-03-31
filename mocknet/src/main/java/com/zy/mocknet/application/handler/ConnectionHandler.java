package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.application.selector.IConnectionSelector;
import com.zy.mocknet.server.bean.Headers;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;
import com.zy.mocknet.server.bean.ResponseBody;

/**
 * Created by zy on 17-3-20.
 */
public class ConnectionHandler extends Handler {

    @Override
    public Response handle(Request request, HandlerChain chain, int index) {
        String method = request.getMethod();
        String url = request.getRequestUri();
        MockConnection connection = ConnectionStore.getInstance().getConnection(method, url);
        if (connection == null) {
            return Response.create404Response();
        }

        Response response = new Response();
        response.setStatusCode(connection.getResStatusCode());
        response.setHttpVersion(connection.getHttpVersion());
        response.setReasonPhrase(connection.getResReasonPhrase());

        Headers headers = new Headers();
        headers.addHeader(connection.getResHeaders());
        response.setHeaders(headers);
        ResponseBody body = new ResponseBody();
        if (connection.getResBody() != null) {
            body.setContent(connection.getResBody(), connection.getBodyLen());
        }else {
            body.setFile(connection.getResFile());
        }
        response.setBody(body);
        return response;
    }
}
