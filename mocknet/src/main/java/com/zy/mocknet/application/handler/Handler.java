package com.zy.mocknet.application.handler;

import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

/**
 * Created by zy on 17-3-5.
 */
public abstract class Handler {
    public abstract Response handle(Request request, HandlerChain chain, int index);
}
