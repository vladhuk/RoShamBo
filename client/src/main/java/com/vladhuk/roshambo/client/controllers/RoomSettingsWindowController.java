package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.server.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class RoomSettingsWindowController extends AbstractWindowController {

    @FXML
    AnchorPane anchorPane;

    @FXML
    private TextField titleField;

    @FXML
    private TextField descriptionField;

    private Connection connection = Connection.getConnection();

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @FXML
    void createRoom() throws IOException {
        try {
            Room room = new Room(titleField.getText(), descriptionField.getText());
            room.addPlayer(Client.getAccount());
            sendRoom(room);
            changeWindow(Client.GAME_WINDOW, new OnlineGameWindowController());
        } catch (DisconnectException e) {
            showDisconnectAlert();
            changeWindow(Client.MENU_WINDOW);
        }
    }

    private void sendRoom(Room room) throws DisconnectException {
        connection.sendObject(ServerCommand.ADD_ROOM);
        connection.sendObject(room);
    }

    @FXML
    void cancel() throws IOException {
        changeWindow(Client.ROOMS_WINDOW);
    }

}
