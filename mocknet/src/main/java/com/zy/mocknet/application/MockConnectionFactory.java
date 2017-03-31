package com.zy.mocknet.application;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zy on 17-3-26.
 */
public final class MockConnectionFactory {
    private static volatile MockConnectionFactory instance;

    private Map<String, String> generalHeaders;

    private MockConnectionFactory() {
        generalHeaders = new HashMap<>();
        generalHeaders.put("Server", "MockNet/1.0.0 (Java)");
    }

    public static MockConnectionFactory getInstance() {
        if (instance == null) {
            synchronized (MockConnectionFactory.class) {
                if (instance == null) {
                    instance = new MockConnectionFactory();
                }
            }
        }
        return instance;
    }

    public MockConnection createGeneralConnection(String url, String body) {
        return createGeneralConnection(MockConnection.GET, url, "text", body);
    }

    public MockConnection createGeneralConnection(String method, String url, String body) {
        return createGeneralConnection(method, url, "text", body);
    }

    public MockConnection createGeneralConnection(String method, String url,
                                                  String contentType, String body) {
        MockConnection.Builder builder = new MockConnection.Builder();
        builder.setResponseParams(generalHeaders)
                .setMethod(method)
                .setUrl(url)
                .addResponseHeader("Content-Type", contentType)
                .addResponseHeader("Content-Length", String.valueOf(body.getBytes().length))
                .setResponseStatusCode(200)
                .setResponseReasonPhrase("OK")
                .setResponseBody(contentType, body);
        return builder.build();
    }

    public MockConnection create404Connection(String url, String body) {
        return create404Connection(MockConnection.GET, url, "text", body);
    }

    public MockConnection create404Connection(String method, String url, String body) {
        return create404Connection(method, url, "text", body);
    }

    public MockConnection create404Connection(String method, String url,
                                              String contentType, String body) {
        MockConnection.Builder builder = new MockConnection.Builder();
        builder.setResponseParams(generalHeaders)
                .setMethod(method)
                .setUrl(url)
                .setResponseStatusCode(404)
                .setResponseReasonPhrase("NotFound")
                .setResponseBody(contentType, body);
        return builder.build();
    }
}
