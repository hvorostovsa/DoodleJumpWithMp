package com.example.doodlejumpwithmp.model.doodle;

import javafx.scene.image.Image;

public class ShadowDoodle extends Doodle {
    // Other players will be shown as ShadowDoodle

    private int lastDirection = 0;
    private boolean loose = false;

    public ShadowDoodle(Image characterImage) {
        super(characterImage);
    }

    @Override
    public void moveX(int newDirection) {
        if (isOnScreen()) {
            super.moveX(newDirection);
        }
    }

    public void setLastDirection(int lastDirection) {
        this.lastDirection = lastDirection;
    }

    public int getLastDirection() {
        return lastDirection;
    }

    public void setLoose(boolean loose) {
        this.loose = loose;
    }

    public boolean getLoose() {
        return this.loose;
    }

    public void moveX() {
        moveX(lastDirection);
    }

    public boolean isOnScreen() {
        // TODO Semyon please do it
        return true;
    }
}
