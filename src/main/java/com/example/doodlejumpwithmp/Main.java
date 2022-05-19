package com.example.doodlejumpwithmp;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    private GraphicsContext gc;

    private Controller controller;

    //заменю getResource
    Image background = new Image("file:C:\\Users\\User\\IdeaProjects\\DoodleJumpWithMp\\src\\main\\resources\\com\\example\\doodlejumpwithmp\\background.png",
            450, 700, false, false);
    Image platformImage = new Image("file:C:\\Users\\User\\IdeaProjects\\DoodleJumpWithMp\\src\\main\\resources\\com\\example\\doodlejumpwithmp\\Normal_platform.png",
            70, 15, false, false);
    Image doodleImage = new Image("file:C:\\Users\\User\\IdeaProjects\\DoodleJumpWithMp\\src\\main\\resources\\com\\example\\doodlejumpwithmp\\Doodle.png",
            60, 80, false, false);


    public void repaintScene() {
        gc.drawImage(background, 0, 0);
        repaintDoodle(controller.getDoodle());

        for (Platform platform : controller.getPlatforms()) {
            repaintPlatforms(platform);
        }
    }

    private void repaintDoodle(Doodle doodle) {
        gc.drawImage(doodle.getImage(), doodle.getX(), doodle.getY());
    }

    private void repaintPlatforms(Platform platform) {
        gc.drawImage(platform.getImage(), platform.getX(), platform.getY());
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
            }
        };
        timer.start();
        stage.show();
        repaintScene();

    }

    public static void main(String[] args) {
        launch(args);
    }
}