package com.zy.mocknet.server.bean;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Http response
 * Created by zy on 17-3-5.
 */
public class Response {
    private String httpVersion;
    private int statusCode;
    private String reasonPhrase;
    private Headers headers;
    private ResponseBody body;

    public Response() {

    }

    public Response(String httpVersion, int statusCode, String reasonPhrase,
                    Headers headers, ResponseBody body) {
        this.httpVersion = httpVersion;
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.body = body;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public Headers getHeaders() {
        return headers;
    }

    public void setHeaders(Headers headers) {
        this.headers = headers;
    }

    public ResponseBody getBody() {
        return body;
    }

    public void setBody(ResponseBody body) {
        this.body = body;
    }

    public synchronized void addHeader(String name, String val) {
        if (headers == null) {
            headers = new Headers();
        }
        headers.addHeader(name, val);
    }

    public synchronized void addHeader(Map<String, String> header) {
        if (headers == null) {
            headers = new Headers();
        }
        headers.addHeader(header);
    }

    public void writeTo(OutputStream outputStream) throws IOException {
        StringBuilder builder = new StringBuilder();

        builder.append(httpVersion)
                .append(BasicRule.SP)
                .append(statusCode)
                .append(BasicRule.SP)
                .append(reasonPhrase)
                .append(BasicRule.CRLF)
                .append(headers == null ? "" : headers.toString())
                .append(BasicRule.CRLF);

        outputStream.write(builder.toString().getBytes());
        if (body != null) {
            body.writeTo(outputStream);
        }
        outputStream.flush();
    }

    public static Response create404Response() {
        Response response = new Response();
        response.setStatusCode(404);
        response.setHttpVersion("HTTP/1.1");
        response.setReasonPhrase("request not found");
        ResponseBody body = new ResponseBody();
        body.setContentType("text/html; charset=utf-8");
        String html = "<html>" +
                "<h1>" +
                "404" +
                "No Match Url Found" +
                "</h1>" +
                "</html>";
        try {
            body.setContent(html.getBytes("utf-8"), html.getBytes("utf-8").length);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            body.setContent(html.getBytes(), html.getBytes().length);
        }
        response.setBody(body);
        return response;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(httpVersion)
                .append(BasicRule.SP)
                .append(statusCode)
                .append(BasicRule.SP)
                .append(reasonPhrase)
                .append(BasicRule.CRLF)
                .append(headers == null ? "" : headers.toString())
                .append(BasicRule.CRLF)
                .append(body == null ? "" : body.toString());

        return builder.toString();
    }
}
