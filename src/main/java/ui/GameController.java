package ui;

import core.Grid;
import core.SimulationEngine;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

public class GameController {
    private final SimulationEngine engine;
    private final CanvasRenderer renderer;
    private final Canvas canvas;
    private final BorderPane root;

    public GameController(BorderPane root) {
        this.root = root;
        this.engine = new SimulationEngine(new Grid());
        this.canvas = new Canvas();
        this.renderer = new CanvasRenderer(canvas);

        setupCanvas();
        setupControls();
        setupInteraction();
        startRenderingLoop();
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
        canvas.setOnMouseClicked(event -> {
            long gridX = renderer.screenToGridX(event.getX());
            long gridY = renderer.screenToGridY(event.getY());
            
            if (event.getButton() == javafx.scene.input.MouseButton.PRIMARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, true);
            } else if (event.getButton() == javafx.scene.input.MouseButton.SECONDARY) {
                engine.getCurrentGrid().setCell(gridX, gridY, false);
            }
            renderer.render(engine.getCurrentGrid());
        });

        canvas.setOnScroll(event -> {
            double oldZoom = renderer.getZoom();
            renderer.setZoom(oldZoom * (event.getDeltaY() > 0 ? 1.1 : 0.9));
            renderer.render(engine.getCurrentGrid());
        });

        // Mouse drag panning
        final double[] lastX = new double[1];
        final double[] lastY = new double[1];
        canvas.setOnMousePressed(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.MIDDLE) {
                lastX[0] = event.getX();
                lastY[0] = event.getY();
            }
        });

        canvas.setOnMouseDragged(event -> {
            if (event.getButton() == javafx.scene.input.MouseButton.MIDDLE) {
                renderer.pan(event.getX() - lastX[0], event.getY() - lastY[0]);
                lastX[0] = event.getX();
                lastY[0] = event.getY();
                renderer.render(engine.getCurrentGrid());
            }
        });
    }

    private void startRenderingLoop() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {
                renderer.render(engine.getCurrentGrid());
            }
        }.start();
    }

    public SimulationEngine getEngine() {
        return engine;
    }
}
