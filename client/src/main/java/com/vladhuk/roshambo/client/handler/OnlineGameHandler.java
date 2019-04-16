package com.vladhuk.roshambo.client.handler;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.controllers.AbstractGameWindowController;
import com.vladhuk.roshambo.client.controllers.OnlineGameWindowController;
import com.vladhuk.roshambo.client.game.RoShamBo;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.*;
import com.vladhuk.roshambo.server.models.Account;
import javafx.application.Platform;

import java.io.IOException;

public class OnlineGameHandler implements Runnable {

    private OnlineGameWindowController controller;
    private AbstractGameWindowController.Player opponent;
    private Connection connection = Connection.getConnection();

    public OnlineGameHandler(OnlineGameWindowController controller) {
        this.controller = controller;
    }

    @Override
    public void run() {
        try {
            while (!Thread.interrupted()) {
                ServerCommand command = (ServerCommand) connection.receiveObject();

                switch (command) {
                    case NEW_OPPONENT:
                        setOpponent();
                        break;
                    case ITEM:
                        setItem();
                        break;
                    case LEAVE_ROOM:
                        deleteOpponent();
                        break;
                    case STOP:
                        Thread.currentThread().interrupt();
                        break;
                }

                Thread.yield();
            }
        } catch (DisconnectException e) {
            disconnect();
        }
    }

    private void setOpponent() throws DisconnectException {
        Account account = (Account) connection.receiveObject();
        Platform.runLater(() -> controller.setOpponent(account));
        opponent = controller.getOpponent();
        controller.setTurnable(true);
    }

    private void setItem() throws DisconnectException {
        String stringItem = (String) connection.receiveObject();
        RoShamBo item = RoShamBo.valueOf(stringItem);

        Platform.runLater(() -> {
            opponent.setItem(item);
            controller.setInfo("Waiting for your turn...");
            controller.addWaitingImage(opponent.getImageView());
        });
    }

    private void deleteOpponent() throws DisconnectException {
        connection.sendObject(ServerCommand.NEW_OPPONENT);

        Platform.runLater(() -> {
            controller.setOpponent(AbstractGameWindowController.NULL_ACCOUNT);
            controller.setTurnable(false);
        });
    }

    private void disconnect() {
        Platform.runLater(() -> {
            WindowManager.showDisconnectAlert();
            try {
                controller.getWindowManager().changeWindow(Client.MENU_WINDOW);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
