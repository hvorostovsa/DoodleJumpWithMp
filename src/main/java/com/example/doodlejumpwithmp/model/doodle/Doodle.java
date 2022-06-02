package com.example.doodlejumpwithmp.model.doodle;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.model.Direction;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.controller.serverwork.ServerParameter;
import javafx.scene.image.Image;

import java.util.List;

public class Doodle {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 80;
    private static final int BASE_INDENT_LEFT_LEG = 7;
    private static final int BASE_INDENT_RIGHT_LEG = 22;

    private static final int MAX_MOVE_SPEED = 7;
    private static final int MIN_MOVE_SPEED = 4;
    private static final double ACCELERATION = 0.5;

    private double indentLeftLeg = BASE_INDENT_LEFT_LEG;
    private double indentRightLeg = BASE_INDENT_RIGHT_LEG;

    private Direction moveDirection = Direction.NONE; // 0 - not moving; 1 - moving right; -1 - moving left
    private double moveSpeed = 0;

    private final Image characterImage;

    private double coordinateX;
    private double coordinateY;
    private double diffY = 0;
    private boolean loose = false;
    private Direction doodleSide = Direction.RIGHT; // 1 => doodle sees to the right side. -1 => to the left side

    public void setLoose(boolean loose) {
        this.loose = loose;
    }

    public boolean getLoose() {
        return this.loose;
    }

    public double getX() {
        return coordinateX;
    }

    public double getY() {
        return coordinateY;
    }

    public void setY(double coordinateY) {
        this.coordinateY = coordinateY;
    }

    public void setX(double coordinateX) {
        this.coordinateX = coordinateX;
    }

    public double getDiffY() {
        return diffY;
    }

    public void setDiffY(double diffY) {
        this.diffY = diffY;
    }

    public void setPosition(double x, double y) {
        coordinateX = x;
        coordinateY = y;
    }

    public double getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(double moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    public Direction getMoveDirection() {
        return moveDirection;
    }

    public void setMoveDirection(Direction moveDirection) {
        this.moveDirection = moveDirection;
    }

    public Direction getDoodleSide() {
        return doodleSide;
    }

    public void setDoodleSide(Direction doodleSide) {
        this.doodleSide = doodleSide;
    }

    public Image getImage() {
        return characterImage;
    }

    public Doodle(Image characterImage){
        this.coordinateY = 0;
        this.coordinateX = 0;
        this.characterImage = characterImage;
    }

    public static int getWidth() {
        return WIDTH;
    }

    public static int getHeight() {
        return HEIGHT;
    }

    public void jump() {
        diffY = -11;
    }

    public void moveY(List<Platform> platforms, boolean isShadow) {
        diffY += 0.3;
        coordinateY += diffY;
        if (diffY > 0) {
            for (Platform platform : platforms) {
                if (intersectPlatform(platform) && platform.canJumpToPlatform()) {
                    if (isShadow || platform.jumpFromPlatform()) {
                        jump();
                        break;
                    }
                }
            }
        }
    }

    public void moveY(List<Platform> platforms) {
        moveY(platforms, false);
    }

    public void moveX(Direction newDirection) {
        if (newDirection != Direction.NONE) { // if need to move
            // * make move
            doodleSide = newDirection;
            if (newDirection == moveDirection) {
                moveSpeed = Math.min(moveSpeed + ACCELERATION, MAX_MOVE_SPEED);
            } else {
                moveSpeed = MIN_MOVE_SPEED;
                moveDirection = newDirection;
            }
            coordinateX += moveDirection.getValue() * moveSpeed;
            // *

            // * change indents if doodleImage mirrored
            if (moveDirection == Direction.RIGHT) { // move right
                indentLeftLeg = BASE_INDENT_LEFT_LEG;
                indentRightLeg = BASE_INDENT_RIGHT_LEG;
            } else { // move left
                indentLeftLeg = BASE_INDENT_RIGHT_LEG;
                indentRightLeg = BASE_INDENT_LEFT_LEG;
            }
            // *
        } else {
            moveDirection = newDirection;
        }

        cycleDoodle();
    }

    private void cycleDoodle() {
        // if doodle go beyond the right edge of the screen, he will return from the left one
        double difference;
        if (this.coordinateX + WIDTH < 0) {
            difference = -(this.coordinateX + WIDTH);
            this.coordinateX = Main.SCREEN_WIDTH - difference;
        } else if (this.coordinateX > Main.SCREEN_WIDTH) {
            difference = this.coordinateX - Main.SCREEN_WIDTH;
            this.coordinateX = difference - WIDTH;
        }
    }

    public boolean intersectPlatform(Platform platform) {
        double leftLeg = coordinateX + indentLeftLeg;
        double rightLeg = coordinateX + characterImage.getWidth() - indentRightLeg;
        double bottom = coordinateY + characterImage.getHeight();
        double platformTopRightX = platform.getX() + platform.getImage().getWidth();
        return (bottom > platform.getY() && bottom < platform.getY() + 15) &&
                ((leftLeg >= platform.getX() && leftLeg <= platformTopRightX) ||
                        (rightLeg >= platform.getX() && rightLeg <= platformTopRightX));
    }

    public JSONObject collectData() {
        //  get pos, speed, loose, lastMove and put it into JSONObject
        JSONObject data = new JSONObject();
        if (loose) {
            data.put(ServerParameter.LOOSE.toString(), true);
        } else {
            data.put(ServerParameter.COORDINATE_X.toString(), this.coordinateX);
            data.put(ServerParameter.COORDINATE_Y.toString(), this.coordinateY);
            data.put(ServerParameter.MOVE_DIRECTION.toString(), this.moveDirection.getValue());
            data.put(ServerParameter.MOVE_SPEED.toString(), this.moveSpeed);
            data.put(ServerParameter.DOODLE_SIDE.toString(), this.doodleSide.getValue());
            data.put(ServerParameter.DIFF_Y.toString(), this.getDiffY());
        }
        return data;
    }
}
