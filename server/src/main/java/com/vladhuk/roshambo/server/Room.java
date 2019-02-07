package com.vladhuk.roshambo.server;

import java.io.Serializable;

public class Room implements Serializable {
    
    private String title = "";
    private String description = "";
    private Account player1;
    private Account player2;

    public Room() {}

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Room(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public boolean isFull() {
        return (player1 != null) && (player2 != null);
    }
    
    public boolean isEmpty() {
        return (player1 == null) && (player2 == null);
    }

    public Account getOpponent(Account player) {
        if (player.equals(player1)) {
            return player2;
        }
        return player1;
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
    
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int hashCode() {
        int result = 0;

        if (player1 != null) {
            result += player1.hashCode();
        }

        if (player2 != null) {
            result += player2.hashCode();
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        Room object = (Room) obj;

        return hashCode() == object.hashCode();
    }
}
