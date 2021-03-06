package com.vladhuk.roshambo.client;

import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.Window;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.model.Account;
import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;


public class Client extends Application {

    private static Account account = new Account();

    public static final String DOC_PATH = System.getProperty("user.home") + "/Documents/Roshambo/";
    public static final Window LOGIN_WINDOW = new Window("RoShamBo -- Log in", "view/LoginWindow.fxml");
    public static final Window MENU_WINDOW = new Window("RoShamBo -- Menu", "view/MenuWindow.fxml");
    public static final Window GAME_WINDOW = new Window("RoShamBo -- Game", "view/GameWindow.fxml");
    public static final Window REGISTER_WINDOW = new Window("Roshambo -- Create account", "view/RegisterWindow.fxml");
    public static final Window CONNECTION_WINDOW = new Window("Roshambo -- Change server", "view/ConnectionWindow.fxml");
    public static final Window ROOMS_WINDOW = new Window("Roshambo -- Rooms", "view/RoomsWindow.fxml");
    public static final Window ROOM_SETTINGS_WINDOW = new Window("Roshambo -- Create room", "view/RoomSettingsWindow.fxml");

    public static Account getAccount() {
        return account;
    }

    public static void setAccount(Account account) {
        Client.account = account;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        createConfigDirectory();

        String ip = Connection.loadIpFromFile();
        Connection.buildConnection(ip);

        stage.getIcons().add(new Image("images/RPS.png"));

        WindowManager windowManager = new WindowManager();
        windowManager.loadNewWindow(LOGIN_WINDOW, stage);
    }

    private static void createConfigDirectory() {
        File directory = new File(DOC_PATH);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

}
