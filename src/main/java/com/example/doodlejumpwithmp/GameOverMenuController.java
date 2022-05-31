package com.example.doodlejumpwithmp;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class GameOverMenuController extends MenuController {
    private Main main;
    private Controller controller;

    public void initData(Main main, Controller controller, ClientServerController csc) {
        this.main = main;
        this.controller = controller;
    }

//    public void setScoreText(String string) {
//        scoreText.setText(string);
//    }

//    @FXML
//    private Text scoreText;

    @FXML
    void exitClick() {
        main.setScreenMode(ScreenMode.START_MENU);
        main.changeScreenMode();
        //controller.setDefaultFieldValues();
    }

    @FXML
    void restartClick() {
        main.setScreenMode(ScreenMode.SINGLE_GAME);
        main.changeScreenMode();
        //controller.setDefaultFieldValues();
        controller.initialGamePreparations();
    }

    @FXML
    void textOnExitButtonClick() {
        exitClick();
    }

    @FXML
    void textOnRestartButtonClick() {
        restartClick();
    }

}
