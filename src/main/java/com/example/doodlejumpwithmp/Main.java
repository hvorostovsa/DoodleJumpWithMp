package com.example.doodlejumpwithmp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
    //    private final int FPS = 60;  // max fps rate
    private final static int SCREEN_WIDTH = 450;
    private final static int SCREEN_HEIGHT = 700;
    private final static int MENU_BUTTON_WIDTH = 150;
    private final static int MENU_BUTTON_HEIGHT = 100;
    private final static int LOGO_WIDTH = 400;
    private final static int LOGO_HEIGHT = 100;

    private String mirror = "RIGHT";

    private static final String PACKAGE_NAME = Main.class.getPackage().getName(); // com.example.doodlejumpwithmp
    private static final String RESOURCE_PREFIX = PACKAGE_NAME.replace(".", "/") + "/";

    private GraphicsContext gc;
    private Controller controller;
    ArrayList<String> keys;
    private String screen = "Starting Menu";
    private Boolean canStart = false;

    private static MenuButton singleGameButton;
    private static MenuButton multiplayerGameButton;
    private static MenuButton createRoomButton;
    private static MenuButton connectRoomButton;

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
    static Image menuButtonImage = new Image(
            getFilePathFromResources("MenuButton.png"),
            MENU_BUTTON_WIDTH, MENU_BUTTON_HEIGHT, false, false
    );
    static Image logoImage = new Image(
            getFilePathFromResources("Logo.png"),
            LOGO_WIDTH, LOGO_HEIGHT, false, false
    );


    private static String getFilePathFromResources(String filename) {
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


    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }

        repaintScore(controller.getScoreString());
        repaintDoodle(controller.getDoodle());
    }

    private void repaintDoodle(Doodle doodle) {
        if (mirror.equals("RIGHT")) {
            gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
        } else gc.drawImage(
                doodle.getImage(),
                doodle.getX() + doodle.getImage().getWidth(), doodle.getY(),
                -1 * doodle.getImage().getWidth(), doodle.getImage().getHeight());
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
        singleGameButton = new MenuButton(gc, menuButtonImage, "Single Game");
        multiplayerGameButton = new MenuButton(gc, menuButtonImage, "Multiplayer");
        singleGameButton.createOnPosition(100, 250);
        multiplayerGameButton.createOnPosition(190, 350);
    }

    private void runConnectingMenu() {
        gc.drawImage(background, 0, 0);
        gc.drawImage(logoImage, 20, 20);
        createRoomButton = new MenuButton(gc, menuButtonImage, "Create Room");
        connectRoomButton = new MenuButton(gc, menuButtonImage, "Find Room");
        createRoomButton.createOnPosition(100, 250);
        connectRoomButton.createOnPosition(190, 350);
    }

    private void openServerSettings(Group root, AnimationTimer timer) {
        timer.stop();
        ImageView iv = new ImageView(background);
        Button submitButton = new Button("Submit");
        submitButton.setTranslateX(50);
        submitButton.setTranslateY(400);
        submitButton.setPrefWidth(80);
        submitButton.setPrefHeight(20);
        Text text = new Text("Everything is ready. Press F to start");
        Font font = new Font("Times New Roman", 15);
        text.setFont(font);
        text.setX(150);
        text.setY(415);
        submitButton.setOnAction(event -> {
                    screen = "Game";
                    root.getChildren().add(text);
                }
        );
        TextField tfID = new TextField("Enter Your ID here");
        tfID.setTranslateX(50);
        tfID.setTranslateY(300);
        tfID.setPrefWidth(350);
        tfID.setPrefHeight(20);
        TextField tfPort = new TextField("Enter Your port here");
        tfPort.setTranslateX(50);
        tfPort.setTranslateY(350);
        tfPort.setPrefWidth(350);
        tfPort.setPrefHeight(20);
        root.getChildren().clear();
        root.getChildren().add(iv);
        root.getChildren().add(submitButton);
        root.getChildren().add(tfID);
        root.getChildren().add(tfPort);

    }

    private void openClientSettings(Group root, AnimationTimer timer) {
        timer.stop();
        ImageView iv = new ImageView(background);
        Button submitButton = new Button("Submit");
        submitButton.setTranslateX(50);
        submitButton.setTranslateY(400);
        submitButton.setPrefWidth(80);
        submitButton.setPrefHeight(20);
        Text text = new Text("Everything is ready. Waiting for server");
        Font font = new Font("Times New Roman", 15);
        text.setFont(font);
        text.setX(150);
        text.setY(415);
        submitButton.setOnAction(event -> {
                    screen = "Game";
                    root.getChildren().add(text);
                }
        );
        TextField tfID = new TextField("Enter Your ID here");
        tfID.setTranslateX(50);
        tfID.setTranslateY(300);
        tfID.setPrefWidth(350);
        tfID.setPrefHeight(20);
        TextField tfPort = new TextField("Enter Your port here");
        tfPort.setTranslateX(50);
        tfPort.setTranslateY(350);
        tfPort.setPrefWidth(350);
        tfPort.setPrefHeight(20);
        root.getChildren().clear();
        root.getChildren().add(iv);
        root.getChildren().add(submitButton);
        root.getChildren().add(tfID);
        root.getChildren().add(tfPort);
    }

    private void setGameOverText(String string) {
        Font font = new Font("Times New Roman", 25);
        gc.setFont(font);
        gc.fillText(string, 50, 300);
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

        controller = new Controller(this, doodleImage, platformImage);

        keys = controller.getInput();


        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                switch (screen) {
                    case "Game" -> {
                        controller.update();
                        if (controller.ifFall()) {
                            setGameOverText("Game Over! Press Space to restart");
                        }
                    }
                    case "Starting Menu" -> runStartingMenu();
                    case "Connecting Menu" -> runConnectingMenu();
                    case "Create Room" -> openServerSettings(root, this);
                    case "Connect room" -> openClientSettings(root, this);
                }
            }
        };
        timer.start();

        scene.setOnMouseClicked(event -> {
            if (screen.equals("Starting Menu")) {
                if (singleGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screen = "Game";
                if (multiplayerGameButton.getBoundary().contains(event.getX(), event.getY()))
                    screen = "Connecting Menu";
            } else if (screen.equals("Connecting Menu")) {
                if (createRoomButton.getBoundary().contains(event.getX(), event.getY())) {
                    screen = "Create Room";
                }
                if (connectRoomButton.getBoundary().contains(event.getX(), event.getY()))
                    screen = "Connect room";
            }
        });

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();

            if (screen.equals("Game")) {
                mirror = code;
                if (!keys.contains(code)) {
                    keys.add(code);
                }
                if (code.equals("SPACE") && controller.ifFall()) {
                    restartGame();
                }
            }

            if (code.equals("F")) {
                root.getChildren().clear();
                root.getChildren().add(canvas);
                this.gc = canvas.getGraphicsContext2D();
                timer.start();
            }
            if (code.equals("ESCAPE")) {
                screen = "Starting Menu";
                restartGame();
            }
        });

        scene.setOnKeyReleased(event -> {
            String code = event.getCode().toString();
            keys.remove(code);
        });

        stage.show();

    }

    private void restartGame() {
        controller = new Controller(this, doodleImage, platformImage);
        keys = controller.getInput();
    }

    public static void main(String[] args) {
        launch(args);
    }
}