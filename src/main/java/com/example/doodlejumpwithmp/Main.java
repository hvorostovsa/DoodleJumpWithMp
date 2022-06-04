package com.example.doodlejumpwithmp;

import com.example.doodlejumpwithmp.controller.ClientServerController;
import com.example.doodlejumpwithmp.controller.GameController;
import com.example.doodlejumpwithmp.controller.MenuController;
import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.doodle.ShadowDoodle;
import com.example.doodlejumpwithmp.model.platform.Platform;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {
    public final static int SCREEN_WIDTH = 450;
    public final static int SCREEN_HEIGHT = 700;

    private GraphicsContext gc;
    private GameController gameController;
    private ClientServerController clientServerController;
    private ArrayList<KeyCode> controlList;
    private ScreenMode screenMode = ScreenMode.START_MENU;
    private boolean screenModeIsChanged = true;

    static Image background = new Image(
            MainUtils.getFilePathFromResources("background.png"),
            SCREEN_WIDTH, SCREEN_HEIGHT, false, false
    );

    public static Image platformImage = new Image(
            MainUtils.getFilePathFromResources("Normal_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    public static Image movingPlatformImage = new Image(
            MainUtils.getFilePathFromResources("Moving_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    public static Image oneJumpPlatformImage = new Image(
            MainUtils.getFilePathFromResources("One_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    public static Image zeroJumpPlatformImage = new Image(
            MainUtils.getFilePathFromResources("Zero_jump_platform.png"),
            Platform.getWidth(), Platform.getHeight(), false, false
    );
    public static Image doodleImage = new Image(
            MainUtils.getFilePathFromResources("Doodle.png"),
            Doodle.getWidth(), Doodle.getHeight(), false, false
    );
    public static Image shadowDoodleImage = new Image(
            MainUtils.getFilePathFromResources("ShadowDoodle.png"),
            ShadowDoodle.getWidth(), ShadowDoodle.getHeight(), false, false
    );

    public void setScreenMode(ScreenMode screenMode) {
        this.screenMode = screenMode;
    }

    public void changeScreenMode() {
        screenModeIsChanged = true;
    }

    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        for (Platform platform : gameController.getPlatforms()) {
            repaintPlatforms(platform);
        }

        repaintScore();
        repaintShadowDoodles();
        repaintDoodle(gameController.getDoodle());

        if (gameController.isDoodleFall()) {
            screenMode = ScreenMode.GAME_OVER_MENU;
            screenModeIsChanged = true;
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
        for (ShadowDoodle shadowDoodle : gameController.getShadowDoodles().values()) {
            if (shadowDoodle.getLoose()) continue;
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
        Font font = new Font("Times New Roman", 20);
        gc.setFont(font);
        gc.setFill(Color.BLACK);
        gc.fillText(gameController.getScoreString(), 20, 20);
    }

    @Override
    public void start(Stage stage) throws IOException {
        gameController = new GameController(this);
        clientServerController = new ClientServerController(this, gameController);
        stage.setResizable(false);
        stage.setTitle("Doodle Jump");
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

        controlList = gameController.getInput();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (screenModeIsChanged) {
                    System.out.println(1);
                    screenModeIsChanged = false;
                    switch (screenMode) {
                        case SINGLE_GAME, MULTIPLAYER_GAME -> {
                            gameController.initialGamePreparations();
                            stage.setScene(scene);
                        }
                        case GAME_OVER_MENU -> {
                            group.getChildren().remove(scoreText);
                            scoreText.setText(gameController.getScoreString());
                            group.getChildren().add(scoreText);
                            reinitializeGame(); // TODO wrap in method
                            stage.setScene(gameOverScene);
                        }
                        case START_MENU -> stage.setScene(startMenuScene);
                        case CONNECTION_MENU -> stage.setScene(connectionMenuScene);
                        case SERVER_ROOM -> stage.setScene(serverRoomScene);
                        case CLIENT_ROOM -> stage.setScene(clientRoomScene);
                    }
                }
                if (screenMode == ScreenMode.SINGLE_GAME || screenMode == ScreenMode.MULTIPLAYER_GAME)
                    gameController.update();
                else if (screenMode == ScreenMode.SERVER_ROOM)
                    clientServerController.beforeConnectionStart(true);
                else if (screenMode == ScreenMode.CLIENT_ROOM)
                    clientServerController.beforeConnectionStart();

            }
        };
        timer.start();

        scene.setOnKeyPressed(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.RIGHT || code == KeyCode.LEFT) {
                if (!controlList.contains(code)) {
                    controlList.add(code);
                }
            }
        });

        scene.setOnKeyReleased(event -> {
            KeyCode code = event.getCode();
            if (code == KeyCode.RIGHT || code == KeyCode.LEFT)
                controlList.remove(code);
        });

        stage.show();
    }

    private Scene createScene(ScreenMode screenMode) throws IOException {
        FXMLLoader loader;

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


        Group root = loader.load();
        MenuController menuController = loader.getController();
        menuController.initData(this, gameController, clientServerController);


        return new Scene(root, Main.SCREEN_WIDTH, Main.SCREEN_HEIGHT);
    }

    private Text createScoreText() {
        Text score = new Text();
        Font font = new Font("Times New Roman", 24);
        score.setFont(font);
        score.setX(150);
        score.setY(600);
        return score;
    }

    private void reinitializeGame() {
        gameController.setDefaultValues();
    }

    public static void main(String[] args) {
        launch(args);
    }
}