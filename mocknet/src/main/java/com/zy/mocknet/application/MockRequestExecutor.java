package com.zy.mocknet.application;

import com.zy.mocknet.application.handler.*;
import com.zy.mocknet.application.handler.chain.HandlerChain;
import com.zy.mocknet.application.handler.chain.RealHandlerChain;
import com.zy.mocknet.server.RequestExecutor;
import com.zy.mocknet.server.bean.Request;
import com.zy.mocknet.server.bean.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 17-3-17.
 */
public class MockRequestExecutor implements RequestExecutor {

    private HandlerChain chain;
    private List<Handler> userHandlers;
    private boolean initHandler;

    public MockRequestExecutor() {
        chain = new RealHandlerChain();
        userHandlers = new ArrayList<>();
        initHandler = true;
    }

    public MockRequestExecutor(List<Handler> handlers) {
        this();
        userHandlers.addAll(handlers);
    }

    public MockRequestExecutor(HandlerChain chain) {
        this.chain = chain;
        userHandlers = new ArrayList<>();
        initHandler = false;
    }

    public void addUserHandler(Handler handler) {
        userHandlers.add(handler);
    }

    public void addUserHandler(List<Handler> handlers) {
        this.userHandlers.addAll(handlers);
    }

    public void resetUserHandler(List<Handler> handlers) {
        this.userHandlers.clear();
        this.userHandlers.addAll(handlers);
    }

    @Override
    public Response execute(Request request) {
        for (Handler h : userHandlers) {
            chain.addHandler(h);
        }

        if (initHandler) {
            chain.addHandler(new BlockHandler());
            chain.addHandler(new VerifyParamHandler());
            chain.addHandler(new VerifyHeaderHandler());
            chain.addHandler(new LogHandler());
            chain.addHandler(new ConnectionHandler());
        }

        return chain.start(request);
    }
}
