package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;

import java.util.ArrayList;

public class Doodle {
    private Image characterImage;

    private double coordinateX;
    private double coordinateY;
    private double difY = 0;
    private boolean fall = false;
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
        fall = difY > 0;
        for (Platform platform : platforms) {
            if (fall && intersectPlatform(platform)) {
                difY = -11;
                break;
            }
        }
    }
    //добавлю отражение дудла
    public void moveX(String move) {
        if (move.equals("RIGHT")) {
            coordinateX += 4;
        }
        else if (move.equals("LEFT")){
            coordinateX -= 4;
        }
    }


    //переделаю на отталкивание от ног а не концов картинки
    public boolean intersectPlatform(Platform platform) {
        double bottomY = coordinateY + characterImage.getHeight();
        double rightX = coordinateX + characterImage.getWidth();
        double platformTopRightX = platform.getX() + platform.getImage().getWidth();
        return (bottomY > platform.getY() && bottomY < platform.getY() + 15) &&
                ((coordinateX >= platform.getX() && coordinateX <= platformTopRightX) ||
                        (rightX >= platform.getX() && rightX <= platformTopRightX));
    }

}
