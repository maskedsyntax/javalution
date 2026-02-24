package core;

public class WorldChunk {
    public static final int CHUNK_SIZE = 64;
    private final boolean[][] cells;
    private int liveCells = 0;

    public WorldChunk() {
        this.cells = new boolean[CHUNK_SIZE][CHUNK_SIZE];
    }

    public void setCell(int x, int y, boolean alive) {
        if (cells[x][y] != alive) {
            cells[x][y] = alive;
            liveCells += alive ? 1 : -1;
        }
    }

    public boolean getCell(int x, int y) {
        return cells[x][y];
    }

    public boolean isEmpty() {
        return liveCells == 0;
    }

    public int getLiveCells() {
        return liveCells;
    }
}
