package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.model.serverwork.Server;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ServerMenuController extends MenuController {
    private Main main;
    private Controller controller;

    private ClientServerController csc;

    public void initData(Main main, Controller controller, ClientServerController csc) {
        this.main = main;
        this.controller = controller;
        this.csc = csc;
    }

    @FXML
    private TextField bottomTextField;

    @FXML
    private TextField topTextField;

    @FXML
    void backButtonClick() {
        main.setScreenMode(ScreenMode.CONNECTION_MENU);
        main.changeScreenMode();
        csc.setIp(null);
        csc.setPort(-1);
    }

    @FXML
    void startGameClick() {
        Server server = controller.getServer();
        server.stopAccept();
        System.out.println("Send start game");
        long seed = System.currentTimeMillis();
        controller.setSeed(seed);
        System.out.println("Seed: " + seed);
        server.sendStartGame(seed);
        System.out.println("Set multiplayer game");
        controller.initialGamePreparations();
        main.setScreenMode(ScreenMode.MULTIPLAYER_GAME);
        main.changeScreenMode();
    }

    @FXML
    void submitClick() {
        csc.setIp(topTextField.getText());
        csc.setPort(Integer.parseInt(bottomTextField.getText()));
        csc.createServer();
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

