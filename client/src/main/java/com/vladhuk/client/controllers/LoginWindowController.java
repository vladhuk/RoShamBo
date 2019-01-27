package com.vladhuk.client.controllers;

import com.vladhuk.client.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class LoginWindowController extends WindowController implements Initializable {

    private static final File account = new File(System.getProperty("user.home") + "/Documents/account.dat");

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

    @Override
    protected Stage getStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            loadAccount();
        } catch (FileNotFoundException e) {
            // File creates automatically
        } catch (IOException e) {
            errorAlert();
        }
    }

    private void loadAccount() throws FileNotFoundException, IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(account))) {
            if (!reader.ready()) {
                return;
            }
            nicknameField.setText(reader.readLine());
            passwordField.setText(reader.readLine());
            rememberBox.fire();
        }
    }

    private void errorAlert() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("RoShamBo -- Error");
        alert.setHeaderText("Error");
        alert.setContentText("Error loading file \"account.dat\".");

        alert.showAndWait();
    }

    @FXML
    void login(ActionEvent event) throws IOException {
        if (!isFieldCorrectly(nicknameField, nicknameLabel)) {
            return;
        }

        Main.PLAYER.setName(nicknameField.getText());

        saveAccount();

        changeWindow(Main.MENU_WINDOW);
    }

    private boolean isFieldCorrectly(TextField checkedField, Label information) {

        String text = checkedField.getText();

        if (text.isBlank()) {
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
        try (PrintWriter writer = new PrintWriter(account)) {
            if (rememberBox.isSelected()) {
                writer.println(nicknameField.getText());
                writer.println(passwordField.getText());
            }
        }
    }

}