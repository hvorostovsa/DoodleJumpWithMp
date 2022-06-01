package com.example.doodlejumpwithmp.controller;

import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.ScreenMode;
import javafx.fxml.FXML;

public class GameOverMenuController extends MenuController {
    private Main main;

    public void initData(Main main, GameController gameController, ClientServerController clientServerController) {
        this.main = main;
    }

    @FXML
    void exitClick() {
        main.setScreenMode(ScreenMode.START_MENU);
        main.changeScreenMode();
    }

    @FXML
    void restartClick() {
        main.setScreenMode(ScreenMode.SINGLE_GAME);
        main.changeScreenMode();
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
