package com.zy.mocknet.server;

import com.zy.mocknet.application.MockRequestExecutor;
import com.zy.mocknet.common.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by zy on 17-3-5.
 */
public class Server {
    public static int PORT = 8088;
    private ServerSocket serverSocket;
    private RequestExecutor executor;
    private boolean running;

    private Server(RequestExecutor executor, ServerSocket serverSocket) {
        running = false;
        this.serverSocket = serverSocket;
        this.executor = executor;
    }

    public void start() {
        Logger.d("Mock Server Start");
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                Logger.d("Request Comming");
                RequestRunnable runnable = new RequestRunnable(socket, executor);
                Future<?> future = ThreadPool.getInstance().submit(runnable);
                try {
                    if (future.get() == null) {
                        Logger.d("Accept Request Success");
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
    }

    public void stop() {
        Logger.d("Mock Server Stop");
        running = false;
        ThreadPool.getInstance().shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Server createHttpServer() {
        return new Server(new MockRequestExecutor(),
                ServerSocketFactory.getInstance().createHttpServerSocket(PORT));
    }

    public static Server createHttpServer(int port) {
        return new Server(new MockRequestExecutor(),
                ServerSocketFactory.getInstance().createHttpServerSocket(port));
    }

    public static Server createHttpServer(RequestExecutor executor) {
        return new Server(executor,
                ServerSocketFactory.getInstance().createHttpServerSocket(PORT));
    }

    public static Server createHttpServer(int port, RequestExecutor executor) {
        return new Server(executor,
        ServerSocketFactory.getInstance().createHttpServerSocket(port));
    }

    public static Server createServer(RequestExecutor executor, ServerSocket serverSocket) {
        return new Server(executor, serverSocket);
    }

    public static Server createServer(ServerSocket serverSocket) {
        return new Server(new MockRequestExecutor(), serverSocket);
    }
}
