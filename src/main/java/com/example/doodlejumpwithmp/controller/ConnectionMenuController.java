package com.example.doodlejumpwithmp.controller;

import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.ScreenMode;
import javafx.fxml.FXML;

public class ConnectionMenuController extends MenuController {
    private Main main;

    public void initData(Main main, GameController gameController, ClientServerController clientServerController) {
        this.main = main;
    }

    @FXML
    void backButtonClick() {
        main.setScreenMode(ScreenMode.START_MENU);
        main.changeScreenMode();
    }

    @FXML
    void createRoomClick() {
        main.setScreenMode(ScreenMode.SERVER_ROOM);
        main.changeScreenMode();

    }

    @FXML
    void findRoomClick() {
        main.setScreenMode(ScreenMode.CLIENT_ROOM);
        main.changeScreenMode();
    }

    @FXML
    void textOnBackButtonClick() {
        backButtonClick();
    }

    @FXML
    void textOnCreateButtonClick() {
        createRoomClick();
    }

    @FXML
    void textOnFindButtonClick() {
        findRoomClick();
    }

}

