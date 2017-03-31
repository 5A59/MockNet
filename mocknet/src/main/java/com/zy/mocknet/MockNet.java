package com.zy.mocknet;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.MockRequestExecutor;
import com.zy.mocknet.application.handler.Handler;
import com.zy.mocknet.application.selector.IConnectionSelector;
import com.zy.mocknet.common.logger.Logger;
import com.zy.mocknet.server.Server;
import com.zy.mocknet.server.ThreadPool;

import java.net.ServerSocket;
import java.util.List;

/**
 * Created by zy on 17-3-5.
 */
public class MockNet {

    private MockRequestExecutor executor;
    private Server server;
    private int port;

    private MockNet() {
        executor = new MockRequestExecutor();
    }

    public static MockNet create() {
        Logger.init();
        return new MockNet();
    }

    public MockNet addHandler(Handler handler) {
        executor.addUserHandler(handler);
        return this;
    }

    public MockNet addHandler(List<Handler> handlers) {
        executor.addUserHandler(handlers);
        return this;
    }

    public MockNet addConnection(MockConnection connection) {
        ConnectionStore.getInstance().addConnection(connection);
        return this;
    }

    public MockNet addConnection(MockConnection.Builder builder) {
        ConnectionStore.getInstance().addConnection(builder.build());
        return this;
    }

    public MockNet setSelector(IConnectionSelector selector) {
        ConnectionStore.getInstance().setSelector(selector);
        return this;
    }

    public void start() {
        server = Server.createHttpServer(executor);
        runServer();
    }

    public void start(int port) {
        server = Server.createHttpServer(port, executor);
        runServer();
    }

    public void start(ServerSocket serverSocket) {
        server = Server.createServer(serverSocket);
        runServer();
    }

    public void runServer() {
//        ThreadPool.getInstance().submit(() -> server.start()); // lambda
        ThreadPool.getInstance().submit(new Runnable() {
            @Override
            public void run() {
                server.start();
            }
        });
    }

    public void stop() {
        server.stop();
    }
}
