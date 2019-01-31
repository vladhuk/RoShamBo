package com.vladhuk.roshambo.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;


public class Client extends Application {

    public static final String DOC_PATH = System.getProperty("user.home") + "/Documents/Roshambo/";
    public static final Window LOGIN_WINDOW = new Window("RoShamBo -- Log in", "view/LoginWindow.fxml");
    public static final Window MENU_WINDOW = new Window("RoShamBo -- Menu", "view/MenuWindow.fxml");
    public static final Window SINGLEPLAYER_WINDOW = new Window("RoShamBo -- Singleplayer", "view/GameWindow.fxml");
    public static final Window REGISTER_WINDOW = new Window("Roshambo -- Registry", "view/RegisterWindow.fxml");
    public static final Window CONNECTION_WINDOW = new Window("Roshambo -- Change server", "view/ConnectionWindow.fxml");
    public static final Account ACCOUNT = new Account();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        createConfigDirectory();

        String ip = Connection.loadIpFromFile();
        Connection.connect(ip);

        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(LOGIN_WINDOW.PATH_TO_FXML));
        Scene loginScene = new Scene(root);
        stage.setScene(loginScene);
        stage.setTitle(LOGIN_WINDOW.NAME);
        stage.getIcons().add(new Image("images/RPS.png"));
        stage.show();
    }

    private static void createConfigDirectory() {
        File directory = new File(DOC_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

}
