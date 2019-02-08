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
    private Thread waitForOpponentThread;
    private volatile boolean isTurnable = false;

    @Override
    public void init() {
        gameHandler.start();

        Platform.runLater(() ->
            getCurrentStage().getScene().getWindow().setOnCloseRequest(e -> {
                if (isThreadRunning(waitForOpponentThread)) {
                    waitForOpponentThread.interrupt();
                }
                leaveRoom();
                Platform.exit();
                System.exit(0);
            })
        );
    }

    private boolean isThreadRunning(Thread thread) {
        return (thread != null) && thread.isAlive();
    }

    @Override
    public void turn(RoShamBo playersItem) {
        if (!isTurnable()) {
            return;
        }
        setTurnable(false);

        getPlayer().setItem(playersItem);

        updatePlayersItemImage();
        addWaitingImage(getOpponent().getImageView());
        setInfo("Waiting for opponent...");

        try {
            Connection.sendObject(ServerCommand.ITEM);
            Connection.sendObject(playersItem.name());

            waitForOpponent();
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

    private void waitForOpponent() {
        waitForOpponentThread = new Thread(() -> {
            while (getOpponent().getItem() == null) {
                if (isOpponentDeleted() || Thread.interrupted()) {
                    return;
                }
                Thread.yield();
            }

            Platform.runLater(() -> {
                updateOpponentsItemImage();
                showResult();
            });

            setTurnable(true);
        });
        waitForOpponentThread.setDaemon(true);
        waitForOpponentThread.start();
    }

    private boolean isOpponentDeleted() {
        return getOpponent().equals(NULL_ACCOUNT);
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
        leaveRoom();
        changeWindow(Client.ROOMS_WINDOW);
    }

    private void leaveRoom() {
        try {
            Connection.sendObject(ServerCommand.LEAVE_ROOM);
            gameHandler.interrupt();
        } catch (DisconnectException e) {
            disconnect();
        }
    }


}
