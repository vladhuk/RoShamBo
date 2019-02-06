package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.logics.RoShamBo;
import com.vladhuk.roshambo.server.Account;

import java.io.IOException;


public class SingleplayerWindowController extends AbstractGameWindowController {

    @Override
    protected Player addPlayer() {
        return new Player(Client.getAccount());
    }

    @Override
    protected Player addOpponent() {
        return new Player(new Account("Bot"));
    }

    @Override
    protected void turn(RoShamBo playersItem) {
        getPlayer().setItem(playersItem);
        getOpponent().setItem(RoShamBo.randomItem());

        showResult();

        updatePlayersItemImage();
        updateOpponentsItemImage();
    }

    @Override
    protected void back() throws IOException {
        changeWindow(Client.MENU_WINDOW);
    }
}
