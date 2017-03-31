package com.zy.mocknet.server;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by zy on 17-3-5.
 */
public class ThreadPool {

    private final int MAX_THREAD = 10;

    private static volatile ThreadPool threadPool;
    private ExecutorService executorService;

    private ThreadPool() {
        executorService = Executors.newFixedThreadPool(MAX_THREAD);
    }

    private static synchronized void syncInit() {
        if (threadPool == null){
            threadPool = new ThreadPool();
        }
    }

    public static  ThreadPool getInstance() {
        if (threadPool == null){
            syncInit();
        }
        return threadPool;
    }

    public Future<?> submit(Runnable runnable) {
        return executorService.submit(runnable);
    }

    public List<Runnable> shutdownNow() {
        return executorService.shutdownNow();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
