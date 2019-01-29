package com.vladhuk.roshambo.client;

import com.vladhuk.roshambo.client.controllers.AbstractWindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Client extends Application {

    private static boolean isOnline = false;

    public static final AbstractWindowController.Window LOGIN_WINDOW =
            new AbstractWindowController.Window("RoShamBo -- Log in", "view/LoginWindow.fxml");
    public static final AbstractWindowController.Window MENU_WINDOW =
            new AbstractWindowController.Window("RoShamBo -- Menu", "view/MenuWindow.fxml");
    public static final AbstractWindowController.Window SINGLEPLAYER_WINDOW =
            new AbstractWindowController.Window("RoShamBo -- Singleplayer", "view/gameWindow.fxml");

    public static final Account ACCOUNT = new Account();

    public static final int PORT = 5543;

    public static void setOnline(boolean isServerConnected) {
        Client.isOnline = isServerConnected;
    }

    public static boolean isOnline() {
        return isOnline;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource(LOGIN_WINDOW.PATH_TO_FXML));
        Scene loginScene = new Scene(root);
        stage.setScene(loginScene);
        stage.setTitle(LOGIN_WINDOW.NAME);
        stage.getIcons().add(new Image("images/RPS.png"));
        stage.show();
    }
}
