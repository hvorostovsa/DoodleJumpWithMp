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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
    private final static int SCREEN_WIDTH = 450;
    private final static int SCREEN_HEIGHT = 700;

    private final static double BUTTON_WIDTH = 180;
    private final static double SMALL_BUTTON_WIDTH = BUTTON_WIDTH / 2;

    private final static int LOGO_WIDTH = 350;
    private final static int LOGO_HEIGHT = 250;

    private String direction = "RIGHT";

    private static final String PACKAGE_NAME = Main.class.getPackage().getName(); // com.example.doodlejumpwithmp
    private static final String RESOURCE_PREFIX = PACKAGE_NAME.replace(".", "/") + "/";

    private StringBuilder inputIp = new StringBuilder("IP: ");
    private StringBuilder inputPort = new StringBuilder("Port: ");

    private String ip;
    private final static int MAX_NUM_IP = 15;
    private String port;
    private final static int MAX_NUM_PORT = 5;

    private GraphicsContext gc;
    private Controller controller;
    private ArrayList<String> rightLeftListener;
    private ScreenMode screenMode = ScreenMode.START_MENU;

    private static MenuButton firstGameButton;
    private static MenuButton secondGameButton;
    private static MenuButton smallGameButton;

    private static MenuTextField ipTextField;
    private boolean ipFieldIsActive = false;
    private static MenuTextField portTextField;
    private boolean portFieldIsActive = false;

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

        repaintScore();
        repaintDoodle(controller.getDoodle());

        if (controller.ifDoodleFall()) {
            gc.drawImage(logoImage, 50, 20);
            firstGameButton = new MenuButton(gc, BUTTON_WIDTH);
            secondGameButton = new MenuButton(gc, BUTTON_WIDTH);

            firstGameButton.createOnPosition(125, 300, "Restart");
            secondGameButton.createOnPosition(125, 450, "Exit to Main Menu");
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

    private void repaintScore() {
        setText(controller.getScoreString(), 20, 20, 20);
    }

    private void runStartingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 50, 20);

        firstGameButton = new MenuButton(gc, BUTTON_WIDTH);
        secondGameButton = new MenuButton(gc, BUTTON_WIDTH);

        firstGameButton.createOnPosition(125, 300, "Single Game");
        secondGameButton.createOnPosition(125, 450, "Multiplayer");
    }

    private void runConnectingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 50, 20);

        firstGameButton = new MenuButton(gc, BUTTON_WIDTH);
        secondGameButton = new MenuButton(gc, BUTTON_WIDTH);
        smallGameButton = new MenuButton(gc, SMALL_BUTTON_WIDTH);

        firstGameButton.createOnPosition(125, 300, "Create Room");
        secondGameButton.createOnPosition(125, 450, "Find Room");
        smallGameButton.createOnPosition(250, 600, "Back");
    }

    private void openConnectionSettings(boolean isServer) {
        controller.setIsServer(isServer);

        gc.drawImage(background, 0, 0);

        String ip = inputIp.toString();
        String port = inputPort.toString();

        ipTextField = new MenuTextField(gc);
        ipTextField.setSelected(ipFieldIsActive);
        ipTextField.createTextField(50, 150, ip);

        portTextField = new MenuTextField(gc);
        portTextField.setSelected(portFieldIsActive);
        portTextField.createTextField(50, 300, port);

        firstGameButton = new MenuButton(gc, BUTTON_WIDTH);
        smallGameButton = new MenuButton(gc, SMALL_BUTTON_WIDTH);

        firstGameButton.createOnPosition(50, 450, "Submit");
        smallGameButton.createOnPosition(180, 600, "Back");

        if (isServer) {
            secondGameButton = new MenuButton(gc, BUTTON_WIDTH);
            secondGameButton.createOnPosition(250, 450, "Start");
        }
    }

    private void openConnectionSettings() {
        openConnectionSettings(false);
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

        rightLeftListener = controller.getInput();

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
                if (firstGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.SINGLE_GAME;
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
                    port = inputPort.substring(6);
                    System.out.println(ip + " " + port);
                } else if (controller.getIsServer() && secondGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screenMode = ScreenMode.MULTIPLAYER_GAME;
                else if (smallGameButton.getBoundary().contains(event.getX(), event.getY())) {
                    inputIp = new StringBuilder("IP: ");
                    inputPort = new StringBuilder("Port: ");

                    ip = null;
                    port = null;

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
                    direction = code;
                    if (!rightLeftListener.contains(code)) {
                        rightLeftListener.add(code);
                    }
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME) {
                String code = event.getCode().toString();
                if (code.equals("RIGHT") || code.equals("LEFT"))
                    rightLeftListener.remove(code);
            }
        });

        stage.show();

    }

    private void restartGame() {
        controller = new Controller(this);
        rightLeftListener = controller.getInput();
    }

    public static void main(String[] args) {
        launch(args);
    }
}