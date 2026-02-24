package core;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Grid {
    private final Map<Long, WorldChunk> chunks = new HashMap<>();
    private boolean toroidal = false;
    private long width = Long.MAX_VALUE;
    private long height = Long.MAX_VALUE;

    public void setToroidal(boolean toroidal) {
        this.toroidal = toroidal;
    }

    public boolean isToroidal() {
        return toroidal;
    }

    public void setCell(long x, long y, boolean alive) {
        if (toroidal) {
            x = (x % width + width) % width;
            y = (y % height + height) % height;
        }
        long chunkX = Math.floorDiv(x, WorldChunk.CHUNK_SIZE);
        long chunkY = Math.floorDiv(y, WorldChunk.CHUNK_SIZE);
        int localX = (int) (x - chunkX * WorldChunk.CHUNK_SIZE);
        int localY = (int) (y - chunkY * WorldChunk.CHUNK_SIZE);

        long key = getChunkKey(chunkX, chunkY);
        WorldChunk chunk = chunks.get(key);
        if (chunk == null) {
            if (!alive) return;
            chunk = new WorldChunk();
            chunks.put(key, chunk);
        }
        chunk.setCell(localX, localY, alive);
        if (chunk.isEmpty()) {
            chunks.remove(key);
        }
    }

    public boolean getCell(long x, long y) {
        if (toroidal) {
            x = (x % width + width) % width;
            y = (y % height + height) % height;
        }
        long chunkX = Math.floorDiv(x, WorldChunk.CHUNK_SIZE);
        long chunkY = Math.floorDiv(y, WorldChunk.CHUNK_SIZE);
        int localX = (int) (x - chunkX * WorldChunk.CHUNK_SIZE);
        int localY = (int) (y - chunkY * WorldChunk.CHUNK_SIZE);

        long key = getChunkKey(chunkX, chunkY);
        WorldChunk chunk = chunks.get(key);
        return chunk != null && chunk.getCell(localX, localY);
    }

    public Set<Long> getActiveChunkKeys() {
        return chunks.keySet();
    }

    public WorldChunk getChunk(long key) {
        return chunks.get(key);
    }

    private long getChunkKey(long chunkX, long chunkY) {
        return (chunkX << 32) | (chunkY & 0xffffffffL);
    }

    public static long getChunkX(long key) {
        return key >> 32;
    }

    public static long getChunkY(long key) {
        return (int) (key & 0xffffffffL);
    }

    public void clear() {
        chunks.clear();
    }
}
