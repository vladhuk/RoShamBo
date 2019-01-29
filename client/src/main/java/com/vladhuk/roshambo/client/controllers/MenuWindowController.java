package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuWindowController extends AbstractWindowController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label helloLabel;

    @Override
    public Stage getStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String nickname = Client.ACCOUNT.getNickname();
        helloLabel.setText("Hello, " + nickname + "!");
    }

    @FXML
    void confirmExit(ActionEvent event) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit");
        alert.setHeaderText("Where do you want to exit?");

        ButtonType toLoginButton = new ButtonType("Change account");
        ButtonType toDesktopButton = new ButtonType("To desktop");
        ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(toLoginButton, toDesktopButton, cancelButton);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == toLoginButton) {
            changeWindow(Client.LOGIN_WINDOW);
        } else if (result.get() == toDesktopButton) {
            Stage currentStage = getStage();
            currentStage.close();
        }
    }

    @FXML
    void openSingleplayer(ActionEvent event) throws IOException {
        changeWindow(Client.SINGLEPLAYER_WINDOW);
    }

}
