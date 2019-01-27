package com.vladhuk.client.controllers;

import com.vladhuk.client.Main;
import com.vladhuk.client.logics.RoShamBo;
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

import static com.vladhuk.client.Main.*;

public abstract class GameWindowController extends WindowController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playersNicknameLabel.setText(PLAYER.getName());
        opponentsNicknameLabel.setText(OPPONENT.getName());
        addCheckingImage(playersImageView);
        addCheckingImage(opponentsImageView);
    }

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

    protected void showResult() {
        RoShamBo player = PLAYER.getItem();
        RoShamBo opponent = OPPONENT.getItem();

        switch (player.compete(opponent)) {
            case WIN:
                informationLabel.setTextFill(Color.GREEN);
                informationLabel.setText("You win");
                playersCounterLabel.setText(String.valueOf(Main.PLAYER.incrementAndGetWinsCounter()));
                return;
            case LOSE:
                informationLabel.setTextFill(Color.RED);
                informationLabel.setText("You lose");
                opponentsCounterLabel.setText(String.valueOf(Main.OPPONENT.incrementAndGetWinsCounter()));
                return;
            case DRAW:
                informationLabel.setTextFill(Color.ORANGE);
                informationLabel.setText("Draw");
        }
    }

    protected void addPlayersItemImages() {
        switch (PLAYER.getItem()) {
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

    protected void addOpponentsItemImage() {
        switch (OPPONENT.getItem()) {
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

    protected abstract void turn(RoShamBo playersItem);

}
