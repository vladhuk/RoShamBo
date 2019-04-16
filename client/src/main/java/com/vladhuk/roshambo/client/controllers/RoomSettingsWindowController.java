package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class RoomSettingsWindowController implements WindowController {

    @FXML
    AnchorPane anchorPane;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    private Connection connection = Connection.getConnection();
    private WindowManager windowManager = new WindowManager(() -> (Stage) anchorPane.getScene().getWindow());

    @FXML
    void createRoom() throws IOException {
        try {
            Room room = new Room(titleField.getText(), descriptionField.getText());
            room.addPlayer(Client.getAccount());
            sendRoom(room);
            windowManager.changeWindow(Client.GAME_WINDOW, new OnlineGameWindowController());
        } catch (DisconnectException e) {
            WindowManager.showDisconnectAlert();
            windowManager.changeWindow(Client.MENU_WINDOW);
        }
    }

    private void sendRoom(Room room) throws DisconnectException {
        connection.sendObject(ServerCommand.ADD_ROOM);
        connection.sendObject(room);
    }

    @FXML
    void cancel() throws IOException {
        windowManager.changeWindow(Client.ROOMS_WINDOW);
    }

}
