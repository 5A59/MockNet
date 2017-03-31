package com.zy.mocknet.application.handler.chain;

import com.zy.mocknet.application.handler.Handler;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 17-3-17.
 */
public class RealHandlerChain extends HandlerChain {

    private List<Handler> handlers;
    private int index = 0;

    public RealHandlerChain() {
        handlers = new ArrayList<>();
    }

    @Override
    public Handler getHandler(int index) {
        if (index < 0 || index >= handlers.size()) {
            return null;
        }
        return handlers.get(index);
    }

    @Override
    public Response start(Request request) {
        Handler handler = handlers.get(index);
        Response response = handler.handle(request, this, index);
        return response;
    }

    @Override
    public void addHandler(Handler handler) {
        if (handler != null) {
            handlers.add(handler);
        }
    }
}
