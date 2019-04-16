package com.vladhuk.roshambo.client.util;

import com.vladhuk.roshambo.client.controllers.WindowController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.Supplier;

public class WindowManager {

    private FXMLLoader fxmlLoader;
    private Supplier<Stage> stageSupplier;
    private Stage currentStage;

    public WindowManager() {}

    public WindowManager(Supplier<Stage> stageSupplier) {
        this.stageSupplier = stageSupplier;
    }

    public WindowManager(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public Stage getCurrentStage() {
        return currentStage == null ? stageSupplier.get() : currentStage;
    }

    public void setCurrentStage(Stage currentStage) {
        this.currentStage = currentStage;
    }

    public void changeWindow(Window window) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(window.PATH_TO_FXML));

        getCurrentStage().close();
        loadAndShowScene(window, getCurrentStage());
    }

    public void changeWindow(Window window, WindowController controller) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        fxmlLoader.setController(controller);

        getCurrentStage().close();
        loadAndShowScene(window, getCurrentStage());
    }

    public void loadNewWindow(Window window, Stage newStage) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(window.PATH_TO_FXML));

        loadAndShowScene(window, newStage);
    }

    private void loadAndShowScene(Window window, Stage stage) throws IOException {
        Parent root = fxmlLoader.load();

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(window.NAME);
        stage.show();
    }

    public static void showAlert(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Roshambo -- Error");
        alert.setHeaderText(title);
        alert.setContentText(description);
        alert.showAndWait();
    }

    public static void showDisconnectAlert() {
        showAlert("Lost connection to server", "The game will continue in offline mode.");
    }
}
