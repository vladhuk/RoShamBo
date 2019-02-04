package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.server.Account;
import com.vladhuk.roshambo.server.DisconnectException;
import com.vladhuk.roshambo.server.ServerCommand;
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
    private TextField nicknameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmField;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label informationLabel;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @FXML
    void reconnect() {
        if (Connection.reconnect()) {
            reconnectButton.setVisible(false);
            informationLabel.setText("");
        }
        else {
            informationLabel.setText("Couldn't connect to server");
        }
    }

    @FXML
    void accept() throws IOException {
        if (!Connection.isConnected()) {
            return;
        }

        if (!checkFields()) {
            informationLabel.setText("");
            return;
        }

        Account account = new Account(nicknameField.getText(), passwordField.getText());

        try {
            if (!sendAccountToServer(account)) {
                informationLabel.setText("Account already exists");
            }
        } catch (DisconnectException e) {
            reconnectButton.setVisible(true);
            informationLabel.setText("Lost connection to server");
            return;
        }

        Client.setAccount(account);
        changeWindow(Client.MENU_WINDOW);
    }

    private boolean checkFields() {
        return isFieldCorrectly(nicknameField, nicknameLabel) & isFieldCorrectly(passwordField, passwordLabel)
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
        Connection.sendObject(ServerCommand.CREATE_ACCOUNT);
        Connection.sendObject(account);
        return Connection.receiveAnswer();
    }

    @FXML
    void cancel() throws IOException {
        changeWindow(Client.LOGIN_WINDOW);
    }

}
