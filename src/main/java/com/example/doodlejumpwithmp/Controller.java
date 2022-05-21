package com.example.doodlejumpwithmp;


import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

public class Controller {
    private final Main main;
    private final Doodle doodle;

    private final ArrayList<Platform> platforms = new ArrayList<>();
    private final ArrayList<String> input = new ArrayList<>();

    private int interval = 100;
    private boolean gameOver = false;

    private int score = 0;

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public Doodle getDoodle() {
        return doodle;
    }

    public boolean ifFall() {
        return gameOver;
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public Controller(Main main, Image doodleImage, Image platformImage) {
        this.main = main;
        Doodle player = new Doodle(doodleImage);
        this.doodle = player;

        Platform oldPlatform = new Platform(platformImage);
        oldPlatform.setPosition(185, Main.getScreenHeight() - 15);
        platforms.add(oldPlatform);
        player.setPosition(185, 560);
        double max = 0;
        while (max < Main.getScreenHeight()) {
            Platform newPlatform = createPlatform(oldPlatform, interval);
            platforms.add(newPlatform);
            max += (oldPlatform.getY() - newPlatform.getY());
            oldPlatform = platforms.get(platforms.size() - 1);
        }
    }

    public boolean containsPlatform(Platform platform, double bottom, double top) {
        return platform.getY() > top && platform.getY() < bottom;
    }

    private static boolean getTrueByChance(double number) {
        // double number from 0.0 to 1.0
        return new Random().nextDouble() < number;
    }

    public Platform createPlatform(Platform previous, int interval) {
        Platform platform;
        if (getTrueByChance(0.7)) {  // Classic platform by 70% chance
            platform = new Platform(Main.platformImage);
        } else if (getTrueByChance(0.67)) { // Moving platform by 20% chance
            platform = new MovingPlatform(Main.movingPlatformImage);
        } else { // One Jump Platform by 10% chance
            platform = new OneJumpPlatform(Main.oneJumpPlatformImage);
        }
        platform.setPosition(new Random().nextInt(380), previous.getY() - 10 - new Random().nextInt(interval));
        if (!platform.intersectPlatform(previous)) {
            return platform;
        }
        return createPlatform(previous, interval);
    }

    public String getScoreString() {
        return "Your score:" + score;
    }

    public void dragScreen() {
        if (doodle.getY() < 300) {
            doodle.setY(300);
            for (Platform platform : platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            score += doodle.getDifY() * -1;
            Platform oldPlatform = platforms.get(platforms.size() - 1);
            if (!containsPlatform(oldPlatform, 0, interval * (-2)))
                platforms.add(createPlatform(oldPlatform, interval));
            if (platforms.get(0).getY() > Main.getScreenHeight()) platforms.remove(0);
        }  else if (doodle.getY() > Main.getScreenHeight()) {
            for (Platform platform: platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            if (platforms.get(0).getY() < -100) gameOver = true;
        }

    }

    public void updateCoordinateX() {
//        System.out.println(input);
        if (input.contains("RIGHT")) {
            if (input.contains("LEFT")) {
                doodle.moveX(0); // no move
            } else {
                doodle.moveX(1); // move right
            }
        } else if (input.contains("LEFT")) {
            doodle.moveX(-1); // move left
        } else {
            doodle.moveX(0); // no move
        }
    }

    public void updateCoordinateY() {
        doodle.moveY(getPlatforms());
    }

    private void updatePlatforms() {
        for (Platform platform: platforms) {
            platform.update();
        }
    }

    public void update() {
        dragScreen();
        updateCoordinateY();
        updateCoordinateX();
        updatePlatforms();
        main.repaintScene();
    }

}