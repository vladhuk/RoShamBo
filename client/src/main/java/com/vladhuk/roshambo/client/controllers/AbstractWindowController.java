package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Window;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractWindowController {

    protected void changeWindow(Window window) throws IOException {
        Stage stage = getStage();
        stage.close();

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(window.NAME);
        stage.show();
    }

    protected abstract Stage getStage();

}
