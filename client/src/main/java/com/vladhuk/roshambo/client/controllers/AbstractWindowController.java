package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractWindowController {

    protected void changeWindow(Window window) throws IOException {
        Stage currentStage = getCurrentStage();
        currentStage.close();

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle(window.NAME);
        currentStage.show();
    }

    protected abstract Stage getCurrentStage();

    protected void newWindow(Window window, Stage newStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        Scene scene = new Scene(root);
        newStage.setScene(scene);
        newStage.setTitle(window.NAME);
        newStage.show();
    }

}
