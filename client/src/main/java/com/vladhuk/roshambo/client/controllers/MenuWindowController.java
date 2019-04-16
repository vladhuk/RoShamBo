package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.util.ServerCommand;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuWindowController implements Initializable, WindowController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label helloLabel;

    @FXML
    private Button onlineButton;

    private Connection connection = Connection.getConnection();

    private WindowManager windowManager = new WindowManager(() -> (Stage) anchorPane.getScene().getWindow());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (connection.isConnected()) {
            onlineButton.setDisable(false);
        }

        String username = Client.getAccount().getUsername();
        helloLabel.setText("Hello, " + username + "!");
    }

    @FXML
    void openSingleplayer() throws IOException {
        windowManager.changeWindow(Client.GAME_WINDOW, new SingleplayerWindowController());
    }

    @FXML
    void openOnline() throws IOException {
        windowManager.changeWindow(Client.ROOMS_WINDOW);
    }

    @FXML
    void confirmExit() throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Where do you want to exit?");

        ButtonType toLoginButton = new ButtonType("Change account");
        ButtonType toDesktopButton = new ButtonType("To desktop");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(toLoginButton, toDesktopButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == toLoginButton) {
            if (connection.isConnected()) {
                connection.sendObject(ServerCommand.EXIT);
            }
            windowManager.changeWindow(Client.LOGIN_WINDOW);
        } else if (result.get() == toDesktopButton) {
            windowManager.getCurrentStage().close();
        }
    }

}
