package com.example.doodlejumpwithmp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.Stage;

public class MenuController {
    private Main main;
    private Controller controller;

    public void initData(Main main, Controller controller, ClientServerController csc) {
        this.main = main;
        this.controller = controller;
    }

    @FXML
    void singleGameClick() {
        main.setScreenMode(ScreenMode.SINGLE_GAME);
        main.changeScreenMode();
        controller.initialGamePreparations();
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
