package com.example.doodlejumpwithmp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
//    private final int FPS = 60;  // max fps rate
    private final static int SCREEN_WIDTH = 450;
    private final static int SCREEN_HEIGHT = 700;

    private String mirror = "RIGHT";

    private static final String PACKAGE_NAME = Main.class.getPackage().getName(); // com.example.doodlejumpwithmp
    private static final String RESOURCE_PREFIX = PACKAGE_NAME.replace(".", "/") + "/";

    private GraphicsContext gc;
    private Controller controller;
    ArrayList<String> keys;

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
    static Image doodleImage = new Image(
            getFilePathFromResources("Doodle.png"),
            Doodle.getWidth(), Doodle.getHeight(), false, false
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

        repaintScore(controller.getScore());
        repaintDoodle(controller.getDoodle());
    }

    private void repaintDoodle(Doodle doodle) {
        if (mirror.equals("RIGHT")) {
            gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
        } else gc.drawImage(
                doodle.getImage(),
                doodle.getX()  + doodle.getImage().getWidth(), doodle.getY(),
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


    private void setGameOverText(String string) {
        Font font = new Font("Times New Roman", 25);
        gc.setFont(font);
        gc.fillText(string, 50, 300);
    }

    @Override
    public void start(Stage stage) throws IOException {

        stage.setResizable(false);

        Group root = new Group();
        Scene scene = new Scene(root, SCREEN_WIDTH, SCREEN_HEIGHT);
        stage.setScene(scene);

        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        root.getChildren().add(canvas);
        this.gc = canvas.getGraphicsContext2D();

        controller = new Controller(this, doodleImage, platformImage);

        keys = controller.getInput();

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            mirror = code;
            if (!keys.contains(code)) {
                keys.add(code);
            }
            if (code.equals("SPACE")) {
                restartGame();
            }
        });

        scene.setOnKeyReleased(event -> {
            String code = event.getCode().toString();
            keys.remove(code);

        });

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.update();
                if (controller.ifFall()) {
                    setGameOverText("Game Over! Press Space to restart");
                }
            }
        };
        timer.start();
        stage.show();

    }

    private void restartGame() {
        if (controller.ifFall()) {
            controller = new Controller(this, doodleImage, platformImage);
            keys = controller.getInput();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}