package com.zy.mocknet.server.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * Http params.
 * Created by zy on 17-3-18.
 */
public class Parameters {
    private Map<String, String> params;

    public Parameters() {
        params = new HashMap<>();
    }

    public Parameters(Map<String, String> params) {
        this();
        this.params.putAll(params);
    }

    public void addParam(String name, String val) {
        params.put(name, val);
    }

    public void addParam(Map<String, String> params) {
        this.params.putAll(params);
    }

    public Map<String, String> getParams() {
        return new HashMap<>(params);
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
