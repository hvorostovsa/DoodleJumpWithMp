package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Doodle {
    private final Image characterImage;

    private double coordinateX;
    private double coordinateY;
    private double difY = 0;
    private double score = 0;
    private double physY = 0;

    public double getX() {
        return coordinateX;
    }

    public double getY() {
        return coordinateY;
    }

    public void setY(double i) {
        coordinateY = i;
    }

    public double getDifY() {
        return difY;
    }

    public void setPosition(double x, double y) {
        coordinateX = x;
        coordinateY = y;
    }

    public Image getImage() {
        return characterImage;
    }

    public Doodle(Image characterImage) {
        this.coordinateY = 0;
        this.coordinateX = 0;
        this.characterImage = characterImage;
    }

    public void moveY(ArrayList<Platform> platforms) {
        difY += 0.3;
        coordinateY += difY;
        boolean fall = difY > 0;
        for (Platform platform : platforms) {
            if (fall && intersectPlatform(platform)) {
                jump(); // make jump
                break;
            }
        }
    }

    public void jump() {
        difY = -11;
    }

    // добавлю отражение дудла
    public void moveX(String move) {
        if (move.equals("RIGHT")) {
            coordinateX += 4;
        }
        else if (move.equals("LEFT")){
            coordinateX -= 4;
        }
    }


    public boolean intersectPlatform(Platform platform) {
        double leftLeg = coordinateX + 7; // получать координаты ног с отдельного класса
        double rightLeg = leftLeg + 38 - 7;
        double bottomY = coordinateY + characterImage.getHeight();
        double platformTopRightX = platform.getX() + platform.getImage().getWidth();
        return (bottomY > platform.getY() && bottomY < platform.getY() + 15) &&
                ((leftLeg >= platform.getX() && leftLeg <= platformTopRightX) ||
                        (rightLeg >= platform.getX() && rightLeg <= platformTopRightX));
    }

}
