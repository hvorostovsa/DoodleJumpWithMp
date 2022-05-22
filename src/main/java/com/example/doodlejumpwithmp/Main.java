package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
    private final static int SCREEN_WIDTH = 450;
    private final static int SCREEN_HEIGHT = 700;
    private final static int LOGO_WIDTH = 400;
    private final static int LOGO_HEIGHT = 100;

    private String direction = "RIGHT";

    private static final String PACKAGE_NAME = Main.class.getPackage().getName(); // com.example.doodlejumpwithmp
    private static final String RESOURCE_PREFIX = PACKAGE_NAME.replace(".", "/") + "/";

    private final StringBuilder inputIp = new StringBuilder();
    private final StringBuilder inputPort = new StringBuilder();

    private String ip;
    private String port;

    private GraphicsContext gc;
    private Controller controller;
    private ArrayList<String> keys;
    private ScreenMode screenMode = ScreenMode.START_MENU;
    // TODO replace with 3 button
    private static MenuButton singleGameButton;
    private static MenuButton multiplayerGameButton;
    private static MenuButton serverRoomButton;
    private static MenuButton clientRoomButton;
    private static MenuTextField ipTextField;
    private boolean ipActive = false;
    private static MenuTextField portTextField;
    private boolean portActive = false;
    private static MenuButton submitButton;
    private static MenuButton startButton;

    static Image background = new Image(
            getFilePathFromResources("background.png"),
            SCREEN_WIDTH, SCREEN_HEIGHT, false, false
    );
    static Image platformImage = new Image(
            getFilePathFromResources("Normal_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image movingPlatformImage = new Image(
            getFilePathFromResources("Moving_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image oneJumpPlatformImage = new Image(
            getFilePathFromResources("One_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image zeroJumpPlatformImage = new Image(
            getFilePathFromResources("Zero_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    static Image doodleImage = new Image(
            getFilePathFromResources("Doodle.png"),
            Doodle.getWidth(), Doodle.getHeight(), false, false
    );
    static Image logoImage = new Image(
            getFilePathFromResources("Logo.png"),
            LOGO_WIDTH, LOGO_HEIGHT, false, false
    );

    public static String getFilePathFromResources(String filename) {
        String filepath = filename.replace("\\", "/");
        if (!filename.startsWith(RESOURCE_PREFIX)) {
            filepath = RESOURCE_PREFIX + filepath;
        }
        URL url = Main.class.getClassLoader().getResource(filepath);
        if (url == null) {
            System.err.println("File \"" + filename + "\" not found"); // FileNotFoundException
            System.exit(-1);
        }
        return url.toString();
    }

    public static int getScreenHeight() {
        return SCREEN_HEIGHT;
    }

    public static int getScreenWidth() {
        return SCREEN_WIDTH;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }

        repaintScore(controller.getScoreString());
        repaintDoodle(controller.getDoodle());
        // TODO add buttons instead of text
        if (controller.ifFall()) {
            setText("Game Over! Press Space to restart", 50, 300);
            setText("Or press Escape to return to main menu", 30, 320);
        }
    }

    private void repaintDoodle(Doodle doodle) {
        if (direction.equals("RIGHT"))
            gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
        else
            gc.drawImage(
                    doodle.getImage(),
                    doodle.getX() + doodle.getImage().getWidth(), doodle.getY(),
                    -1 * doodle.getImage().getWidth(), doodle.getImage().getHeight()
            );
    }

    private void repaintPlatforms(Platform platform) {
        gc.drawImage(platform.getImage(), platform.getX(), platform.getY());
    }

    private void repaintScore(String string) {
        Font font = new Font("Times New Roman", 20);
        gc.setFont(font);
        gc.fillText(string, 20, 20);
    }

    private void runStartingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 20, 20);
        singleGameButton = new MenuButton(gc, "Single Game");
        multiplayerGameButton = new MenuButton(gc, "Multiplayer");
        singleGameButton.createOnPosition(125, 250);
        multiplayerGameButton.createOnPosition(125, 400);
    }

    private void runConnectingMenu() {
        setText("Press ESCAPE to return start menu", 50, 550);
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 20, 20);
        serverRoomButton = new MenuButton(gc, "Create Room");
        clientRoomButton = new MenuButton(gc, "Find Room");
        serverRoomButton.createOnPosition(125, 250);
        clientRoomButton.createOnPosition(125, 400);
    }

    private void openConnectionSettings(boolean isServer) {
        controller.setIsServer(isServer);
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 20, 20);
        ipTextField = new MenuTextField(gc);
        String ip = inputIp.toString();
        String port = inputPort.toString();
        ipTextField.createTextField(50, 300, ip);
        portTextField = new MenuTextField(gc);
        portTextField.createTextField(50, 350, port);
        submitButton = new MenuButton(gc, "Submit");
        submitButton.createOnPosition(50, 500);
        if (controller.getIsServer()) {
           startButton = new MenuButton(gc, "Start");
           startButton.createOnPosition(250, 500);
        }
    }

    private void openConnectionSettings() {
        openConnectionSettings(false);
    }

    private void setText(String string, double x, double y) {
        Font font = new Font("Times New Roman", 25);
        gc.setFont(font);
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

        keys = controller.getInput();

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
                if (singleGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.SINGLE_GAME;
                if (multiplayerGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CONNECTION_MENU;
            } else if (screenMode == ScreenMode.CONNECTION_MENU) {
                if (serverRoomButton.getBoundary().contains(event.getX(), event.getY())) {
                    screenMode = ScreenMode.SERVER_ROOM;
                }
                if (clientRoomButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.CLIENT_ROOM;
            } else if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                // TODO add restart button when doodle fall
            } else {
                if (ipTextField.getBoundary().contains(event.getX(), event.getY())) {
                    ipActive = true;
                    portActive = false;
                } else if (portTextField.getBoundary().contains(event.getX(), event.getY())) {
                    ipActive = false;
                    portActive = true;
                } else {
                    ipActive = false;
                    portActive = false;
                }
                if (submitButton.getBoundary().contains(event.getX(), event.getY())) {
                    ip = inputIp.toString();
                    port = inputPort.toString();
                    System.out.println(ip + " " + port);
                }
                if (controller.getIsServer() && startButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.MULTIPLAYER_GAME;
            }
        });

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if (screenMode == ScreenMode.SERVER_ROOM || screenMode == ScreenMode.CLIENT_ROOM) {
                if (ipActive) {
                    if (inputIp.length() < 15) {
                        if (event.getCode().isDigitKey()) inputIp.append(code.substring(code.length() - 1));
                        if (code.equals("PERIOD")) inputIp.append(".");

                    }
                    if (code.equals("BACK_SPACE") && inputIp.length() != 0) {
                        inputIp.delete(inputIp.length() - 1, inputIp.length());
                    }
                }

                if (portActive) {
                    if (event.getCode().isDigitKey()) {
                        inputPort.append(code.substring(code.length() - 1));
                    if (code.equals("BACK_SPACE") && inputPort.length() != 0)
                        inputPort.delete(inputPort.length() - 1, inputPort.length());
                    }
                }
            }

            if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                if (code.equals("RIGHT") || code.equals("LEFT")) {
                    direction = code;
                    if (!keys.contains(code)) {
                        keys.add(code);
                    }
                }
                if (controller.ifFall()) {
                    if (code.equals("SPACE")) restartGame();
                    if (code.equals("ESCAPE")) {
                        screenMode = ScreenMode.START_MENU;
                        restartGame();
                    }
                }
            } else if (code.equals("ESCAPE")) {
                screenMode = ScreenMode.START_MENU;
                restartGame();
            }
        });

        scene.setOnKeyReleased(event -> {
            String code = event.getCode().toString();
            if (code.equals("RIGHT") || code.equals("LEFT"))
                keys.remove(code);
        });

        stage.show();

    }

    private void restartGame() {
        controller = new Controller(this);
        keys = controller.getInput();
    }

    public static void main(String[] args) {
        launch(args);
    }
}