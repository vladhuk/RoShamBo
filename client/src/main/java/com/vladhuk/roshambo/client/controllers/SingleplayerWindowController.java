package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.game.RoShamBo;
import com.vladhuk.roshambo.server.models.Account;

import java.io.IOException;


public class SingleplayerWindowController extends AbstractGameWindowController {

    @Override
    protected void init() {
        setOpponent(new Account("Bot"));
    }

    @Override
    public void turn(RoShamBo playersItem) {
        getPlayer().setItem(playersItem);
        getOpponent().setItem(RoShamBo.randomItem());

        updatePlayersItemImage();
        updateOpponentsItemImage();

        showResult();
    }

    @Override
    protected void back() throws IOException {
        getWindowManager().changeWindow(Client.MENU_WINDOW);
    }
}
