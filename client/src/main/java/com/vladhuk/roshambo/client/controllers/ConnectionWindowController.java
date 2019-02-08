package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Connection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectionWindowController implements Initializable {

    @FXML
    private TextField ipTextField;

    @FXML
    private Label currentIpLabel;

    @FXML
    private Label informationLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            currentIpLabel.setText("Current ip: " + Connection.getIP());
            ipTextField.setText(Connection.loadIpFromFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void saveIP() {
        String ip = ipTextField.getText();

        if (!isFieldBlank(ip)) {
            Connection.saveIpToFile(ip);
        }
    }

    private boolean isFieldBlank(String text) {
        return text.replaceAll(" ", "").isEmpty();
    }

    @FXML
    void connect() {
        String ip = ipTextField.getText();

        if (isFieldBlank(ip)) {
            informationLabel.setText("");
            return;
        }

        if (Connection.connect(ip)) {
            informationLabel.setTextFill(Color.GREEN);
            currentIpLabel.setText("Current ip: " + ip);
            informationLabel.setText("Successfully connected");
        }
        else {
            informationLabel.setTextFill(Color.RED);
            informationLabel.setText("Could not connect to server");
        }
    }

}