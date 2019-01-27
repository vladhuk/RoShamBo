package com.vladhuk.client;

import com.vladhuk.client.controllers.WindowController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


public class Main extends Application {

    public static final WindowController.Window LOGIN_WINDOW =
            new WindowController.Window("RoShamBo -- Log in", "view/LoginWindow.fxml");
    public static final WindowController.Window MENU_WINDOW =
            new WindowController.Window("RoShamBo -- Menu", "view/MenuWindow.fxml");
    public static final WindowController.Window SINGLEPLAYER_WINDOW =
            new WindowController.Window("RoShamBo -- Singleplayer", "view/gameWindow.fxml");

    public static final Player PLAYER = new Player();
    public static final Player OPPONENT = new Player();

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
