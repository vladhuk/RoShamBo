package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.*;
import com.vladhuk.roshambo.server.models.Account;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;


public class LoginWindowController extends AbstractAuthorizationWindowController implements Initializable {

    private static final File ACCOUNT_FILE =
            new File(Client.DOC_PATH + "account.dat");

    private static final Stage connectionStage = new Stage();

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Button reconnectButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label usernameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label informationLabel;

    @FXML
    private CheckBox rememberBox;

    private Connection connection = Connection.getConnection();
    private WindowManager windowManager = new WindowManager(() -> (Stage) anchorPane.getScene().getWindow());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (!connection.isConnected()) {
            setWindowOnlineStatus(false);
            informationLabel.setText("Couldn't connect to server");
        }

        try {
            loadAccount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private void setWindowOnlineStatus(boolean status) {
        reconnectButton.setVisible(!status);
        passwordField.setDisable(!status);
        createAccountButton.setDisable(!status);
    }

    @FXML
    void reconnect() {
        if (connection.reconnect()) {
            setWindowOnlineStatus(true);
            informationLabel.setText("");
        }
        else {
            informationLabel.setText("Couldn't connect to server");
        }
    }

    private void loadAccount() throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACCOUNT_FILE))) {
            if (!reader.ready()) {
                return;
            }
            usernameField.setText(reader.readLine());
            passwordField.setText(reader.readLine());
            rememberBox.fire();
        } catch (FileNotFoundException e) {
            // File creates automatically
        }
    }

    @FXML
    void setConnection() throws IOException {
        windowManager.loadNewWindow(Client.CONNECTION_WINDOW, connectionStage);
    }

    @FXML
    void createAccount() throws IOException {
        connectionStage.close();
        windowManager.changeWindow(Client.REGISTER_WINDOW);
    }

    @FXML
    void login() throws IOException {
        if (!checkFields()) {
            informationLabel.setText("");
            return;
        }

        Account account = new Account(usernameField.getText(), passwordField.getText());

        if (connection.isConnected()) {
            try {
                if (!isAccountExist(account)) {
                    informationLabel.setText("Invalid username or password");
                    return;
                }
            } catch (DisconnectException e) {
                setWindowOnlineStatus(false);
                informationLabel.setText("Lost connection to server");
                e.printStackTrace();
                return;
            }
        }

        Client.setAccount(account);

        if (rememberBox.isSelected()) {
            saveFields();
        }

        connectionStage.close();
        windowManager.changeWindow(Client.MENU_WINDOW);
    }

    private boolean checkFields() {
        boolean result = true;

        if (!isFieldCorrectly(usernameField, usernameLabel)) {
            result = false;
        }

        if (connection.isConnected() && !isFieldCorrectly(passwordField, passwordLabel)) {
            result = false;
        }

        return result;
    }

    private boolean isAccountExist(Account account) throws DisconnectException {
        connection.sendObject(ServerCommand.LOGIN);
        connection.sendObject(account);

        boolean answer = connection.receiveAnswer();

        return answer;
    }

    private void saveFields() throws IOException {
        try (PrintWriter writer = new PrintWriter(ACCOUNT_FILE)) {
            writer.println(usernameField.getText());
            writer.println(passwordField.getText());
        }
    }

}