package com.vladhuk.roshambo.client.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public abstract class AbstractWindowController {

    protected void changeWindow(Window window) throws IOException {
        Stage currentStage = getStage();
        currentStage.close();

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(window.PATH_TO_FXML));
        Scene menuScene = new Scene(root);
        currentStage.setScene(menuScene);
        currentStage.setTitle(window.NAME);
        currentStage.show();
    }

    protected abstract Stage getStage();

    public static class Window {

        public final String NAME;
        public final String PATH_TO_FXML;

        public Window(String name, String pathToFxml) {
            this.NAME = name;
            this.PATH_TO_FXML = pathToFxml;
        }
    }

}
