package com.zy.mocknet.application.selector;

import com.zy.mocknet.application.MockConnection;

import java.util.List;
import java.util.Random;

/**
 * Created by zy on 17-3-30.
 */
public class RandomSelector implements IConnectionSelector {

    static Random random = new Random();

    @Override
    public MockConnection select(List<MockConnection> connections) {
        if (connections == null || connections.size() == 0) {
            return null;
        }

        int index = random.nextInt(connections.size());

        return connections.get(index);
    }
}
