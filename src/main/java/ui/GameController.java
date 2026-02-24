package ui;

import core.Grid;
import core.SimulationEngine;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;

public class GameController {
    private final SimulationEngine engine;
    private final CanvasRenderer renderer;
    private final Canvas canvas;
    private final BorderPane root;
    private final StatusBar statusBar;

    private long lastFrameTime = 0;
    private int frameCount = 0;
    private int fps = 0;

    public GameController(BorderPane root) {
        this.root = root;
        this.engine = new SimulationEngine(new Grid());
        this.canvas = new Canvas();
        this.renderer = new CanvasRenderer(canvas);
        this.statusBar = new StatusBar();

        setupCanvas();
        setupControls();
        setupInteraction();
        startRenderingLoop();
        
        root.setBottom(statusBar);
    }

    private void setupCanvas() {
        Pane container = new Pane();
        container.getChildren().add(canvas);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());
        
        canvas.widthProperty().addListener(e -> renderer.render(engine.getCurrentGrid()));
        canvas.heightProperty().addListener(e -> renderer.render(engine.getCurrentGrid()));

        root.setCenter(container);
    }

    private void setupControls() {
        ControlsPane controls = new ControlsPane(this);
        root.setTop(controls);
    }

    private void setupInteraction() {
        canvas.setFocusTraversable(true);
        canvas.setOnMouseClicked(event -> {
            canvas.requestFocus();
            long gridX = renderer.screenToGridX(event.getX());
            long gridY = renderer.screenToGridY(event.getY());
            
            if (event.getButton() == MouseButton.PRIMARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, true);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, false);
            }
            renderer.render(engine.getCurrentGrid());
        });

        canvas.setOnMouseDragged(event -> {
            long gridX = renderer.screenToGridX(event.getX());
            long gridY = renderer.screenToGridY(event.getY());
            
            if (event.getButton() == MouseButton.PRIMARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, true);
            } else if (event.getButton() == MouseButton.SECONDARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, false);
            } else if (event.getButton() == MouseButton.MIDDLE) {
                // Should use the pressed/dragged logic for panning instead
            }
            renderer.render(engine.getCurrentGrid());
        });

        canvas.setOnScroll(event -> {
            double oldZoom = renderer.getZoom();
            double factor = event.getDeltaY() > 0 ? 1.2 : 0.8;
            renderer.setZoom(oldZoom * factor);
            renderer.render(engine.getCurrentGrid());
        });

        // Mouse middle-drag panning
        final double[] lastX = new double[1];
        final double[] lastY = new double[1];
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                lastX[0] = event.getX();
                lastY[0] = event.getY();
            }
        });

        canvas.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getButton() == MouseButton.MIDDLE) {
                renderer.pan(event.getX() - lastX[0], event.getY() - lastY[0]);
                lastX[0] = event.getX();
                lastY[0] = event.getY();
                renderer.render(engine.getCurrentGrid());
            }
        });

        canvas.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.SPACE) {
                if (engine.isRunning()) engine.stop(); else engine.start();
            } else if (event.getCode() == KeyCode.S) {
                engine.step();
            } else if (event.getCode() == KeyCode.C) {
                engine.getCurrentGrid().clear();
                engine.resetGeneration();
            } else if (event.getCode() == KeyCode.R) {
                randomizeGrid();
            } else if (event.getCode() == KeyCode.EQUALS || event.getCode() == KeyCode.PLUS) {
                renderer.setZoom(renderer.getZoom() * 1.2);
            } else if (event.getCode() == KeyCode.MINUS) {
                renderer.setZoom(renderer.getZoom() * 0.8);
            }
        });
    }

    private void randomizeGrid() {
        Grid grid = engine.getCurrentGrid();
        grid.clear();
        engine.resetGeneration();
        java.util.Random random = new java.util.Random();
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                if (random.nextDouble() < 0.2) {
                    grid.setCell(x, y, true);
                }
            }
        }
    }

    private void startRenderingLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (lastFrameTime > 0) {
                    long elapsedNanos = now - lastFrameTime;
                    if (elapsedNanos > 1e9) {
                        fps = frameCount;
                        frameCount = 0;
                        lastFrameTime = now;
                    }
                    frameCount++;
                } else {
                    lastFrameTime = now;
                }

                renderer.render(engine.getCurrentGrid());
                statusBar.update(engine.getGeneration(), engine.getPopulation(), renderer.getZoom(), fps);
            }
        }.start();
    }

    public SimulationEngine getEngine() {
        return engine;
    }
}
