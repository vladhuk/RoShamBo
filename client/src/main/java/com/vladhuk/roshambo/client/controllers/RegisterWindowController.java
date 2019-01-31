package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterWindowController extends AbstractWindowController {

    @FXML
    AnchorPane anchorPane;

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
    private PasswordField confirmPassworField;

    @FXML
    private Label confirmLabel;

    @FXML
    void accept(ActionEvent event) {
        // TODO
    }

    @FXML
    void cancel(ActionEvent event) throws IOException {
        changeWindow(Client.LOGIN_WINDOW);
    }


    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }
}
