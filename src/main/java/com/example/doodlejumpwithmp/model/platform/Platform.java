package com.example.doodlejumpwithmp.model.platform;

import javafx.scene.image.Image;

public class Platform {
    private static final int WIDTH = 70;
    private static final int HEIGHT = 15;

    private double coordinateX; // package private. Getters and setters are bad!!
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
        double rightX = coordinateX + platformImage.getWidth();
        double platformTopRightX = platform.getX() + platform.getImage().getWidth();
        return ((coordinateX >= platform.getX() && coordinateX <= platformTopRightX) ||
                        (rightX >= platform.getX() && rightX <= platformTopRightX));
    }

    public void moveOutFromTheScreen() {
        setX(-100 - getWidth());
    }

    public boolean canJumpToPlatform() {
        return true;
    }

    public void update() {
        // pass
    }
}
