package com.example.doodlejumpwithmp.controller;

import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.ScreenMode;
import com.example.doodlejumpwithmp.controller.serverwork.Server;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerMenuController extends MenuController {
    private Main main;
    private GameController gameController;

    private ClientServerController clientServerController;

    public void initData(Main main, GameController gameController, ClientServerController clientServerController) {
        this.main = main;
        this.gameController = gameController;
        this.clientServerController = clientServerController;
    }

    @FXML
    private TextField bottomTextField;

    @FXML
    private TextField topTextField;

    @FXML
    void backButtonClick() {
        main.setScreenMode(ScreenMode.CONNECTION_MENU);
        main.changeScreenMode();
        clientServerController.setIp(null);
        clientServerController.setPort(-1);
    }

    @FXML
    void startGameClick() {
        Server server = gameController.getServer();
        server.stopAccept();
        System.out.println("Send start game");
        long seed = System.currentTimeMillis();
        gameController.setSeed(seed);
        System.out.println("Seed: " + seed);
        server.sendStartGame(seed);
        System.out.println("Set multiplayer game");
        main.setScreenMode(ScreenMode.MULTIPLAYER_GAME);
        main.changeScreenMode();
    }

    @FXML
    void submitClick() {
        clientServerController.setIp(topTextField.getText());
        clientServerController.setPort(Integer.parseInt(bottomTextField.getText()));
        clientServerController.createServer();
    }

    @FXML
    void textOnBackButtonClick() {
        backButtonClick();
    }

    @FXML
    void textOnStartButtonClick() {
        startGameClick();
    }

    @FXML
    void textOnSubmitButtonClick() {
        submitClick();
    }

}

