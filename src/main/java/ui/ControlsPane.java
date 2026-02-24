package ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;

public class ControlsPane extends HBox {
    private final GameController controller;

    public ControlsPane(GameController controller) {
        this.controller = controller;
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setAlignment(Pos.CENTER);
        this.setStyle("-fx-background-color: #2b2b2b;");

        setupControls();
    }

    private void setupControls() {
        ToggleButton playPauseBtn = new ToggleButton("Play");
        playPauseBtn.setOnAction(e -> {
            if (playPauseBtn.isSelected()) {
                controller.getEngine().start();
                playPauseBtn.setText("Pause");
            } else {
                controller.getEngine().stop();
                playPauseBtn.setText("Play");
            }
        });

        Button stepBtn = new Button("Step");
        stepBtn.setOnAction(e -> controller.getEngine().step());

        Button clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> controller.getEngine().getCurrentGrid().clear());

        Button randomBtn = new Button("Random");
        randomBtn.setOnAction(e -> randomizeGrid());

        Slider speedSlider = new Slider(1, 1000, 100);
        speedSlider.setPrefWidth(200);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controller.getEngine().setSpeed(1001 - newVal.intValue());
        });

        this.getChildren().addAll(
                playPauseBtn, stepBtn, clearBtn, randomBtn,
                new Label("Speed:"), speedSlider
        );
    }

    private void randomizeGrid() {
        core.Grid grid = controller.getEngine().getCurrentGrid();
        grid.clear();
        java.util.Random random = new java.util.Random();
        for (int x = 0; x < 100; x++) {
            for (int y = 0; y < 100; y++) {
                if (random.nextDouble() < 0.2) {
                    grid.setCell(x, y, true);
                }
            }
        }
    }
}
