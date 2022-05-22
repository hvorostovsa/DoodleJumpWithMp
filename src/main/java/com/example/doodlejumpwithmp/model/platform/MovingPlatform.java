package com.example.doodlejumpwithmp.model.platform;

import com.example.doodlejumpwithmp.Main;
import javafx.scene.image.Image;

public class MovingPlatform extends Platform {
    // FAbrickA was here...
    private final int SCREEN_WIDTH = Main.getScreenWidth();
    private final int moveSpeed = 2;  // need to make correlation with the difficulty
    private int moveDirection = 1;  // 1 - right; -1 - left

    public MovingPlatform(Image platformImage) {
        super(platformImage);
    }

    private void move() {
        this.coordinateX += moveDirection * moveSpeed;
        double difference;
        if (this.coordinateX <= 0) {
            difference = coordinateX * -1;
            this.coordinateX = difference;
            moveDirection *= -1;
        } else if (this.coordinateX >= SCREEN_WIDTH - getWidth()) {
            difference = this.coordinateX - (SCREEN_WIDTH - getWidth());
            this.coordinateX = (SCREEN_WIDTH - getWidth()) - difference;
            moveDirection *= -1;
        }
    }

    @Override
    public void update() {
        move();
    }

}
