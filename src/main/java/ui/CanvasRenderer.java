package ui;

import core.Grid;
import core.WorldChunk;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class CanvasRenderer {
    private final Canvas canvas;
    private double zoom = 10.0;
    private double offsetX = 0;
    private double offsetY = 0;
    private boolean showGrid = true;

    public CanvasRenderer(Canvas canvas) {
        this.canvas = canvas;
    }

    public void render(Grid grid) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.web("#1e1e1e"));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (showGrid && zoom > 4) {
            drawGridLines(gc);
        }

        gc.setFill(Color.web("#00ff00")); // Classic green cells
        
        // Calculate visible range
        long startCX = (long) Math.floor(offsetX / (WorldChunk.CHUNK_SIZE * zoom));
        long startCY = (long) Math.floor(offsetY / (WorldChunk.CHUNK_SIZE * zoom));
        long endCX = (long) Math.ceil((offsetX + canvas.getWidth()) / (WorldChunk.CHUNK_SIZE * zoom));
        long endCY = (long) Math.ceil((offsetY + canvas.getHeight()) / (WorldChunk.CHUNK_SIZE * zoom));

        for (long cx = startCX; cx <= endCX; cx++) {
            for (long cy = startCY; cy <= endCY; cy++) {
                WorldChunk chunk = grid.getChunk(getChunkKey(cx, cy));
                if (chunk != null) {
                    drawChunk(gc, chunk, cx, cy);
                }
            }
        }
    }

    private void drawChunk(GraphicsContext gc, WorldChunk chunk, long cx, long cy) {
        double chunkStartX = cx * WorldChunk.CHUNK_SIZE * zoom - offsetX;
        double chunkStartY = cy * WorldChunk.CHUNK_SIZE * zoom - offsetY;

        for (int x = 0; x < WorldChunk.CHUNK_SIZE; x++) {
            for (int y = 0; y < WorldChunk.CHUNK_SIZE; y++) {
                if (chunk.getCell(x, y)) {
                    gc.fillRect(chunkStartX + x * zoom, chunkStartY + y * zoom, zoom - 1, zoom - 1);
                }
            }
        }
    }

    private void drawGridLines(GraphicsContext gc) {
        gc.setStroke(Color.web("#333333"));
        gc.setLineWidth(0.5);

        double startX = - (offsetX % zoom);
        for (double x = startX; x < canvas.getWidth(); x += zoom) {
            gc.strokeLine(x, 0, x, canvas.getHeight());
        }

        double startY = - (offsetY % zoom);
        for (double y = startY; y < canvas.getHeight(); y += zoom) {
            gc.strokeLine(0, y, canvas.getWidth(), y);
        }
    }

    private long getChunkKey(long chunkX, long chunkY) {
        return (chunkX << 32) | (chunkY & 0xffffffffL);
    }

    public void setZoom(double zoom) {
        this.zoom = Math.max(1.0, Math.min(zoom, 50.0));
    }

    public double getZoom() {
        return zoom;
    }

    public void pan(double dx, double dy) {
        offsetX -= dx;
        offsetY -= dy;
    }

    public void setShowGrid(boolean showGrid) {
        this.showGrid = showGrid;
    }

    public long screenToGridX(double screenX) {
        return (long) Math.floor((screenX + offsetX) / zoom);
    }

    public long screenToGridY(double screenY) {
        return (long) Math.floor((screenY + offsetY) / zoom);
    }
}
