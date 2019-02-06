package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.Connection;
import com.vladhuk.roshambo.client.logics.RoShamBo;
import com.vladhuk.roshambo.server.Account;
import com.vladhuk.roshambo.server.DisconnectException;

import java.io.IOException;

public class OnlineGameWindowController extends AbstractGameWindowController {

    @Override
    protected Player addPlayer() {
        return new Player(Client.getAccount());
    }

    @Override
    protected Player addOpponent() {
        Account account = null;

        try {
            account = (Account) Connection.receiveObject();
        } catch (DisconnectException e) {
            showDisconnectAlert();
            try {
                changeWindow(Client.MENU_WINDOW);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return new Player(account);
    }

    @Override
    protected void turn(RoShamBo playersItem) {

    }

    @Override
    void back() throws IOException {
        changeWindow(Client.ROOMS_WINDOW);
    }
}
