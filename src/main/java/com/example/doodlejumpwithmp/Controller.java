package com.example.doodlejumpwithmp;


import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.Random;

public class Controller {

    private Main main;

    private ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayList<String> input = new ArrayList<>();
    private Doodle doodle;

    private boolean gameOver = false;

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
    private int interval = 100;

    public Controller(Main main, Doodle player, Image image) {
        this.main = main;
        this.doodle = player;

        Platform oldPlatform = new Platform(image);
        oldPlatform.setPosition(185, 680);
        platforms.add(oldPlatform);
        player.setPosition(185, 560);
        double max = 0;
        while (max < 700) {
            Platform newPlatform = createPlatform(oldPlatform, oldPlatform.getImage(), interval );
            platforms.add(newPlatform);
            max += (oldPlatform.getY() - newPlatform.getY());
            oldPlatform = platforms.get(platforms.size() - 1);
        }
    }

    public boolean containsPlatform(Platform platform, double bottom, double top) {
        return platform.getY() > top && platform.getY() < bottom;
    }

    public Platform createPlatform(Platform previous, Image image, int i) {
        Platform platform = new Platform(image);
        platform.setPosition(new Random().nextInt(380), previous.getY() - new Random().nextInt(i));
        if (!platform.intersectPlatform(previous)) {
            return platform;
        }
        return createPlatform(previous, image, i);
    }

    public void dragScreen() {
        if (doodle.getY() < 300) {
            doodle.setY(300);
            for (Platform platform : platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            Platform oldPlatform = platforms.get(platforms.size() - 1);
            if (!containsPlatform(oldPlatform, 0, interval * (-2)))
                platforms.add(createPlatform(oldPlatform, oldPlatform.getImage(), interval));
            if (platforms.get(0).getY() > 700) platforms.remove(0);
        }  else if (doodle.getY() > 700) {
            for (Platform platform : platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            if (platforms.get(0).getY() < -100) platforms.clear();
            if (platforms.size() == 0) gameOver = true;
        }

    }
    //переименую drawMoveX() и drawJumping()
    public void drawMoveX() {
        if (input.contains("RIGHT")) {
            doodle.moveX("RIGHT");
        }
        if (input.contains("LEFT")) {
            doodle.moveX("LEFT");
        }
    }

    public void drawJumping() {
        doodle.moveY(getPlatforms());
    }

    public void update() {
        System.out.println(platforms.size());
        dragScreen();
        drawJumping();
        drawMoveX();
        main.repaintScene();
    }

}