package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.util.Connection;
import com.vladhuk.roshambo.client.handler.OnlineGameHandler;
import com.vladhuk.roshambo.client.game.RoShamBo;
import com.vladhuk.roshambo.client.util.WindowManager;
import com.vladhuk.roshambo.server.DisconnectException;
import com.vladhuk.roshambo.server.ServerCommand;
import javafx.application.Platform;
import javafx.fxml.Initializable;

import java.io.IOException;

public class OnlineGameWindowController extends AbstractGameWindowController implements Initializable {

    private Thread gameHandler = new Thread(new OnlineGameHandler(this));
    private Thread waitForOpponentThread;
    private volatile boolean isTurnable = false;
    private Connection connection = Connection.getConnection();

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
            connection.sendObject(ServerCommand.ITEM);
            connection.sendObject(playersItem.name());

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
        WindowManager.showDisconnectAlert();

        try {
            getWindowManager().changeWindow(Client.MENU_WINDOW);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    void back() throws IOException {
        leaveRoom();
        getWindowManager().changeWindow(Client.ROOMS_WINDOW);
    }

    private void leaveRoom() {
        try {
            connection.sendObject(ServerCommand.LEAVE_ROOM);
            gameHandler.interrupt();
        } catch (DisconnectException e) {
            disconnect();
        }
    }


}
