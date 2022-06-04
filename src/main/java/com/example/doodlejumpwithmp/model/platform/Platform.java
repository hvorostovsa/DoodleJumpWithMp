package com.example.doodlejumpwithmp.model.platform;

import javafx.scene.image.Image;

public class Platform {
    public static final int WIDTH = 70;
    public static final int HEIGHT = 15;

    private double coordinateX;
    private double coordinateY;
    private final Image platformImage;

    public Platform(Image platformImage) {
        this.coordinateX = 0;
        this.coordinateY = 0;
        this.platformImage = platformImage;
    }

    public double getX() {
        return coordinateX;
    }

    public double getY() {
        return coordinateY;
    }

    public void setX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public void setY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public Image getImage() {
        return platformImage;
    }

    public void setPosition(double x, double y) {
        coordinateY = y;
        coordinateX = x;
    }

    public boolean intersectPlatform(Platform platform) {
        double rightX = coordinateX + WIDTH;
        double platformTopRightX = platform.getX() + WIDTH;
        return ((coordinateX >= platform.getX() && coordinateX <= platformTopRightX) ||
                        (rightX >= platform.getX() && rightX <= platformTopRightX));
    }

    public void moveOutFromTheScreen() {
        setX(-100 - getWidth());
    }

    public boolean canJumpToPlatform() {
        return true;
    }

    public boolean jumpFromPlatform() {
        // прям когда собираемся прыгнуть от этой платформы. true - если можем сделать прыжок
        return canJumpToPlatform();
    }

    public void update() {
        // pass
    }
}
