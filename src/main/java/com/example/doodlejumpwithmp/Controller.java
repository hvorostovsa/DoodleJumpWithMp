package com.example.doodlejumpwithmp;


import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

    private static int getRandomEventNumber(Double... numbers) {
        // pass numbers (1, 1.5, 2.5). Get 0 by chance 20%, 1 by chance 30%, 2 by chance 50%
        ArrayList<Double> newNumbers = new ArrayList<>(List.of(numbers));
        double sum = newNumbers.get(0);
        double currentNumber;
        for (int i = 1; i < numbers.length; i++) {
            currentNumber = newNumbers.get(i);
            sum += currentNumber;
            newNumbers.set(i, currentNumber + newNumbers.get(i - 1));
        }
        double randomNumber = new Random().nextDouble() * sum;
        int resultIndex = 0;
        while (randomNumber > newNumbers.get(resultIndex)) {
            resultIndex += 1;
        }
        return resultIndex;
    }

    public Platform createPlatform(Platform previous, int interval) {
        Platform platform;
        int platformNumber = getRandomEventNumber(65., 20., 10., 5.);
        // 0 => Classic platform by 65% chance
        // 1 => Moving platform by 20% chance
        // 2 => One Jump Platform by 10% chance
        // 3 => Zero Jump Platform by 5% chance
        if (platformNumber == 0) {  // Classic platform
            platform = new Platform(Main.platformImage);
        } else if (platformNumber == 1) { // Moving platform
            platform = new MovingPlatform(Main.movingPlatformImage);
        } else if (platformNumber == 2) { // One Jump Platform
            platform = new OneJumpPlatform(Main.oneJumpPlatformImage);
        } else {
            platform = new ZeroJumpPlatform(Main.zeroJumpPlatformImage);
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