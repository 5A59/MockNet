package com.zy.mocknet.server.bean;

import java.util.Map;

/**
 * Created by zy on 17-3-5.
 */
public class Request {
    private String method;
    private String requestUri;
    private String httpVresion;
    private Headers header;
    private RequestBody body;
    private Parameters params;

    public Request() {

    }

    public Request(String method, String requestUri, String httpVresion, Headers header, RequestBody body) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVresion = httpVresion;
        this.header = header;
        this.body = body;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getRequestUri() {
        return requestUri;
    }

    public void setRequestUri(String requestUri) {
        this.requestUri = requestUri;
    }

    public String getHttpVresion() {
        return httpVresion;
    }

    public void setHttpVresion(String httpVresion) {
        this.httpVresion = httpVresion;
    }

    public Headers getHeader() {
        return header;
    }

    public void setHeader(Headers header) {
        this.header = header;
    }

    public RequestBody getBody() {
        return body;
    }

    public void setBody(RequestBody body) {
        this.body = body;
    }

    public Parameters getParams() {
        return params;
    }

    public void setParams(Parameters params) {
        this.params = params;
    }

    public synchronized void addHeader(String name, String val) {
        if (header == null) {
            header = new Headers();
        }
        header.addHeader(name, val);
    }

    public synchronized void addHeader(Map<String, String> headers) {
        if (this.header == null) {
            this.header = new Headers();
        }
        this.header.addHeader(headers);
    }

    public synchronized void addParam(String name, String val) {
        if (params == null) {
            params = new Parameters();
        }
        params.addParam(name, val);
    }

    public synchronized void addParam(Map<String, String> param) {
        if (params == null) {
            params = new Parameters();
        }
        params.addParam(param);
    }

    // delete body tmp file
    public void destory() {
        if (body != null) {
            body.destroy();
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        // request line
        builder.append(method)
                .append(BasicRule.SP)
                .append(requestUri)
                .append(BasicRule.SP)
                .append(httpVresion)
                .append(BasicRule.CRLF);

        // params
//        if (params != null) {
//            for (Map.Entry<String, String> e : params.getParams().entrySet()) {
//                builder.append(e.getKey()).append(" = ").append(e.getValue()).append("\n");
//            }
//        }

        // headers
        if (header != null) {
            builder.append(header.toString()).append(BasicRule.CRLF);
        }
        // body
        if (body != null) {
            builder.append(body.toString()).append(BasicRule.CRLF);
        }

        return builder.toString();
    }
}
