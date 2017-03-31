package com.zy.mocknet.application.selector;

import com.zy.mocknet.application.MockConnection;

import java.util.List;

/**
 * Created by zy on 17-3-29.
 */
public class HeadSelector implements IConnectionSelector {

    private static HeadSelector selector;

    public HeadSelector() {

    }

    @Override
    public MockConnection select(List<MockConnection> connections) {
        if (connections != null && connections.size() > 0) {
            return connections.get(0);
        }
        return null;
    }

    public static HeadSelector create() {
        if (selector == null) {
            synchronized (HeadSelector.class) {
                if (selector == null) {
                    selector = new HeadSelector();
                }
            }
        }
        return selector;
    }
}
