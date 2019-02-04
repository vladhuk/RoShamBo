package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.server.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsWindowController extends AbstractWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label usersLabel;

    @FXML
    private ListView<Room> listView;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void addRoom() throws IOException {
        changeWindow(Client.ROOM_SETTINGS_WINDOW);
    }

    @FXML
    void update() throws IOException {
        try {
            usersLabel.setText(String.valueOf(getUsersNumber()));
            loadRoomsList();
        } catch (DisconnectException e) {
            showDisconnectAlert();
            changeWindow(Client.MENU_WINDOW);
        }
    }

    private int getUsersNumber() throws DisconnectException {
        Connection.sendObject(ServerCommand.USERS_NUMBER);
        return Connection.receiveInteger();
    }

    private void loadRoomsList() throws DisconnectException {
        Connection.sendObject(ServerCommand.ROOMS_LIST);
        List list = (List) Connection.receiveObject();
        ObservableList<Room> rooms = FXCollections.observableList(list);
        listView.setItems(rooms);
    }

    @FXML
    void back() throws IOException {
        changeWindow(Client.MENU_WINDOW);
    }

}
