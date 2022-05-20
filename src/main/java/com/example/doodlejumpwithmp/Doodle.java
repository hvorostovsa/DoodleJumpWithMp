package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;


import java.io.IOException;
import java.util.ArrayList;

public class Doodle {
//    private final int maxMoveSpeed = 8;
//    private final int minMoveSpeed = 4;
//    private final double acceleration = 0.5;
//    private final double currentSpeed = 0;
//    private final int moveDirection = 0; // 0 - not moving; 1 - moving right; -1 - moving left

    private final int moveSpeed = 6;

    private final Image characterImage;

    private double coordinateX;
    private double coordinateY;
    private double difY = 0;
    private boolean fall = false;
    private double score = 0;
    private double physY = 0;

    private double INDENT_LEFT_LEG = 7;
    private double INDENT_RIGHT_LEG = 22;

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

    public Doodle(Image characterImage) throws IOException {
        this.coordinateY = 0;
        this.coordinateX = 0;
        this.characterImage = characterImage;
    }

    public void jump() {
        difY = -11;
    }

    public void moveY(ArrayList<Platform> platforms) {
        difY += 0.3;
        coordinateY += difY;
        fall = difY > 0;
        for (Platform platform : platforms) {
            if (fall && intersectPlatform(platform)) {
                jump(); // make jump
                break;
            }
        }
    }
    //добавлю отражение дудла
    public void moveX(String move) {
        if (move.equals("RIGHT")) {
            coordinateX += moveSpeed;
        }
        else if (move.equals("LEFT")){
            coordinateX -= moveSpeed;
        }
    }

    public boolean intersectPlatform(Platform platform) {
        double leftLeg = coordinateX + INDENT_LEFT_LEG; //получать координаты ног с отдельного класса
        double rightLeg = coordinateX + characterImage.getWidth() - INDENT_RIGHT_LEG;
        double bottomY = coordinateY + characterImage.getHeight();
        double platformTopRightX = platform.getX() + platform.getImage().getWidth();
        return (bottomY > platform.getY() && bottomY < platform.getY() + 10) &&
                ((leftLeg >= platform.getX() && leftLeg <= platformTopRightX) ||
                        (rightLeg >= platform.getX() && rightLeg <= platformTopRightX));
    }

}
