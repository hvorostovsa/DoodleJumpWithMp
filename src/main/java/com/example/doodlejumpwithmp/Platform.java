package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;

public class Platform {

    private double coordinateX;
    private double coordinateY;

    public double getX() {
        return coordinateX;
    }

    public double getY() {
        return coordinateY;
    }

    private Image platformImage;

    public Image getImage() {
        return platformImage;
    }

    public Platform(Image platformImage) {
        this.coordinateX = 0;
        this.coordinateY = 0;
        this.platformImage = platformImage;
    }

    public void setPosition(double x, double y) {
        coordinateY = y;
        coordinateX = x;
    }

}
