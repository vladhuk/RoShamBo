package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.model.Account;
import com.vladhuk.roshambo.server.util.DisconnectException;
import com.vladhuk.roshambo.server.util.ServerCommand;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;


public class RegisterWindowController extends AbstractAuthorizationWindowController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button reconnectButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label informationLabel;

    private Connection connection = Connection.getConnection();
    private WindowManager windowManager = new WindowManager(() -> (Stage) anchorPane.getScene().getWindow());

    @FXML
    void reconnect() {
        if (connection.reconnect()) {
            reconnectButton.setVisible(false);
            informationLabel.setText("");
        }
        else {
            informationLabel.setText("Couldn't connect to server");
        }
    }

    @FXML
    void accept() throws IOException {
        if (!connection.isConnected()) {
            return;
        }

        if (!checkFields()) {
            informationLabel.setText("");
            return;
        }

        Account account = new Account(usernameField.getText(), passwordField.getText());

        try {
            if (!sendAccountToServer(account)) {
                informationLabel.setText("Account already exists");
                return;
            }
        } catch (DisconnectException e) {
            reconnectButton.setVisible(true);
            informationLabel.setText("Lost connection to server");
            return;
        }

        Client.setAccount(account);
        windowManager.changeWindow(Client.MENU_WINDOW);
    }

    private boolean checkFields() {
        return isFieldCorrectly(usernameField, usernameLabel) & isFieldCorrectly(passwordField, passwordLabel)
                & isConfirmed();
    }

    private boolean isConfirmed() {
        ObservableList<String> styles = confirmField.getStyleClass();
        if (!styles.contains(ERROR_STYLE)) {
            styles.add(ERROR_STYLE);
        }

        if (!passwordField.getText().equals(confirmField.getText())) {
            return false;
        }

        styles.remove(ERROR_STYLE);

        return true;
    }

    private boolean sendAccountToServer(Account account) throws DisconnectException {
        connection.sendObject(ServerCommand.CREATE_ACCOUNT);
        connection.sendObject(account);
        return connection.receiveAnswer();
    }

    @FXML
    void cancel() throws IOException {
        windowManager.changeWindow(Client.LOGIN_WINDOW);
    }
}
