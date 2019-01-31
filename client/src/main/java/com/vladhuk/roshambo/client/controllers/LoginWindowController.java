package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginWindowController extends AbstractWindowController implements Initializable {

    private static final File ACCOUNT_FILE =
            new File(Client.DOC_PATH + "account.dat");

    private static final Stage connectionStage = new Stage();

    private Socket socket;

    @FXML
    private TextField nicknameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private CheckBox rememberBox;

    @FXML
    private Label nicknameLabel;

    @FXML
    private Label passwordLabel;

    @FXML
    private Label informationLabel;

    @FXML
    private Button connectionButton;

    @FXML
    private Button reconnectButton;

    @FXML
    private Button createAccountButton;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        connectionButton.setTooltip(new Tooltip("Change server"));
        reconnectButton.setTooltip(new Tooltip("Reconnect"));

        if (!Connection.isConnected()) {
            reconnectButton.setVisible(true);
            passwordField.setDisable(true);
            createAccountButton.setDisable(true);
            informationLabel.setText("Couldn't connect to server");
        }

        try {
            loadAccount();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void reconnect() {
        if (Connection.reconnect()) {
            reconnectButton.setVisible(false);
            passwordField.setDisable(false);
            createAccountButton.setDisable(false);
            informationLabel.setText("");
        } else {
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
        if (!isFieldCorrectly(nicknameField, nicknameLabel)) {
            return;
        }

        connectionStage.close();

        Client.ACCOUNT.setNickname(nicknameField.getText());
        saveAccount();

        changeWindow(Client.MENU_WINDOW);
    }

    private boolean isFieldCorrectly(TextField checkedField, Label information) {

        String text = checkedField.getText();

        if (text.replaceAll(" ", "").isEmpty()) {
            information.setText("Empty field");
            information.setTooltip(null);
            return false;
        }

        Matcher matcher = Pattern.compile("\\W").matcher(text);
        if (matcher.find()) {
            information.setText("Field mustn't contain cyrillic, spaces and special symbols.");
            return false;
        }

        information.setText("");

        return true;
    }

    private void saveAccount() throws IOException {
        try (PrintWriter writer = new PrintWriter(ACCOUNT_FILE)) {
            if (rememberBox.isSelected()) {
                writer.println(nicknameField.getText());
                writer.println(passwordField.getText());
            }
        }
    }

}