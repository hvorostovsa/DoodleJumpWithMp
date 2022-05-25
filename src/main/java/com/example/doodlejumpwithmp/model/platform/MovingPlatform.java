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
        this.coordinateX += moveDirection.getValue() * moveSpeed;
        double difference;
        if (this.coordinateX <= 0) {
            difference = coordinateX * -1;
            this.coordinateX = difference;
            moveDirection = moveDirection.getOpposite();
        } else if (this.coordinateX >= Main.SCREEN_WIDTH - getWidth()) {
            difference = this.coordinateX - (Main.SCREEN_WIDTH - getWidth());
            this.coordinateX = (Main.SCREEN_WIDTH - getWidth()) - difference;
            moveDirection = moveDirection.getOpposite();
        }
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
