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
     * Add request handler to executor.
     * @param handler
     * @return Return mocknet instance.
     */
    public MockNet addHandler(Handler handler) {
        executor.addUserHandler(handler);
        return this;
    }

    /**
     * Add a list of request handlers to executor.
     * @param handlers list of handlers
     * @return Return mocknet instance.
     */
    public MockNet addHandler(List<Handler> handlers) {
        executor.addUserHandler(handlers);
        return this;
    }

    /**
     * Add MockConnection to @see ConnectionStore
     * @param connection MockConnection instance
     * @return Return mocknet instance.
     */
    public MockNet addConnection(MockConnection connection) {
        ConnectionStore.getInstance().addConnection(connection);
        return this;
    }

    /**
     * Add MockConnection to @see ConnectionStore
     * @param builder MockConnection builder instance
     * @return Return mocknet instance.
     */
    public MockNet addConnection(MockConnection.Builder builder) {
        ConnectionStore.getInstance().addConnection(builder.build());
        return this;
    }

    /**
     * Set selector when multiple MockConnections can be found with the same url and method.
     * @param selector
     * @return Return mocknet instance.
     */
    public MockNet setSelector(IConnectionSelector selector) {
        ConnectionStore.getInstance().setSelector(selector);
        return this;
    }

    /**
     * Start server with default port 8088 <br>
     * We will create a HttpServer with default params.
     * @return Return mocknet instance.
     */
    public MockNet start() {
        server = Server.createHttpServer(executor);
        runServer();
        return this;
    }

    /**
     * Start server with specific port. <br>
     * We will create a HttpServer with other default params.
     * @param port
     * @return Return mocknet instance.
     */
    public MockNet start(int port) {
        server = Server.createHttpServer(port, executor);
        runServer();
        return this;
    }

    /**
     * Start server with specific @see ServerSocket.
     * @param serverSocket
     * @return Return mocknet instance.
     */
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

    /**
     * Stop server.
     * @return Return mocknet instance.
     */
    public MockNet stop() {
        server.stop();
        return this;
    }
}
