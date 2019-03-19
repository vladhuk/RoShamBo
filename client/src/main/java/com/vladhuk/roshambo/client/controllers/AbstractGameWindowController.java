package com.vladhuk.roshambo.client.controllers;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.client.game.logics.RoShamBo;
import com.vladhuk.roshambo.server.models.Account;
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

    public static final Account NULL_ACCOUNT = new Account("[empty]");

    @FXML
    protected AnchorPane anchorPane;

    @FXML
    private Label informationLabel;

    @FXML
    private Label playersUsernameLabel;

    @FXML
    private Label opponentsUsernameLabel;

    @FXML
    private Label playersCounterLabel;

    @FXML
    private Label opponentsCounterLabel;

    @FXML
    private ImageView playersImageView;

    @FXML
    private ImageView opponentsImageView;

    private Player player;
    private Player opponent;

    @Override
    protected Stage getCurrentStage() {
        return (Stage) anchorPane.getScene().getWindow();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        player = new Player(Client.getAccount(), playersImageView, playersCounterLabel);
        opponent = new Player(NULL_ACCOUNT, opponentsImageView, opponentsCounterLabel);

        playersUsernameLabel.setText(player.getAccount().getUsername());
        opponentsUsernameLabel.setText(opponent.getAccount().getUsername());

        init();
        resetGame();
    }

    protected abstract void init();

    private void resetGame() {
        player.resetWinsCounter();
        opponent.resetWinsCounter();

        player.setItem(null);
        opponent.setItem(null);

        addWaitingImage(player.getImageView());
        addWaitingImage(opponent.getImageView());
    }

    public class Player {

        private Account account;
        private int winsCounter = 0;
        private Label winsCounterLabel;
        private RoShamBo item;
        private ImageView imageView;

        public Player(Account account, ImageView imageView, Label winsCounterLabel) {
            this.account = account;
            this.imageView = imageView;
            this.winsCounterLabel = winsCounterLabel;
        }

        public void setAccount(Account account) {
            this.account = account;
        }

        public Account getAccount() {
            return account;
        }

        public void incrementWinsCounter() {
            winsCounter++;
            winsCounterLabel.setText(String.valueOf(winsCounter));
        }

        public void resetWinsCounter() {
            winsCounter = 0;
            winsCounterLabel.setText("0");
        }

        public synchronized void setItem(RoShamBo item) {
            this.item = item;
        }

        public synchronized RoShamBo getItem() {
            return item;
        }

        public ImageView getImageView() {
            return imageView;
        }

    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Account account) {
        opponent.setAccount(account);
        opponentsUsernameLabel.setText(opponent.getAccount().getUsername());

        resetGame();
    }

    public void addWaitingImage(ImageView imageView) {
        imageView.setImage(new Image("images/Question.png"));
        imageView.setRotate(0);
    }

    @FXML
    void choosePaper() {
        turn(RoShamBo.PAPER);
    }

    @FXML
    void chooseScissors() {
        turn(RoShamBo.SCISSORS);
    }

    @FXML
    void chooseRock() {
        turn(RoShamBo.ROCK);
    }

    public abstract void turn(RoShamBo playersItem);

    public void showResult() {
        RoShamBo playersItem = player.getItem();
        RoShamBo opponentsItem = opponent.getItem();

        switch (playersItem.compete(opponentsItem)) {
            case WIN:
                informationLabel.setTextFill(Color.GREEN);
                informationLabel.setText("You win");
                player.incrementWinsCounter();
                break;
            case LOSE:
                informationLabel.setTextFill(Color.RED);
                informationLabel.setText("You lose");
                opponent.incrementWinsCounter();
                break;
            case DRAW:
                informationLabel.setTextFill(Color.ORANGE);
                informationLabel.setText("Draw");
                break;
        }

        player.setItem(null);
        opponent.setItem(null);
    }

    public void updatePlayersItemImage() {
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

    public void updateOpponentsItemImage() {
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

    public void setInfo(String info) {
        informationLabel.setTextFill(Color.BLACK);
        informationLabel.setText(info);
    }

    @FXML
    abstract void back() throws IOException;

}
