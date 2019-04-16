package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.util.DisconnectException;
import com.vladhuk.roshambo.server.model.Room;
import com.vladhuk.roshambo.server.util.ServerCommand;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class RoomsWindowController implements Initializable, WindowController {

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

    private Connection connection = Connection.getConnection();
    private WindowManager windowManager = new WindowManager(() -> (Stage) anchorPane.getScene().getWindow());

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
        tableView.setRowFactory(tv -> {
            TableRow<Room> row = new TableRow<>();

            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    enterRoom(row.getItem());
                }
            });

            return row;
        });
    }

    private void enterRoom(Room room) {
        try {
            if (sendRoom(room)) {
                windowManager.changeWindow(Client.GAME_WINDOW, new OnlineGameWindowController());
            } else {
                WindowManager.showAlert("The room is is no longer available",
                                        "Choose another room from the list");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean sendRoom(Room room) throws IOException {
        boolean answer = false;

        try {
            connection.sendObject(ServerCommand.ENTER_ROOM);
            connection.sendObject(room);

            answer = connection.receiveAnswer();
        } catch (DisconnectException e) {
            WindowManager.showDisconnectAlert();
            windowManager.changeWindow(Client.MENU_WINDOW);
        }

        return answer;
    }

    @FXML
    void update() throws IOException {
        try {
            usersLabel.setText(String.valueOf(getUsersNumber()));
            loadRoomsList();
        } catch (DisconnectException e) {
            WindowManager.showDisconnectAlert();
            windowManager.changeWindow(Client.MENU_WINDOW);
        }
    }

    private int getUsersNumber() throws DisconnectException {
        connection.sendObject(ServerCommand.USERS_NUMBER);
        return connection.receiveInteger();
    }

    private void loadRoomsList() throws DisconnectException {
        connection.sendObject(ServerCommand.ROOMS_LIST);

        List list = (List) connection.receiveObject();
        ObservableList rooms = FXCollections.observableList(list);

        tableView.setItems(rooms);
    }

    @FXML
    void addRoom() throws IOException {
        windowManager.changeWindow(Client.ROOM_SETTINGS_WINDOW);
    }

    @FXML
    void back() throws IOException {
        windowManager.changeWindow(Client.MENU_WINDOW);
    }

}
