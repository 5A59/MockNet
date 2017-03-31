package com.zy.mocknet.application.handler.chain;

import com.zy.mocknet.application.handler.Handler;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

/**
 * Created by zy on 17-3-16.
 */
public abstract class HandlerChain {

    public abstract Handler getHandler(int index);
    public abstract Response start(Request request);
    public abstract void addHandler(Handler handler);
}
