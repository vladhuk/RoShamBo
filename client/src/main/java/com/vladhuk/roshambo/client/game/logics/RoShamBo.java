package com.vladhuk.roshambo.client.game.logics;

import java.util.Random;

import static com.vladhuk.roshambo.client.game.logics.Outcome.*;

public enum RoShamBo {
    ROCK(DRAW, WIN, LOSE),
    SCISSORS(LOSE, DRAW, WIN),
    PAPER(WIN, LOSE, DRAW);

    private Outcome stoneResult, scissorsResult, paperResult;

    private RoShamBo (Outcome stone, Outcome scissors, Outcome paper) {
        stoneResult = stone;
        scissorsResult = scissors;
        paperResult = paper;
    }

    public Outcome compete(RoShamBo item) {
        switch (item) {
            default:
            case ROCK: return stoneResult;
            case SCISSORS: return scissorsResult;
            case PAPER: return paperResult;
        }
    }

    public static RoShamBo randomItem() {
        return RoShamBo.values()[new Random().nextInt(3)];
    }

}