package com.example.doodlejumpwithmp;


import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.platform.MovingPlatform;
import com.example.doodlejumpwithmp.model.platform.OneJumpPlatform;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.model.platform.ZeroJumpPlatform;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Controller {
    private final Main main;
    private final Doodle doodle;

    private final ArrayList<Platform> platforms = new ArrayList<>();
    private final ArrayList<String> input = new ArrayList<>();

    private int interval = 100;
    private boolean gameOver = false;
    private boolean isServer; // null if single player. True if user is host.

    private double score = 0;

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public Doodle getDoodle() {
        return doodle;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    public boolean getIsServer() {
        return isServer;
    }

    public boolean ifFall() {
        return gameOver;
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public Controller(Main main) {
        this.main = main;
        Doodle player = new Doodle(Main.doodleImage);
        this.doodle = player;

        Platform oldPlatform = new Platform(Main.platformImage);
        oldPlatform.setPosition(185, Main.getScreenHeight() - 15);
        platforms.add(oldPlatform);
        player.setPosition(185, 560);
        double max = 0;
        while (max < Main.getScreenHeight()) {
            Platform newPlatform = placePlatform(oldPlatform);
            platforms.add(newPlatform);
            max += (oldPlatform.getY() - newPlatform.getY());
            oldPlatform = platforms.get(platforms.size() - 1);
        }
    }

    public boolean containsPlatform(Platform platform, double bottom, double top) {
        return platform.getY() > top && platform.getY() < bottom;
    }

    /* это ведь уже не надо??
    private static boolean getTrueByChance(double number) {
        // double number from 0.0 to 1.0
        return new Random().nextDouble() < number;
    }*/

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

    private Platform createPlatform(Platform previous, double x, double y, double i, double j, int speed) {
        Platform platform;
        int platformNumber = getRandomEventNumber(x, y, i, j);
        // 0 => Classic platform by x% chance
        // 1 => Moving platform by y% chance
        // 2 => One Jump Platform by i% chance
        // 3 => Zero Jump Platform by j% chance
        platform = switch (platformNumber) {
            case 0 -> new Platform(Main.platformImage); // Classic platform by x% chance
            case 1 -> new MovingPlatform(Main.movingPlatformImage).returnWithNewSpeed(speed); // Moving platform by y% chance
            case 2 -> new OneJumpPlatform(Main.oneJumpPlatformImage); // One Jump Platform by i% chance
            case 3 -> new ZeroJumpPlatform(Main.zeroJumpPlatformImage); // Zero Jump Platform by j% chance
            default -> throw new IllegalStateException("Unexpected value: " + platformNumber);
        };
        platform.setPosition(new Random().nextInt(380), previous.getY() - 10 - new Random().nextInt(interval));
        if (!platform.intersectPlatform(previous)) {
            return platform;
        }
        return createPlatform(previous, x, y, i, j, speed);
    }

    public Platform placePlatform(Platform platform) {
        if (score < 3000) {
            interval = 70;
            return createPlatform(platform, 80, 15, 5, 0, 2);
        } else if (score < 7500) {
            interval = 100;
            return createPlatform(platform, 65, 15, 20, 0, 2);
        } else if (score < 15000) {
            interval = 120;
            return createPlatform(platform, 60, 25, 15, 0, 3);
        } else if (score < 25000) {
            interval = 150;
            return createPlatform(platform, 55, 25, 20, 0, 3);
        } else {
            interval = 190;
            return createPlatform(platform, 40, 35, 25, 0, 4);
        }
    }

    public String getScoreString() {
        int intScore = (int) Math.round(score);
        return "Your score:" + intScore;
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
                platforms.add(placePlatform(oldPlatform));
            if (platforms.get(0).getY() > Main.getScreenHeight()) platforms.remove(0);
        }  else if (doodle.getY() > Main.getScreenHeight()) {
            for (Platform platform: platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            if (platforms.get(0).getY() < -100) gameOver = true;
        }

    }

    public void updateCoordinateX() {
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