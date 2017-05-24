package com.zy.mocknet;

import com.zy.mocknet.application.ConnectionStore;
import com.zy.mocknet.application.MockConnection;
import com.zy.mocknet.application.MockRequestExecutor;
import com.zy.mocknet.application.handler.Handler;
import com.zy.mocknet.application.selector.IConnectionSelector;
import com.zy.mocknet.common.Utils;
import com.zy.mocknet.common.logger.AndroidPrinter;
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

    private MockNet() {
        executor = new MockRequestExecutor();
    }

    /**
     * Create a MockNet instance.<br>
     * It will init this instance by judging whether the system is Android or not.
     * @return MockNet
     */
    public static MockNet create() {
        // TODO: can work but not good
        try {
            Class.forName("android.app.Application");
            initAndroid();
            Logger.d("Current system is Android");
        } catch (ClassNotFoundException e) {
            initJava();
            Logger.d("Current system is not Android");
        }
        String system = Utils.getInstance().getSystem();
        Logger.d(system);
        return new MockNet();
    }

    private static void initAndroid() {
        Logger.init(new AndroidPrinter());
    }

    private static void initJava() {
        Logger.init();
    }

    /**
     *
     * @param handler
     * @return
     */
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

    public MockNet start() {
        server = Server.createHttpServer(executor);
        runServer();
        return this;
    }

    public MockNet start(int port) {
        server = Server.createHttpServer(port, executor);
        runServer();
        return this;
    }

    public MockNet start(ServerSocket serverSocket) {
        server = Server.createServer(serverSocket);
        runServer();
        return this;
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

    public MockNet stop() {
        server.stop();
        return this;
    }
}
