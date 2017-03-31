package com.zy.mocknet.application.selector;

import com.zy.mocknet.application.MockConnection;

import java.util.List;

/**
 * Created by zy on 17-3-29.
 */
public interface IConnectionSelector {
    MockConnection select(List<MockConnection> connections);
}
