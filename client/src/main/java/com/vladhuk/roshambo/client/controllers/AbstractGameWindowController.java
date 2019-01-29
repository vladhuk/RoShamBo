package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.logics.RoShamBo;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public abstract class AbstractGameWindowController extends AbstractWindowController implements Initializable {

    private Player player;
    private Player opponent;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label informationLabel;

    @FXML
    private Label playersNicknameLabel;

    @FXML
    private Label opponentsNicknameLabel;

    @FXML
    private Label playersCounterLabel;

    @FXML
    private Label opponentsCounterLabel;

    @FXML
    protected ImageView playersImageView;

    @FXML
    protected ImageView opponentsImageView;

    @Override
    protected Stage getStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    public static class Player {

        private String name;
        private int winsCounter = 0;
        private RoShamBo item;

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

    protected Player getPlayer() {
        return player;
    }

    protected Player getOpponent() {
        return opponent;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = addPlayer();
        opponent = addOpponent();

        playersNicknameLabel.setText(player.getName());
        opponentsNicknameLabel.setText(opponent.getName());

        addCheckingImage(playersImageView);
        addCheckingImage(opponentsImageView);
    }

    protected abstract Player addPlayer();

    protected abstract Player addOpponent();

    protected void addCheckingImage(ImageView imageView) {
        imageView.setImage(new Image("images/Question.png"));
        imageView.setRotate(0);
    }

    @FXML
    void choosePaper(ActionEvent event) {
        turn(RoShamBo.PAPER);
    }

    @FXML
    void chooseScissors(ActionEvent event) {
        turn(RoShamBo.SCISSORS);
    }

    @FXML
    void chooseRock(ActionEvent event) {
        turn(RoShamBo.ROCK);
    }

    protected abstract void turn(RoShamBo playersItem);

    protected void showResult() {
        RoShamBo playersItem = player.getItem();
        RoShamBo opponentsItem = opponent.getItem();

        switch (playersItem.compete(opponentsItem)) {
            case WIN:
                informationLabel.setTextFill(Color.GREEN);
                informationLabel.setText("You win");
                playersCounterLabel.setText(String.valueOf(player.incrementAndGetWinsCounter()));
                return;
            case LOSE:
                informationLabel.setTextFill(Color.RED);
                informationLabel.setText("You lose");
                opponentsCounterLabel.setText(String.valueOf(opponent.incrementAndGetWinsCounter()));
                return;
            case DRAW:
                informationLabel.setTextFill(Color.ORANGE);
                informationLabel.setText("Draw");
        }
    }

    protected void updatePlayersItemImage() {
        switch (player.getItem()) {
            case PAPER:
                playersImageView.setImage(new Image("images/Paper1.png"));
                playersImageView.setRotate(-46.2);
                break;
            case ROCK:
                playersImageView.setImage(new Image("images/Rock1.png"));
                playersImageView.setRotate(14.9);
                break;
            case SCISSORS:
                playersImageView.setImage(new Image("images/Scissors1.png"));
                playersImageView.setRotate(0);
                break;
        }
    }

    protected void updateOpponentsItemImage() {
        switch (opponent.getItem()) {
            case PAPER:
                opponentsImageView.setImage(new Image("images/Paper2.png"));
                opponentsImageView.setRotate(46.2);
                break;
            case ROCK:
                opponentsImageView.setImage(new Image("images/Rock2.png"));
                opponentsImageView.setRotate(-14.9);
                break;
            case SCISSORS:
                opponentsImageView.setImage(new Image("images/Scissors2.png"));
                opponentsImageView.setRotate(0);
                break;
        }
    }

    @FXML
    abstract void back() throws IOException;

}
