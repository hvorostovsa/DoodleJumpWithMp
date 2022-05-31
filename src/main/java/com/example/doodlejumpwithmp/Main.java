package com.example.doodlejumpwithmp;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.doodle.ShadowDoodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.model.serverwork.Client;
import com.example.doodlejumpwithmp.model.serverwork.Server;
import com.example.doodlejumpwithmp.model.serverwork.ServerKey;
import com.example.doodlejumpwithmp.model.serverwork.ServerParameter;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class Main extends Application {
    public final static int SCREEN_WIDTH = 450;
    public final static int SCREEN_HEIGHT = 700;

    private final static double MENU_BUTTON_WIDTH = 180;
    private final static double SMALL_BUTTON_WIDTH = MENU_BUTTON_WIDTH / 2;

    private final static int LOGO_WIDTH = 350;
    private final static int LOGO_HEIGHT = 250;

    private StringBuilder inputIp = new StringBuilder("IP: ");
    private StringBuilder inputPort = new StringBuilder("Port: ");

    private String ip;
    private final static int MAX_NUM_IP = 15;
    private final static int UNCHANGEABLE_PART_IP = 4;
    private int port;
    private final static int MAX_NUM_PORT = 5;
    private final static int UNCHANGEABLE_PART_PORT = 6;

    private GraphicsContext gc;
    private Controller controller;
    private ClientServerController csc;
    private ArrayList<String> controlList;
    private ScreenMode screenMode = ScreenMode.START_MENU;

    private static MenuButton firstGameButton;
    private static MenuButton secondGameButton;
    private static MenuButton smallGameButton;

    private static MenuTextField ipTextField;
    private boolean ipFieldIsActive = false;
    private static MenuTextField portTextField;
    private boolean portFieldIsActive = false;

    private boolean screenModeIsChanged = true;

    static Image background = new Image(
            MainUtils.getFilePathFromResources("background.png"),
            SCREEN_WIDTH, SCREEN_HEIGHT, false, false
    );

//    static BackgroundImage backgroundImage = new BackgroundImage(background,
//            BackgroundRepeat.NO_REPEAT,
//            BackgroundRepeat.NO_REPEAT,
//            BackgroundPosition.DEFAULT,
//            BackgroundSize.DEFAULT);

    static Image platformImage = new Image(
            MainUtils.getFilePathFromResources("Normal_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image movingPlatformImage = new Image(
            MainUtils.getFilePathFromResources("Moving_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image oneJumpPlatformImage = new Image(
            MainUtils.getFilePathFromResources("One_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image zeroJumpPlatformImage = new Image(
            MainUtils.getFilePathFromResources("Zero_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image doodleImage = new Image(
            MainUtils.getFilePathFromResources("Doodle.png"),
            Doodle.getWidth(), Doodle.getHeight(), false, false
    );
    static Image shadowDoodleImage = new Image(
            MainUtils.getFilePathFromResources("ShadowDoodle.png"),
            ShadowDoodle.getWidth(), ShadowDoodle.getHeight(), false, false
    );
    static Image logoImage = new Image(
            MainUtils.getFilePathFromResources("Logo.png"),
            LOGO_WIDTH, LOGO_HEIGHT, false, false
    );

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public void setScreenMode(ScreenMode screenMode) {
        this.screenMode = screenMode;
    }

    public void changeScreenMode() {
        screenModeIsChanged = true;
    }

    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }

        repaintScore();
        repaintShadowDoodles();
        repaintDoodle(controller.getDoodle());

        if (controller.isDoodleFall()) {
//            gc.drawImage(logoImage, 50, 20);
//            firstGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
//            secondGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
//
//            firstGameButton.createOnPosition(125, 300, "Restart");
//            secondGameButton.createOnPosition(125, 450, "Exit to Main Menu");
            screenMode = ScreenMode.GAME_OVER_MENU;
            screenModeIsChanged = true;
            controller = new Controller(this);
            controlList = controller.getInput();
        }
    }

    private void repaintDoodle(Doodle doodle) {
        switch (doodle.getDoodleSide()) {
            case RIGHT -> gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
            case LEFT -> gc.drawImage(
                    doodle.getImage(),
                    doodle.getX() + doodle.getImage().getWidth(), doodle.getY(),
                    -1 * doodle.getImage().getWidth(), doodle.getImage().getHeight()
            );
            default -> throw new IllegalStateException("Unexpected value: " + doodle.getDoodleSide());
        }
    }

    private void repaintShadowDoodles() {
        for (ShadowDoodle shadowDoodle : controller.getShadowDoodles().values()) {
            switch (shadowDoodle.getDoodleSide()) {
                case RIGHT -> gc.drawImage(shadowDoodle.getImage(), shadowDoodle.getX(), shadowDoodle.getY());
                case LEFT -> gc.drawImage(
                        shadowDoodle.getImage(),
                        shadowDoodle.getX() + shadowDoodle.getImage().getWidth(), shadowDoodle.getY(),
                        -1 * shadowDoodle.getImage().getWidth(), shadowDoodle.getImage().getHeight()
                );
                default -> throw new IllegalStateException("Unexpected value: " + shadowDoodle.getDoodleSide());
            }
        }
    }

    private void repaintPlatforms(Platform platform) {
        gc.drawImage(platform.getImage(), platform.getX(), platform.getY());
    }

    private void repaintScore() {
        setText(controller.getScoreString(), 20, 20, 20);
    }

    /*private void runStartingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 50, 20);

        firstGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
        secondGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);

        firstGameButton.createOnPosition(125, 300, "Single Game");
        secondGameButton.createOnPosition(125, 450, "Multiplayer");
    }

    private void runConnectingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 50, 20);

        firstGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
        secondGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
        smallGameButton = new MenuButton(gc, SMALL_BUTTON_WIDTH);

        firstGameButton.createOnPosition(125, 300, "Create Room");
        secondGameButton.createOnPosition(125, 450, "Find Room");
        smallGameButton.createOnPosition(250, 600, "Back");
    }*/

    /*private void openConnectionSettings(boolean isServer) {
        controller.setIsServer(isServer);
        controller.setIsClient(!isServer);
        gc.drawImage(background, 0, 0);
        String ip = inputIp.toString();
        String port = inputPort.toString();

        ipTextField = new MenuTextField(gc);
        ipTextField.setSelected(ipFieldIsActive);
        ipTextField.createTextField(50, 150, ip);

        portTextField = new MenuTextField(gc);
        portTextField.setSelected(portFieldIsActive);
        portTextField.createTextField(50, 300, port);

        firstGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
        smallGameButton = new MenuButton(gc, SMALL_BUTTON_WIDTH);

        firstGameButton.createOnPosition(50, 450, "Submit");
        smallGameButton.createOnPosition(180, 600, "Back");

        if (isServer) {
            secondGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
            secondGameButton.createOnPosition(250, 450, "Start");
            beforeStartServerWork();
        } else {
            beforeStartClientWork();
        }
    }

    private void openConnectionSettings() {
        openConnectionSettings(false);
    }*/

    /*private void beforeConnectionStart(boolean isServer) {

        if (isServer) {
            beforeStartServerWork();
        } else {
            beforeStartClientWork();
        }
    }
    private void beforeConnectionStart() {
        beforeConnectionStart(false);
    }*/

    /*private void beforeStartServerWork() {
        Server server = controller.getServer();
        if (server != null) {
            LinkedList<JSONObject> receivedRequests = server.getReceivedRequests();
            JSONObject response;
            while (receivedRequests.size() > 0) {
                response = receivedRequests.pop();
                int code = response.getIntValue(ServerParameter.CODE.toString());
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case CONNECTED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        controller.addShadowDoodle(newClientId);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    private void beforeStartClientWork() {
        Client client = controller.getClient();
        if (client != null) {
            LinkedList<JSONObject> receivedRequests = client.getReceivedRequests();
            JSONObject response;
            boolean gameStarted = false;
            while (receivedRequests.size() > 0) {
                if (gameStarted) {
                    break;
                }
                response = receivedRequests.pop();
                int code = response.getIntValue(ServerParameter.CODE.toString());
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case SET_ID -> {
                        int clientId = response.getIntValue(ServerParameter.NEW_ID.toString());
                        client.setClientId(clientId);
                        Integer[] connections =
                                response.getJSONArray(ServerParameter.CONNECTIONS.toString()).toArray(Integer[]::new);
                        List<Integer> list = Stream.of(connections).filter(it -> it != clientId).toList();
                        client.getConnections().addAll(list);
                        for (Integer id : list) {
                            controller.addShadowDoodle(id);
                        }
                    }
                    case NEW_USER_ADDED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        controller.addShadowDoodle(newClientId);
                    }
                    case START_GAME -> {
                        gameStarted = true;
                        long seed = response.getLong(ServerParameter.SEED.toString());
                        System.out.println("Seed: " + seed);
                        controller.setSeed(seed);
                        controller.initialGamePreparations();
                        screenMode = ScreenMode.MULTIPLAYER_GAME;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }*/

    private void setText(String string, double x, double y, int size) {
        Font font = new Font("Times New Roman", size);
        gc.setFont(font);
        gc.setFill(Color.BLACK);
        gc.fillText(string, x, y);
    }

    @Override
    public void start(Stage stage) throws IOException {
        controller = new Controller(this);
        csc = new ClientServerController(this, controller);
        stage.setResizable(false);

        Group gameRoot = new Group();
        Scene scene = new Scene(gameRoot, SCREEN_WIDTH, SCREEN_HEIGHT);
        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        gameRoot.getChildren().add(canvas);
        this.gc = canvas.getGraphicsContext2D();

        Scene startMenuScene = createScene(ScreenMode.START_MENU);
        Scene connectionMenuScene = createScene(ScreenMode.CONNECTION_MENU);
        Scene serverRoomScene = createScene(ScreenMode.SERVER_ROOM);
        Scene clientRoomScene = createScene(ScreenMode.CLIENT_ROOM);

        Scene gameOverScene = createScene(ScreenMode.GAME_OVER_MENU);
        Text scoreText = createScoreText();
        Group group = (Group) gameOverScene.getRoot();

        gc.drawImage(background, 0, 0);

        controlList = controller.getInput();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (screenModeIsChanged) {
                    System.out.println(1);
                    screenModeIsChanged = false;
                    switch (screenMode) {
                        case SINGLE_GAME -> stage.setScene(scene);
                        case GAME_OVER_MENU -> {
                            group.getChildren().remove(scoreText);
                            scoreText.setText(controller.getScoreString());
                            group.getChildren().add(scoreText);
                            stage.setScene(gameOverScene);
                        }
                        case START_MENU -> stage.setScene(startMenuScene);
                        case CONNECTION_MENU -> stage.setScene(connectionMenuScene);
                        case SERVER_ROOM -> stage.setScene(serverRoomScene);
                        case CLIENT_ROOM -> stage.setScene(clientRoomScene);
                    }
                }
                if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME)
                    controller.update();
                else if (screenMode == ScreenMode.SERVER_ROOM)
                    csc.beforeConnectionStart(true);
                else if (screenMode == ScreenMode.CLIENT_ROOM)
                    csc.beforeConnectionStart();

            }
        };
        timer.start();

        /*scene.setOnMouseClicked(event -> {
            if (screenMode == ScreenMode.START_MENU) {/*
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    controller.initialGamePreparations();
                    screenMode = ScreenMode.SINGLE_GAME;
                } else if (secondGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CONNECTION_MENU;
            } else if (screenMode == ScreenMode.CONNECTION_MENU) {
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.SERVER_ROOM;
                else if (secondGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CLIENT_ROOM;
                else if (smallGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.START_MENU;
//            } else if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
//                if (firstGameButton.getBoundary().contains(event.getX(), event.getY()))
//                    ///restartGame();
//                else if (secondGameButton.getBoundary().contains(event.getX(), event.getY())) {
//                    screenMode = ScreenMode.START_MENU;
//                    //restartGame();
//                }

            } else {
                if (ipTextField.getBoundary().contains(event.getX(), event.getY())) {
                    ipFieldIsActive = true;
                    portFieldIsActive = false;
                } else if (portTextField.getBoundary().contains(event.getX(), event.getY())) {
                    ipFieldIsActive = false;
                    portFieldIsActive = true;
                } else {
                    ipFieldIsActive = false;
                    portFieldIsActive = false;
                }
                if ((firstGameButton.getBoundary().contains(event.getX(), event.getY()))) {
                    ip = inputIp.substring(4);
                    port = Integer.parseInt(inputPort.substring(6));
                    System.out.println(ip + ":" + port);
                    if (controller.getIsServer()) { // user is server
                        createServer();
                    } else { // user is client
                        createClient();
                    }
                } else if ((controller.getIsServer()) && secondGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    if (controller.getIsServer()) {
                        Server server = controller.getServer();
                        server.stopAccept();
                        System.out.println("Send start game");
                        long seed = System.currentTimeMillis();
                        controller.setSeed(seed);
                        System.out.println("Seed: " + seed);
                        server.sendStartGame(seed);
                    }
                    System.out.println("Set multiplayer game");
                    controller.initialGamePreparations();
                    screenMode = ScreenMode.MULTIPLAYER_GAME;
                } else if (smallGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    inputIp = new StringBuilder("IP: ");
                    inputPort = new StringBuilder("Port: ");

                    ip = null;
                    port = -1;

                    screenMode = ScreenMode.CONNECTION_MENU;
                }
            }
        });*/

        /*startMenuScene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F) {
                screenMode = ScreenMode.SINGLE_GAME;
                controller.initialGamePreparations();
                stage.setScene(scene);
            }

        });*/

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
//            if (event.getCode() == KeyCode.F) {
//                System.out.println(2);
//                screenMode = ScreenMode.SINGLE_GAME;
//                stage.setScene(scene);
//            }

            /*if (screenMode == ScreenMode.SERVER_ROOM || screenMode == ScreenMode.CLIENT_ROOM) {
                if (ipFieldIsActive) {
                    if (inputIp.length() < MAX_NUM_IP + UNCHANGEABLE_PART_IP) {
                        if (event.getCode().isDigitKey()) inputIp.append(code.substring(code.length() - 1));
                        if (code.equals("PERIOD")) inputIp.append(".");
                    }
                    if (code.equals("BACK_SPACE") && inputIp.length() > UNCHANGEABLE_PART_IP) {
                        inputIp.delete(inputIp.length() - 1, inputIp.length());
                    }
                }

                if (portFieldIsActive) {
                    if (event.getCode().isDigitKey() && inputPort.length() < MAX_NUM_PORT + UNCHANGEABLE_PART_PORT)
                        inputPort.append(code.substring(code.length() - 1));
                    if (code.equals("BACK_SPACE") && inputPort.length() > UNCHANGEABLE_PART_PORT)
                        inputPort.delete(inputPort.length() - 1, inputPort.length());
                }
            }*/

            if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                if (code.equals("RIGHT") || code.equals("LEFT")) {
                    if (!controlList.contains(code)) {
                        controlList.add(code);
                    }
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                String code = event.getCode().toString();
                if (code.equals("RIGHT") || code.equals("LEFT"))
                    controlList.remove(code);
            }
        });

        stage.show();
    }



    private Scene createScene(ScreenMode screenMode) {
        FXMLLoader loader;
        Group root = new Group();

        if (screenMode == ScreenMode.START_MENU)
            loader = new FXMLLoader(Main.class.getResource("start-menu.fxml"));
        else if (screenMode == ScreenMode.CONNECTION_MENU)
            loader = new FXMLLoader(Main.class.getResource("connection-menu.fxml"));
        else if (screenMode == ScreenMode.GAME_OVER_MENU)
            loader = new FXMLLoader(Main.class.getResource("game-over-menu.fxml"));
        else if (screenMode == ScreenMode.SERVER_ROOM)
            loader = new FXMLLoader(Main.class.getResource("server-room.fxml"));
        else if (screenMode == ScreenMode.CLIENT_ROOM)
            loader = new FXMLLoader(Main.class.getResource("client-room.fxml"));
        else throw new IllegalStateException("File for " + screenMode + "not found");

        try {
            root = loader.load();
            MenuController menuController = loader.getController();
            menuController.initData(this, controller, csc);
//            if (menuController.getClass() == GameOverMenuController.class)
//                ((GameOverMenuController) menuController).setScoreText(controller.getScoreString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Scene(root, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    private Text createScoreText() {
        Text score = new Text(controller.getScoreString());
        Font font = new Font("Times New Roman", 18);
        score.setFont(font);
        score.setX(180);
        score.setY(600);
        return score;
    }

    private void createServer() {
        Server server = controller.getServer();
        if (server != null && !(server.getIp().equals(ip) && port == server.getPort())) { // if new server ip:port
            server.close();
            server = null;
        }
        if (server == null) {
            try {
                controller.setServer(new Server(ip, port));
            } catch (IOException exception) {
                System.out.println("incorrect address " + ip + ":" + port);
            }
        }
    }

    private void createClient() {
        Client client = controller.getClient();
        if (client != null && !(client.getIp().equals(ip) && port == client.getPort())) { // if new client ip:port
            client.close();
            client = null;
        }
        if (client == null) {
            try {
                client = new Client(ip, port);
                controller.setClient(client);
                client.sendConnected();
            } catch (IOException exception) {
                System.out.println("can't connect to server " + ip + ":" + port);
            }
        }
    }

//    private void restartGame() {
//        controller.initialGamePreparations();
//    }

    public static void main(String[] args) {
        launch(args);
    }
}