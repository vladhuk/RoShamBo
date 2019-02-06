package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.client.Window;
import com.vladhuk.roshambo.server.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class RoomsWindowController extends AbstractWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label usersLabel;

    @FXML
    private TableView<Room> tableView;

    @FXML
    private TableColumn<Room, String> titleColumn;

    @FXML
    private TableColumn<Room, String> descriptionColumn;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        createColumns();
        addTableListener();

        try {
            update();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    private void addTableListener() {
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            enterRoom(newValue);
        });
    }

    private void enterRoom(Room room) {
        try {
            if (sendRoom(room)) {
                changeWindow(Client.GAME_WINDOW, new OnlineGameWindowController());
            }
            else {
                showAlert("The room is is no longer available", "Choose another room from the list");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean sendRoom(Room room) throws IOException {
        boolean answer = false;

        try {
            Connection.sendObject(ServerCommand.ENTER_ROOM);
            Connection.sendObject(room);

            answer = Connection.receiveAnswer();
        } catch (DisconnectException e) {
            showDisconnectAlert();
            changeWindow(Client.MENU_WINDOW);
        }

        return answer;
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
        ObservableList rooms = FXCollections.observableList(list);

        tableView.setItems(rooms);
    }

    @FXML
    void addRoom() throws IOException {
        changeWindow(Client.ROOM_SETTINGS_WINDOW);
    }

    @FXML
    void back() throws IOException {
        changeWindow(Client.MENU_WINDOW);
    }

}
