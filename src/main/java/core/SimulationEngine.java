package core;

import util.ThreadPoolManager;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class SimulationEngine {
    private Grid currentGrid;
    private Grid nextGrid;
    private final ExecutorService executor = ThreadPoolManager.getExecutor();
    private boolean running = false;
    private int speed = 100; // Delay in ms
    private long generation = 0;

    public SimulationEngine(Grid initialGrid) {
        this.currentGrid = initialGrid;
        this.nextGrid = new Grid();
    }

    public synchronized void step() {
        Set<Long> activeChunks = currentGrid.getActiveChunkKeys();
        Set<Long> chunksToProcess = new HashSet<>();
        
        // Add current active chunks and their immediate neighbors
        for (long key : activeChunks) {
            long cx = Grid.getChunkX(key);
            long cy = Grid.getChunkY(key);
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    chunksToProcess.add(getChunkKey(cx + dx, cy + dy));
                }
            }
        }

        CountDownLatch latch = new CountDownLatch(chunksToProcess.size());
        for (long key : chunksToProcess) {
            executor.submit(() -> {
                try {
                    processChunk(key);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Swap grids
        Grid temp = currentGrid;
        currentGrid = nextGrid;
        nextGrid = temp;
        nextGrid.clear();
        generation++;
    }

    public long getGeneration() {
        return generation;
    }

    public void resetGeneration() {
        generation = 0;
    }

    public int getPopulation() {
        int pop = 0;
        for (long key : currentGrid.getActiveChunkKeys()) {
            WorldChunk chunk = currentGrid.getChunk(key);
            if (chunk != null) {
                pop += chunk.getLiveCells();
            }
        }
        return pop;
    }

    public boolean isRunning() {
        return running;
    }

    private void processChunk(long key) {
        long cx = Grid.getChunkX(key);
        long cy = Grid.getChunkY(key);
        long startX = cx * WorldChunk.CHUNK_SIZE;
        long startY = cy * WorldChunk.CHUNK_SIZE;

        for (int lx = 0; lx < WorldChunk.CHUNK_SIZE; lx++) {
            for (int ly = 0; ly < WorldChunk.CHUNK_SIZE; ly++) {
                long gx = startX + lx;
                long gy = startY + ly;
                int neighbors = countNeighbors(gx, gy);
                boolean alive = currentGrid.getCell(gx, gy);

                if (alive) {
                    if (neighbors == 2 || neighbors == 3) {
                        nextGrid.setCell(gx, gy, true);
                    }
                } else {
                    if (neighbors == 3) {
                        nextGrid.setCell(gx, gy, true);
                    }
                }
            }
        }
    }

    private int countNeighbors(long gx, long gy) {
        int count = 0;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                if (dx == 0 && dy == 0) continue;
                if (currentGrid.getCell(gx + dx, gy + dy)) {
                    count++;
                }
            }
        }
        return count;
    }

    private long getChunkKey(long chunkX, long chunkY) {
        return (chunkX << 32) | (chunkY & 0xffffffffL);
    }

    public synchronized void start() {
        running = true;
        new Thread(() -> {
            while (running) {
                step();
                try {
                    Thread.sleep(speed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    public synchronized void stop() {
        running = false;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Grid getCurrentGrid() {
        return currentGrid;
    }
}
