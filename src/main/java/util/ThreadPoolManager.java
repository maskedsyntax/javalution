package util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPoolManager {
    private static final ExecutorService executor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors());

    public static ExecutorService getExecutor() {
        return executor;
    }

    public static void shutdown() {
        executor.shutdown();
    }
}
