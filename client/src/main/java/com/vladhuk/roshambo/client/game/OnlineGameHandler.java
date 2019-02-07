package com.vladhuk.roshambo.client.game;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.client.controllers.AbstractGameWindowController;
import com.vladhuk.roshambo.client.controllers.OnlineGameWindowController;
import com.vladhuk.roshambo.client.game.logics.RoShamBo;
import com.vladhuk.roshambo.server.*;
import javafx.application.Platform;

import java.io.IOException;

public class OnlineGameHandler implements Runnable {

    private OnlineGameWindowController controller;
    private AbstractGameWindowController.Player opponent;

    public OnlineGameHandler(OnlineGameWindowController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                ServerCommand command = (ServerCommand) Connection.receiveObject();

                switch (command) {
                    case NEW_OPPONENT:
                        setOpponent();
                        break;
                    case ITEM:
                        setItem();
                        break;
                }
            }
        } catch (DisconnectException e) {
            disconnect();
        }
    }

    private void setOpponent() throws DisconnectException {
        Account account = (Account) Connection.receiveObject();
        Platform.runLater(() -> controller.setOpponent(account));
        opponent = controller.getOpponent();
        controller.setTurnable(true);
    }

    private void setItem() throws DisconnectException {
        String stringItem = (String) Connection.receiveObject();
        RoShamBo item = RoShamBo.valueOf(stringItem);

        Platform.runLater(() -> {
            opponent.setItem(item);
            controller.setInfo("Waiting for your turn...");
            controller.addCheckingImage(opponent.getImageView());
        });
    }

    private void disconnect() {
        Platform.runLater(() -> {
            controller.showDisconnectAlert();
            try {
                controller.changeWindow(Client.MENU_WINDOW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
