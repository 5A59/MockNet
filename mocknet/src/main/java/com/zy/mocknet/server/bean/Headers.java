package com.zy.mocknet.server.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Http headers.
 * Created by zy on 17-3-16.
 */
public class Headers {
    private Map<String, String> keyMap; // lowercase - realkey
    private Map<String, String> headers;

    public Headers() {
        headers = new HashMap<>();
        keyMap = new HashMap<>();
    }

    public Headers(Map<String, String> headers) {
        this();
        addHeader(headers);
    }

    public void addHeader(String key, String val) {
        if (key == null || key.isEmpty()) {
            return ;
        }
        keyMap.put(key.toLowerCase(), key);
        headers.put(key, val);
    }

    public void addHeader(Map<String, String> headers) {
        if (headers == null || headers.isEmpty()) {
            return ;
        }
        for (Map.Entry<String, String> e : headers.entrySet()) {
            if (e.getKey() == null || e.getKey().isEmpty()) {
                continue;
            }
            keyMap.put(e.getKey().toLowerCase(), e.getKey());
            this.headers.put(e.getKey(), e.getValue());
        }
    }

    public Map<String, String> getHeaders() {
        return new HashMap<>(headers);
    }

    public String getHeader(String key) {
        String realKey = keyMap.get(key == null ? "" : key.toLowerCase());
        if (realKey == null) {
            return "";
        }
        return headers.get(realKey);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
        }
        return builder.toString();
    }
}
