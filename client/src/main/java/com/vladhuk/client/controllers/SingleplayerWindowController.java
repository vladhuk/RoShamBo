package com.vladhuk.client.controllers;

import com.vladhuk.client.Main;
import com.vladhuk.client.logics.RoShamBo;

import java.io.IOException;


public class SingleplayerWindowController extends GameWindowController {

    @Override
    protected void turn(RoShamBo playersItem) {
        Main.PLAYER.setItem(playersItem);
        Main.OPPONENT.setItem(RoShamBo.randomItem());
        showResult();
        addPlayersItemImages();
        addOpponentsItemImage();
    }

    @Override
    protected void back() throws IOException {
        Main.PLAYER.resetWinsCounter();
        Main.OPPONENT.resetWinsCounter();
        changeWindow(Main.MENU_WINDOW);
    }
}
