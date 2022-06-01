package com.example.doodlejumpwithmp.controller;

import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.ScreenMode;
import com.example.doodlejumpwithmp.controller.ClientServerController;
import com.example.doodlejumpwithmp.controller.GameController;
import javafx.fxml.FXML;

public class MenuController {
    private Main main;

    public void initData(Main main, GameController gameController, ClientServerController clientServerController) {
        this.main = main;
    }

    @FXML
    void singleGameClick() {
        main.setScreenMode(ScreenMode.SINGLE_GAME);
        main.changeScreenMode();
    }

    @FXML
    void multiplayerClick() {
        main.setScreenMode(ScreenMode.CONNECTION_MENU);
        main.changeScreenMode();
    }

    @FXML
    void textOnGameButtonClick() {
        singleGameClick();
    }

    @FXML
    void textOnMultiplayerButtonClick() {
        multiplayerClick();
    }
}
