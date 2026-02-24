package core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class RLEWriter {
    public static void save(String filePath, Grid grid) throws IOException {
        long minX = Long.MAX_VALUE, minY = Long.MAX_VALUE;
        long maxX = Long.MIN_VALUE, maxY = Long.MIN_VALUE;

        // Find bounding box
        boolean empty = true;
        for (long key : grid.getActiveChunkKeys()) {
            WorldChunk chunk = grid.getChunk(key);
            long cx = Grid.getChunkX(key);
            long cy = Grid.getChunkY(key);
            for (int x = 0; x < WorldChunk.CHUNK_SIZE; x++) {
                for (int y = 0; y < WorldChunk.CHUNK_SIZE; y++) {
                    if (chunk.getCell(x, y)) {
                        long gx = cx * WorldChunk.CHUNK_SIZE + x;
                        long gy = cy * WorldChunk.CHUNK_SIZE + y;
                        minX = Math.min(minX, gx);
                        minY = Math.min(minY, gy);
                        maxX = Math.max(maxX, gx);
                        maxY = Math.max(maxY, gy);
                        empty = false;
                    }
                }
            }
        }

        if (empty) return;

        int width = (int) (maxX - minX + 1);
        int height = (int) (maxY - minY + 1);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write("#C Created by Javalution\n");
            writer.write("x = " + width + ", y = " + height + ", rule = B3/S23\n");

            StringBuilder line = new StringBuilder();
            for (long y = minY; y <= maxY; y++) {
                int runCount = 0;
                boolean lastState = grid.getCell(minX, y);

                for (long x = minX; x <= maxX; x++) {
                    boolean currentState = grid.getCell(x, y);
                    if (currentState == lastState) {
                        runCount++;
                    } else {
                        appendRun(line, runCount, lastState);
                        runCount = 1;
                        lastState = currentState;
                    }
                }
                appendRun(line, runCount, lastState);
                if (y < maxY) {
                    line.append("$");
                } else {
                    line.append("!");
                }
                
                if (line.length() > 70) {
                    writer.write(line.toString() + "\n");
                    line.setLength(0);
                }
            }
            if (line.length() > 0) {
                writer.write(line.toString() + "\n");
            }
        }
    }

    private static void appendRun(StringBuilder sb, int count, boolean alive) {
        if (count > 1) sb.append(count);
        sb.append(alive ? 'o' : 'b');
    }
}
