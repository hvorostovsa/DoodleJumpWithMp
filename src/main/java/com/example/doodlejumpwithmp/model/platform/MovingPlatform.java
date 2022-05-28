package com.example.doodlejumpwithmp.model.platform;

import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.model.Direction;
import javafx.scene.image.Image;

public class MovingPlatform extends Platform {
    private int moveSpeed = 2;
    private Direction moveDirection = Direction.RIGHT;  // 1 - right; -1 - left

    public MovingPlatform(Image platformImage) {
        super(platformImage);
    }

    private void move() {
        double coordinateX = getX() + moveDirection.getValue() * moveSpeed;
        if (coordinateX <= 0) {
            coordinateX = coordinateX * -1;
            moveDirection = moveDirection.getOpposite();
        } else if (coordinateX >= Main.SCREEN_WIDTH - getWidth()) {
            coordinateX = 2 * (Main.SCREEN_WIDTH - getWidth()) - coordinateX;
            moveDirection = moveDirection.getOpposite();
        }
        setX(coordinateX);
    }

    public Platform returnWithNewSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
        return this;
    }

    @Override
    public void update() {
        move();
    }
}
