package com.example.doodlejumpwithmp;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.doodle.ShadowDoodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.model.serverwork.Client;
import com.example.doodlejumpwithmp.model.serverwork.Server;
import com.example.doodlejumpwithmp.model.serverwork.ServerKey;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
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
    private int port;
    private final static int MAX_NUM_PORT = 5;

    private GraphicsContext gc;
    private Controller controller;
    private ArrayList<String> controlList;
    private ScreenMode screenMode = ScreenMode.START_MENU;

    private static MenuButton firstGameButton;
    private static MenuButton secondGameButton;
    private static MenuButton smallGameButton;

    private static MenuTextField ipTextField;
    private boolean ipFieldIsActive = false;
    private static MenuTextField portTextField;
    private boolean portFieldIsActive = false;

    static Image background = new Image(
            MainUtils.getFilePathFromResources("background.png"),
            SCREEN_WIDTH, SCREEN_HEIGHT, false, false
    );
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

    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }

        repaintScore();
        repaintShadowDoodles();
        repaintDoodle(controller.getDoodle());

        if (controller.isDoodleFall()) {
            gc.drawImage(logoImage, 50, 20);
            firstGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);
            secondGameButton = new MenuButton(gc, MENU_BUTTON_WIDTH);

            firstGameButton.createOnPosition(125, 300, "Restart");
            secondGameButton.createOnPosition(125, 450, "Exit to Main Menu");
        }
    }

    private void repaintDoodle(Doodle doodle) {
        switch (doodle.getDoodleSide().getValue()) { // ))
            case 1 -> gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
            case -1 -> gc.drawImage(
                    doodle.getImage(),
                    doodle.getX() + doodle.getImage().getWidth(), doodle.getY(),
                    -1 * doodle.getImage().getWidth(), doodle.getImage().getHeight()
            );
            default -> throw new IllegalStateException("Unexpected value: " + doodle.getDoodleSide());
        }
    }

    private void repaintShadowDoodles() {
        for (ShadowDoodle shadowDoodle: controller.getShadowDoodles().values()) {
            switch (shadowDoodle.getDoodleSide().getValue()) { // ))
                case 1 -> gc.drawImage(shadowDoodle.getImage(), shadowDoodle.getX(), shadowDoodle.getY());
                case -1 -> gc.drawImage(
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

    private void runStartingMenu() {
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
    }

    private void openConnectionSettings(boolean isServer) {
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
    }

    private void beforeStartServerWork() {
        Server server = controller.getServer();
        if (server != null) {
            LinkedList<JSONObject> receivedRequests = server.getReceivedRequests();
            JSONObject response;
            while (receivedRequests.size() > 0) {
                response = receivedRequests.pop();
                int code = response.getIntValue("code");
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case CONNECTED -> {
                        int newClientId = response.getIntValue("clientId");
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
                int code = response.getIntValue("code");
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case SET_ID -> {
                        int clientId = response.getIntValue("newId");
                        client.setClientId(clientId);
                        Integer[] connections = response.getJSONArray("connections").toArray(Integer[]::new);
                        List<Integer> list = Stream.of(connections).filter(it -> it != clientId).toList();
                        client.getConnections().addAll(list);
                        for (Integer id: list) {
                            controller.addShadowDoodle(id);
                        }
                    }
                    case NEW_USER_ADDED -> {
                        int newClientId = response.getIntValue("clientId");
                        controller.addShadowDoodle(newClientId);
                    }
                    case START_GAME -> {
                        gameStarted = true;
                        long seed = response.getLong("seed");
                        System.out.println("Seed: " + seed);
                        controller.setSeed(seed);
                        controller.initialGamePreparations();
                        screenMode = ScreenMode.MULTIPLAYER_GAME;
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    private void setText(String string, double x, double y, int size) {
        Font font = new Font("Times New Roman", size);
        gc.setFont(font);
        gc.setFill(Color.BLACK);
        gc.fillText(string, x, y);
    }

    @Override
    public void start(Stage stage) {
        stage.setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        stage.setScene(scene);

        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        root.getChildren().add(canvas);
        this.gc = canvas.getGraphicsContext2D();

        gc.drawImage(background, 0, 0);
        controller = new Controller(this);

        controlList = controller.getInput();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                switch (screenMode) {
                    case SINGLE_GAME -> controller.update();
                    case MULTIPLAYER_GAME -> controller.update(); // TODO add second doodle
                    case START_MENU -> runStartingMenu();
                    case CONNECTION_MENU -> runConnectingMenu();
                    case SERVER_ROOM -> openConnectionSettings(true);
                    case CLIENT_ROOM -> openConnectionSettings();
                }
            }
        };
        timer.start();

        scene.setOnMouseClicked(event -> {

            if (screenMode == ScreenMode.START_MENU) {
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    controller.initialGamePreparations();
                    screenMode = ScreenMode.SINGLE_GAME;
                }
                else if (secondGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CONNECTION_MENU;
            } else if (screenMode == ScreenMode.CONNECTION_MENU) {
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.SERVER_ROOM;
                else if (secondGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CLIENT_ROOM;
                else if (smallGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.START_MENU;
            } else if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY()))
                    restartGame();
                else if (secondGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    screenMode = ScreenMode.START_MENU;
                    restartGame();
                }

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
                }
                else if (smallGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    inputIp = new StringBuilder("IP: ");
                    inputPort = new StringBuilder("Port: ");

                    ip = null;
                    port = -1;

                    screenMode = ScreenMode.CONNECTION_MENU;
                }
            }
        });

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();

            if (screenMode == ScreenMode.SERVER_ROOM || screenMode == ScreenMode.CLIENT_ROOM) {
                if (ipFieldIsActive) {
                    if (inputIp.length() < MAX_NUM_IP + 4) {
                        if (event.getCode().isDigitKey()) inputIp.append(code.substring(code.length() - 1));
                        if (code.equals("PERIOD")) inputIp.append(".");
                    }
                    if (code.equals("BACK_SPACE") && inputIp.length() > 4) {
                        inputIp.delete(inputIp.length() - 1, inputIp.length());
                    }
                }

                if (portFieldIsActive) {
                    if (event.getCode().isDigitKey() && inputPort.length() < MAX_NUM_PORT + 6)
                        inputPort.append(code.substring(code.length() - 1));
                    if (code.equals("BACK_SPACE") && inputPort.length() > 6)
                        inputPort.delete(inputPort.length() - 1, inputPort.length());
                }
            }

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

    private void restartGame() {
        controller = new Controller(this);
        controller.initialGamePreparations();
        controlList = controller.getInput();
    }

    public static void main(String[] args) {
        launch(args);
    }
}