package ui;

import core.RLEParser;
import core.RLEWriter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;

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
        clearBtn.setOnAction(e -> {
            controller.getEngine().getCurrentGrid().clear();
            controller.getEngine().resetGeneration();
        });

        Button randomBtn = new Button("Random");
        randomBtn.setOnAction(e -> randomizeGrid());

        Button loadBtn = new Button("Load RLE");
        loadBtn.setOnAction(e -> loadRLE());

        Button saveBtn = new Button("Save RLE");
        saveBtn.setOnAction(e -> saveRLE());

        Button aboutBtn = new Button("About");
        aboutBtn.setOnAction(e -> showAbout());

        CheckBox toroidalBox = new CheckBox("Wrap");
        toroidalBox.setOnAction(e -> controller.getEngine().getCurrentGrid().setToroidal(toroidalBox.isSelected()));

        Slider speedSlider = new Slider(1, 1000, 100);
        speedSlider.setPrefWidth(200);
        speedSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controller.getEngine().setSpeed(1001 - newVal.intValue());
        });

        this.getChildren().addAll(
                playPauseBtn, stepBtn, clearBtn, randomBtn,
                loadBtn, saveBtn, aboutBtn,
                new Label("Toroidal:"), toroidalBox,
                new Label("Speed:"), speedSlider
        );
    }

    private void loadRLE() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open RLE File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE Patterns", "*.rle"));
        File file = fileChooser.showOpenDialog(getScene().getWindow());
        if (file != null) {
            try {
                controller.getEngine().getCurrentGrid().clear();
                controller.getEngine().resetGeneration();
                RLEParser.load(file.getAbsolutePath(), controller.getEngine().getCurrentGrid());
            } catch (IOException e) {
                showError("Load Error", "Could not load RLE file: " + e.getMessage());
            }
        }
    }

    private void saveRLE() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save RLE File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RLE Patterns", "*.rle"));
        fileChooser.setInitialFileName("pattern.rle");
        File file = fileChooser.showSaveDialog(getScene().getWindow());
        if (file != null) {
            try {
                RLEWriter.save(file.getAbsolutePath(), controller.getEngine().getCurrentGrid());
            } catch (IOException e) {
                showError("Save Error", "Could not save RLE file: " + e.getMessage());
            }
        }
    }

    private void showAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About Javalution");
        alert.setHeaderText("Javalution v1.0.1");
        alert.setContentText("A high-performance Conway’s Game of Life simulator.\n\n" +
                "Built with Java, JavaFX, and Gradle.\n" +
                "GitHub: https://github.com/maskedsyntax/javalution\n\n" +
                "Developed by: maskedsyntax");
        alert.showAndWait();
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void randomizeGrid() {
        core.Grid grid = controller.getEngine().getCurrentGrid();
        grid.clear();
        controller.getEngine().resetGeneration();
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
