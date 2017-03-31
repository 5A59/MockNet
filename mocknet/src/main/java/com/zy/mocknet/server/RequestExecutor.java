package com.zy.mocknet.server;

import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

/**
 * Created by zy on 17-3-5.
 */
public interface RequestExecutor {
    Response execute(Request request);
}
