package com.vladhuk.roshambo.server;

import java.io.Serializable;

public class Room implements Serializable {
    
    private String title = "";
    private String description = "";
    private Account player1;
    private Account player2;
    
    public Room() {}
    
    public Room(String title, String description) {
        this.title = title;
        this.description = description;
    }
    
    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isFull() {
        return (player1 != null) && (player2 != null);
    }
    
    public boolean isEmpty() {
        return (player1 == null) && (player2 == null);
    }

    public Account getPlayer1() {
        return player1;
    }

    public Account getPlayer2() {
        return player2;
    }
    
    public void addPlayer(Account player) {
        if (isFull()) {
            return;
        }

        if (player1 == null) {
            player1 = player;
        } else {
            player2 = player;
        }
    }
    
    public void removePlayer(Account player) {
        if (player1.equals(player)) {
            player1 = null;
        } else {
            player2 = null;
        }
    }
    
    @Override
    public String toString() {
        return title;
    }
    
}