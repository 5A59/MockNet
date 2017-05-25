package com.zy.mocknet.server;

import com.zy.mocknet.application.MockRequestExecutor;
import com.zy.mocknet.common.logger.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * This class parses data that comes from socket and creates a request with these data. <br>
 * Then send the request to RequestExecutor and receive a response. <br>
 * Then send the response to client through socket. <br>
 * The real jobs of parsing data is done by @see RequestRunnable.
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

    /**
     * Start the server.
     */
    public void start() {
        Logger.d("!!! Mock Server Start !!!");
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                if (socket.isClosed()) {
                    continue;
                }
                Logger.d(" \n");
                Logger.d("===========================================================\n");
                Logger.d("Request Comming");
                RequestRunnable runnable = new RequestRunnable(socket, executor);
                Future<?> future = ThreadPool.getInstance().submit(runnable);
                try {
                    if (future.get() == null) {
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } catch (SocketException e) {
                Logger.d("!!! ServerSocket closed !!!");
                running = false;
            } catch (IOException e) {
                Logger.exception(e);
            }
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        Logger.d("!!! Mock Server Stop !!!");
        running = false;
        ThreadPool.getInstance().shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a http server with default port 8088.
     * @see ServerSocketFactory
     * @see MockRequestExecutor
     * @return Return a Server instance.
     */
    public static Server createHttpServer() {
        return new Server(new MockRequestExecutor(),
                ServerSocketFactory.getInstance().createHttpServerSocket(PORT));
    }

    /**
     * Create a http server with spceific port.
     * @param port
     * @return Return a Server instance.
     */
    public static Server createHttpServer(int port) {
        return new Server(new MockRequestExecutor(),
                ServerSocketFactory.getInstance().createHttpServerSocket(port));
    }

    /**
     * Create a http server with specific executor.
     * @param executor
     * @return
     */
    public static Server createHttpServer(RequestExecutor executor) {
        return new Server(executor,
                ServerSocketFactory.getInstance().createHttpServerSocket(PORT));
    }

    /**
     * Create a http server with specific prot and specific executor.
     * @param port
     * @param executor
     * @return
     */
    public static Server createHttpServer(int port, RequestExecutor executor) {
        return new Server(executor,
        ServerSocketFactory.getInstance().createHttpServerSocket(port));
    }

    /**
     * Create a server with specific execurot and serversocket.
     * @param executor
     * @param serverSocket
     * @return
     */
    public static Server createServer(RequestExecutor executor, ServerSocket serverSocket) {
        return new Server(executor, serverSocket);
    }

    /**
     * Create a server with specific serversocket.
     * @param serverSocket
     * @return
     */
    public static Server createServer(ServerSocket serverSocket) {
        return new Server(new MockRequestExecutor(), serverSocket);
    }
}
