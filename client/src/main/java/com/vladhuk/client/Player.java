package com.vladhuk.client;

import com.vladhuk.client.logics.RoShamBo;

public class Player {
    private String name = "Player";
    private int winsCounter = 0;
    private RoShamBo item;

    public Player() {}

    public Player(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getWinsCounter() {
        return winsCounter;
    }

    public int incrementAndGetWinsCounter() {
        return ++winsCounter;
    }

    public void resetWinsCounter() {
        winsCounter = 0;
    }

    public void setItem(RoShamBo item) {
        this.item = item;
    }

    public RoShamBo getItem() {
        return item;
    }

}
