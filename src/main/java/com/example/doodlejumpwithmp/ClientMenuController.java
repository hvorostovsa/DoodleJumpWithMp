package com.example.doodlejumpwithmp;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

public class ClientMenuController extends MenuController {

    private Main main;
    //private Controller controller;

    private ClientServerController csc;

//    private String ip;
//    private int port;


    public void initData(Main main, Controller controller, ClientServerController csc) {
        this.main = main;
        //this.controller = controller;
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
    void submitClick() {
        csc.setIp(topTextField.getText());
        csc.setPort(Integer.parseInt(bottomTextField.getText()));
        //System.out.println(ip + ":" + port);
        csc.createClient();
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