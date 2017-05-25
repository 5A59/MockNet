package com.zy.mocknet.application;

import com.zy.mocknet.application.selector.HeadSelector;
import com.zy.mocknet.application.selector.IConnectionSelector;
import com.zy.mocknet.common.logger.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A store to save @see MockConnection <br>
 * It uses url and method of request to get or put MockConnection. <br>
 * If there are more than one MockConnection can be found with the same url and method,
 *   it will choose one of the by selector.
 * Created by zy on 17-3-5.
 */
public class ConnectionStore {
    private static final String KEY_CONNECT = "++";
    private static volatile ConnectionStore instance;

    private Map<String, List<MockConnection>> connections;
    private Map<String, List<MockConnection>> reConnections;

    private IConnectionSelector selector;

    private ConnectionStore() {
        connections = new HashMap<>();
        reConnections = new HashMap<>();
        selector = HeadSelector.create();
    }

    public static ConnectionStore getInstance() {
        if (instance == null) {
            synchronized (ConnectionStore.class) {
                if (instance == null) {
                    instance = new ConnectionStore();
                }
            }
        }
        return instance;
    }

    /**
     * Add instance of MockConnection.
     * @param con
     * @return
     */
    public boolean addConnection(MockConnection con) {
        if (con == null || con.getUrl() == null || con.getMethod() == null) {
            return false;
        }
        String key = getConnectionKey(con.getMethod(), con.getUrl());
        List<MockConnection> connList = null;
        if (con.getUrl().endsWith("*")) {
            connList = reConnections.get(key);
            if (connList == null) {
                connList = new ArrayList<>();
                reConnections.put(key, connList);
            }
            return connList.add(con);
        }
        connList = connections.get(key);
        if (connList == null) {
            connList = new ArrayList<>();
            connections.put(key, connList);
        }
        return connList.add(con);
    }

    /**
     * Set selector for choosing connection.
     * @param selector
     */
    public void setSelector(IConnectionSelector selector) {
        this.selector = selector;
    }

    /**
     * Get MockConnection by default selector.
     * @param method
     * @param url
     * @return
     */
    public MockConnection getConnection(String method, String url) {
        return getConnection(method, url, selector);
    }

    /**
     * Get MockConnection by specific selector.
     * @param method
     * @param url
     * @param selector
     * @return
     */
    public MockConnection getConnection(String method, String url, IConnectionSelector selector) {
        if (method == null || method.isEmpty() || url == null || url.isEmpty()) {
            Logger.d("=== Request Is Invalid ===");
            return null;
        }
        if (selector == null) {
            selector = HeadSelector.create();
        }
        String key = getConnectionKey(method, url);
        List<MockConnection> connList = connections.get(key);
        MockConnection conn = null;
        if (connList != null && connList.size() > 0) {
            conn = selector.select(connList);
//            conn = connList.get(0);
        }
        if (conn == null && url.endsWith("/")) {
            key = getConnectionKey(method, url.substring(0, url.length() - 1));
            connList = connections.get(key);
            if (connList != null && connList.size() > 0) {
                conn = selector.select(connList);
//                conn = connList.get(0);
            }
        }

        // handle url contains '*' like : 127.0.0.1/test/*
        if (conn == null) {
            for (String k : reConnections.keySet()) {
                String cMethod = getMethod(k);
                if (!cMethod.equals(method)) {
                    continue;
                }
                String cUrl = getUrl(k);
                if (cUrl.substring(0, cUrl.length() - 1).equals(url)
                        || cUrl.substring(0, cUrl.length() - 2).equals(url)) {
                    connList = reConnections.get(k);
                    if (connList != null && connList.size() > 0) {
//                        conn = connList.get(0);
                        conn = selector.select(connList);
                    }
                }
            }
        }
        return conn;
    }

    public static String getConnectionKey(String method, String url) {
        return method + KEY_CONNECT + url;
    }

    public static String getMethod(String key) {
        return key.substring(0, key.indexOf(KEY_CONNECT));
    }

    public static String getUrl(String key) {
        return key.substring(key.indexOf(KEY_CONNECT) + KEY_CONNECT.length());
    }
}
