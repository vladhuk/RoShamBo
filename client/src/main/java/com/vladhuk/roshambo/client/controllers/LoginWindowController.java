package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.server.*;
import javafx.fxml.FXML;
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
    private Button connectionButton;

    @FXML
    private Button reconnectButton;

    @FXML
    private Button createAccountButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label informationLabel;

    @FXML
    private CheckBox rememberBox;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectionButton.setTooltip(new Tooltip("Change server"));
        reconnectButton.setTooltip(new Tooltip("Reconnect"));

        if (!Connection.isConnected()) {
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
        if (Connection.reconnect()) {
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
            nicknameField.setText(reader.readLine());
            passwordField.setText(reader.readLine());
            rememberBox.fire();
        } catch (FileNotFoundException e) {
            // File creates automatically
        }
    }

    @FXML
    void setConnection() throws IOException {
        newWindow(Client.CONNECTION_WINDOW, connectionStage);
    }

    @FXML
    void createAccount() throws IOException {
        connectionStage.close();
        changeWindow(Client.REGISTER_WINDOW);
    }

    @FXML
    void login() throws IOException {
        if (!checkFields()) {
            informationLabel.setText("");
            return;
        }

        Account account = new Account(nicknameField.getText(), passwordField.getText());

        if (Connection.isConnected()) {
            try {
                account = loadAccountFromServer(account);
                if (account == null) {
                    informationLabel.setText("Account doesn't exist");
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
        changeWindow(Client.MENU_WINDOW);
    }

    private boolean checkFields() {
        boolean result = true;

        if (!isFieldCorrectly(nicknameField, nicknameLabel)) {
            result = false;
        }

        if (Connection.isConnected() && !isFieldCorrectly(passwordField, passwordLabel)) {
            result = false;
        }

        return result;
    }

    private Account loadAccountFromServer(Account account) throws DisconnectException {
        Connection.sendCommand(ServerCommand.LOGIN);
        Connection.sendAccountID(account.hashCode());

        Account serverAccount = null;

        boolean answer = Connection.receiveAnswer();
        if (answer) {
            serverAccount = Connection.receiveAccount();
        }

        return serverAccount;
    }

    private void saveFields() throws IOException {
        try (PrintWriter writer = new PrintWriter(ACCOUNT_FILE)) {
            writer.println(nicknameField.getText());
            writer.println(passwordField.getText());
        }
    }

}