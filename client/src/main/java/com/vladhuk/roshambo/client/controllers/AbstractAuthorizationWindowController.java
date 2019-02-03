package com.vladhuk.roshambo.client.controllers;

import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractAuthorizationWindowController extends AbstractWindowController {

    protected static final String ERROR_STYLE = "error";

    protected boolean isFieldCorrectly(TextField checkedField, Label information) {

        String text = checkedField.getText();

        ObservableList<String> styles = checkedField.getStyleClass();
        if (!styles.contains(ERROR_STYLE)) {
            styles.add(ERROR_STYLE);
        }

        if (text.replaceAll(" ", "").isEmpty()) {
            information.setText("Empty field");
            return false;
        }

        Matcher matcher = Pattern.compile("\\W").matcher(text);
        if (matcher.find()) {
            information.setText("Field mustn't contain cyrillic, spaces and special symbols.");
            return false;
        }

        styles.remove(ERROR_STYLE);

        information.setText("");

        return true;
    }

}
