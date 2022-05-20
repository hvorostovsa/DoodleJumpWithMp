package com.example.doodlejumpwithmp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class Main extends Application {
    private GraphicsContext gc;
    private Controller controller;

    private static final String PACKAGE_NAME = Main.class.getPackage().getName(); // com.example.doodlejumpwithmp
    private static final String RESOURCE_PREFIX = PACKAGE_NAME.replace(".", "/") + "/";

    Image background = new Image(
            getFilePathFromResources("background.png"),
            450, 700, false, false
    );
    Image platformImage = new Image(
            getFilePathFromResources("Normal_platform.png"),
            70, 15, false, false
    );
    Image doodleImage = new Image(
            getFilePathFromResources("Doodle.png"),
            60, 80, false, false
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


    public void repaintScene() {
        gc.drawImage(background, 0, 0);

        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }
        repaintDoodle(controller.getDoodle());


    }

    private void repaintDoodle(Doodle doodle) {
        gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
        //gc.drawImage(doodle.getImage(), doodle.getX()  + doodle.getImage().getWidth(), doodle.getY(), -1 * doodle.getImage().getWidth(), doodle.getImage().getHeight());
    }

    private void repaintPlatforms(Platform platform) {
        gc.drawImage(platform.getImage(), platform.getX(), platform.getY());
    }

    private Text setGameOverText(String string) {
        Text gameOverText = new Text();
        gameOverText.setX(170);
        gameOverText.setY(300);
        Font font = new Font("Times New Roman", 25);
        gameOverText.setFont(font);
        gameOverText.setText(string);
        return gameOverText;
    }

    @Override
    public void start(Stage stage) throws IOException {

        Doodle player = new Doodle(doodleImage);

        stage.setResizable(false);

        Group root = new Group();

        Scene scene = new Scene(root, 450, 700);
        stage.setScene(scene);

        Canvas canvas = new Canvas(scene.getWidth(), scene.getHeight());
        root.getChildren().add(canvas);
        this.gc = canvas.getGraphicsContext2D();

        controller = new Controller(this, player, platformImage);

        ArrayList<String> keys = controller.getInput();

        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            if ( !keys.contains(code) )
                keys.add(code);

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
                    stop();
                    root.getChildren().add(setGameOverText("Game over"));

                }
            }
        };
        timer.start();stage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}