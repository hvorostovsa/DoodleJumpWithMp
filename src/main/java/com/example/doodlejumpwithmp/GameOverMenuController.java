package com.example.doodlejumpwithmp;

import javafx.fxml.FXML;

public class GameOverMenuController extends MenuController {
    private Main main;
    private Controller controller;

    public void initData(Main main, Controller controller, ClientServerController csc) {
        this.main = main;
        this.controller = controller;
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
