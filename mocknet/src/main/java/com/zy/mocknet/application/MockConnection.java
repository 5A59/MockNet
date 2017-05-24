package com.zy.mocknet.application;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy on 17-3-5.
 */
public class MockConnection {
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String HEAD = "HEAD";
    public static final String OPTIONS = "OPTIONS";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String TRACE = "TRACE";
    public static final String CONNECT = "CONNECT";

    public static final String HTTP_1_1 = "HTTP/1.1";

    public static final int NO_BLOCK = -1;

    // TODO: 感觉这个类还是有点乱

    private Config config;

    // request
    private MockRequest request;

    // response
    private MockResponse response;

    private MockConnection() {
        config = new Config();
        request = new MockRequest();
        response = new MockResponse();
        request.httpVersion = "HTTP/1.1";
        response.resStatusCode = 200;
        response.resReasonPhrase = "OK";
    }

    public String getHttpVersion() {
        return request.httpVersion;
    }

    public void setHttpVersion(String httpVersion) {
        request.httpVersion = httpVersion;
    }

    public String getUrl() {
        return request.url;
    }

    public void setUrl(String url) {
        request.url = url;
    }

    public String getMethod() {
        return request.method;
    }

    public void setMethod(String method) {
        request.method = method;
    }

    public boolean isVerifyHeader() {
        return config.verifyHeader;
    }

    public void setVerifyHeader(boolean verifyHeader) {
        this.config.verifyHeader = verifyHeader;
    }

    public boolean isVerifyParam() {
        return config.verifyParam;
    }

    public void setVerifyParam(boolean verifyParam) {
        this.config.verifyParam = verifyParam;
    }

    public Map<String, String> getReqHeaders() {
        return request.reqHeaders;
    }

    public void setReqHeaders(Map<String, String> reqHeaders) {
        request.reqHeaders = reqHeaders;
    }

    public Map<String, String> getReqParams() {
        return request.reqParams;
    }

    public void setReqParams(Map<String, String> reqParams) {
        request.reqParams = reqParams;
    }

    public Map<String, String> getResHeaders() {
        return response.resHeaders;
    }

    public void setResHeaders(Map<String, String> resHeaders) {
        response.resHeaders = resHeaders;
    }

    public Map<String, String> getResParams() {
        return response.resParams;
    }

    public void setResParams(Map<String, String> resParams) {
        response.resParams = resParams;
    }

    public byte[] getResBody() {
        return response.resBody;
    }

    public void setResBody(byte[] resBody) {
        response.resBody = resBody;
    }

    public int getResStatusCode() {
        return response.resStatusCode;
    }

    public void setResStatusCode(int resStatusCode) {
        response.resStatusCode = resStatusCode;
    }

    public String getResReasonPhrase() {
        return response.resReasonPhrase;
    }

    public void setResReasonPhrase(String resReasonPhrase) {
        response.resReasonPhrase = resReasonPhrase;
    }

    public int getBodyLen() {
        return response.bodyLen;
    }

    public void setBodyLen(int bodyLen) {
        response.bodyLen = bodyLen;
    }

    public File getResFile() {
        return response.resFile;
    }

    public void setResFile(File resFile) {
        response.resFile = resFile;
    }

    public String getContentType() {
        return response.contentType;
    }

    public void setContentType(String contentType) {
        response.contentType = contentType;
    }

    public int getBlockTime() {
        return config.blockTime;
    }

    public void setBlockTime(int blockTime) {
        config.blockTime = blockTime;
    }

    public boolean isLog() {
        return config.log;
    }

    public void setLog(boolean log) {
        config.log = log;
    }

    private class Config {
        int blockTime = NO_BLOCK;
        boolean verifyHeader = false;
        boolean verifyParam = false;
        boolean log = true;
    }

    private class MockResponse {
        private Map<String, String> resHeaders;
        private Map<String, String> resParams;
        private int bodyLen;
        private byte[] resBody;
        private File resFile;
        private int resStatusCode;
        private String contentType;
        private String resReasonPhrase;
    }

    private class MockRequest {
        private String url;
        private String method;
        private String httpVersion;
        private Map<String, String> reqHeaders;
        private Map<String, String> reqParams;
    }

    public static class Builder {

        private MockConnection connection;

        public Builder() {
            connection = new MockConnection();
        }

        public Builder setUrl(String url) {
            connection.request.url = url;
            return this;
        }

        public Builder setMethod(String method) {
            connection.request.method = method;
            return this;
        }

        public Builder setHttpVersion(String version) {
            connection.request.httpVersion = version;
            return this;
        }

        public Builder setResponseStatusCode(int code) {
            connection.response.resStatusCode = code;
            return this;
        }

        public Builder setResponseReasonPhrase(String phrase) {
            connection.response.resReasonPhrase = phrase;
            return this;
        }

        public synchronized void initReqHeaders() {
            if (connection.request.reqHeaders == null) {
                connection.request.reqHeaders = new HashMap<>();
            }
        }

        public Builder setVerifyHeaders(boolean verify) {
            initReqHeaders();
            connection.config.verifyHeader = verify;
            return this;
        }

        public Builder setRequestHeaders(Map<String, String> headers, boolean verifyHeaders) {
            initReqHeaders();
            connection.request.reqHeaders.putAll(headers);
            connection.config.verifyHeader = verifyHeaders;
            return this;
        }

        public Builder addRequestHeader(String name, String val) {
            initReqHeaders();
            connection.request.reqHeaders.put(name, val);
            return this;
        }

        public synchronized void initReqParams() {
            if (connection.request.reqParams == null) {
                connection.request.reqParams = new HashMap<>();
            }
        }

        public Builder setVerifyParams(boolean verify) {
            initReqParams();
            connection.config.verifyParam = verify;
            return this;
        }

        public Builder setRequestParams(Map<String, String> params, boolean verifyParams) {
            initReqParams();
            connection.request.reqParams.putAll(params);
            connection.config.verifyParam = verifyParams;
            return this;
        }

        public Builder addRequestParam(String name, String val) {
            initReqParams();
            connection.request.reqParams.put(name, val);
            return this;
        }

        public synchronized void initResHeaders() {
            if (connection.response.resHeaders == null) {
                connection.response.resHeaders = new HashMap<>();
            }
        }

        public Builder setResponseHeaders(Map<String, String> headers) {
            initResHeaders();
            connection.response.resHeaders.clear();
            connection.response.resHeaders.putAll(headers);
            return this;
        }

        public Builder addResponseHeader(String name, String val) {
            initResHeaders();
            connection.response.resHeaders.put(name ,val);
            return this;
        }

        public Builder addResponseHeader(Map<String, String> headers) {
            if (headers == null || headers.isEmpty()) {
                return this;
            }
            initResHeaders();
            connection.response.resHeaders.putAll(headers);
            return this;
        }

        public synchronized void initResParam() {
            connection.response.resParams = new HashMap<>();
        }

        public Builder setResponseParams(Map<String, String> params) {
            if (connection.response.resParams == null) {
                initResParam();
            }
            connection.response.resParams.putAll(params);
            return this;
        }

        public Builder setResponseBody(String contentType, byte[] body, int len) {
            connection.response.contentType = contentType;
            connection.response.resBody = Arrays.copyOf(body, len);
            return this;
        }

        public Builder setResponseBody(String contentType, String body) {
            connection.response.contentType = contentType;
            connection.response.resBody = body.getBytes();
            connection.response.bodyLen = connection.response.resBody.length;
            return this;
        }

        public Builder setResponseBody(String contentType, File file) {
            connection.response.contentType = contentType;
            connection.response.resFile = file;
            return this;
        }

        public Builder setBlockTime(int time) {
            connection.config.blockTime = time;
            return this;
        }

        public Builder isLog(boolean log) {
            connection.config.log = log;
            return this;
        }

        public MockConnection build() {
            return connection;
        }
    }
}
