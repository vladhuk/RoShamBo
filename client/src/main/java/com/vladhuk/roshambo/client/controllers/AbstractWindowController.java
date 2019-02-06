package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractWindowController {

    private static FXMLLoader fxmlLoader;

    protected void changeWindow(Window window) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(window.PATH_TO_FXML));

        loadScene(window);
    }

    protected void changeWindow(Window window, AbstractWindowController controller) throws IOException {
        fxmlLoader = new FXMLLoader(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        fxmlLoader.setController(controller);

        loadScene(window);
    }

    private void loadScene(Window window) throws IOException {
        Parent root = fxmlLoader.load();

        Stage currentStage = getCurrentStage();
        currentStage.close();

        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(window.NAME);
        currentStage.show();
    }

    protected abstract Stage getCurrentStage();

    public static void newWindow(Window window, Stage newStage) throws IOException {
        fxmlLoader = new FXMLLoader(AbstractWindowController.class.getClassLoader().getResource(window.PATH_TO_FXML));

        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.setTitle(window.NAME);
        newStage.show();
    }

    protected void showAlert(String title, String description) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Roshambo -- Error");
        alert.setHeaderText(title);
        alert.setContentText(description);
        alert.showAndWait();
    }

    protected void showDisconnectAlert() {
        showAlert("Lost connection to server", "The game will continue in offline mode.");
    }

}
