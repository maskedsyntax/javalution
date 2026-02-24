package ui;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class StatusBar extends HBox {
    private final Label generationLabel = new Label("Generation: 0");
    private final Label populationLabel = new Label("Population: 0");
    private final Label zoomLabel = new Label("Zoom: 1.0x");
    private final Label fpsLabel = new Label("FPS: 0");

    public StatusBar() {
        this.getStyleClass().add("status-bar");
        this.setSpacing(20);
        this.setPadding(new Insets(5, 10, 5, 10));
        this.getChildren().addAll(generationLabel, populationLabel, zoomLabel, fpsLabel);
    }

    public void update(long gen, int pop, double zoom, int fps) {
        generationLabel.setText("Generation: " + gen);
        populationLabel.setText("Population: " + pop);
        zoomLabel.setText(String.format("Zoom: %.1fx", zoom));
        fpsLabel.setText("FPS: " + fps);
    }
}
