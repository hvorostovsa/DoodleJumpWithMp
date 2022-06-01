package com.example.doodlejumpwithmp.controller;

import com.example.doodlejumpwithmp.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ClientMenuController extends MenuController {

    private Main main;

    private ClientServerController clientServerController;

    public void initData(Main main, GameController gameController, ClientServerController clientServerController) {
        this.main = main;
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
    void submitClick() {
        clientServerController.setIp(topTextField.getText());
        clientServerController.setPort(Integer.parseInt(bottomTextField.getText()));
        System.out.println(clientServerController.getIp() + ":" + clientServerController.getPort());
        clientServerController.createClient();
    }

    @FXML
    void textOnBackButtonClick() {
        backButtonClick();
    }

    @FXML
    void textOnSubmitButtonClick() {
        submitClick();
    }

}