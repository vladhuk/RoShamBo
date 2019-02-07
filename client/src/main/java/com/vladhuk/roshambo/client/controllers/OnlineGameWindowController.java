package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.client.game.OnlineGameHandler;
import com.vladhuk.roshambo.client.game.logics.RoShamBo;
import com.vladhuk.roshambo.server.DisconnectException;
import com.vladhuk.roshambo.server.ServerCommand;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.io.IOException;

public class OnlineGameWindowController extends AbstractGameWindowController implements Initializable {

    private Thread gameHandler = new Thread(new OnlineGameHandler(this));
    private volatile boolean isTurnable = false;

    @Override
    public void init() {
        gameHandler.setDaemon(true);
        gameHandler.start();
    }

    @Override
    public void turn(RoShamBo playersItem) {
        if (!isTurnable()) {
            return;
        }
        setTurnable(false);

        getPlayer().setItem(playersItem);

        updatePlayersItemImage();
        setInfo("Waiting for opponent...");

        try {
            Connection.sendObject(ServerCommand.ITEM);
            Connection.sendObject(playersItem.name());

            checkForOpponent();
        } catch (DisconnectException e) {
            disconnect();
        }
    }

    public boolean isTurnable() {
        return isTurnable;
    }

    public void setTurnable(boolean turnable) {
        isTurnable = turnable;
    }

    private void checkForOpponent() {
        Thread thread = new Thread(() -> {
            while (getOpponent().getItem() == null) {
                Thread.yield();
            }
            Platform.runLater(() -> {
                updateOpponentsItemImage();
                showResult();
            });

            setTurnable(true);
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void disconnect() {
        gameHandler.interrupt();
        showDisconnectAlert();

        try {
            changeWindow(Client.MENU_WINDOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void back() throws IOException {
        changeWindow(Client.ROOMS_WINDOW);
    }
}
