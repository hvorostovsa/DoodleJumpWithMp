package com.example.doodlejumpwithmp;

import javafx.scene.image.Image;

import java.util.ArrayList;

public class Doodle {
    private static final int WIDTH = 60;
    private static final int HEIGHT = 80;
    private double indentLeftLeg = 7;
    private double indentRightLeg = 22;

    private final int maxMoveSpeed = 7;
    private final int minMoveSpeed = 4;
    private final double acceleration = 0.5;
    private int moveDirection = 0; // 0 - not moving; 1 - moving right; -1 - moving left
    private double moveSpeed = 0;

    private final Image characterImage;

    private double coordinateX;
    private double coordinateY;
    private double difY = 0;
    private boolean fall = false;

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
        difY = -11;
    }


    public void moveY(ArrayList<Platform> platforms) {
        difY += 0.3;
        coordinateY += difY;
        fall = difY > 0;
        for (Platform platform : platforms) {
            if (fall && intersectPlatform(platform)) {
                if (platform.canJumpToPlatform()) {
                    jump(); // make jump
                    break;
                }
            }
        }
    }
    public void moveX(String move) {
        if (move.equals("RIGHT")) {
            if (moveDirection == 1) {
                moveSpeed = Math.min(moveSpeed + acceleration, maxMoveSpeed);
            } else {
                moveSpeed = minMoveSpeed;
                moveDirection = 1;
            }
            coordinateX += moveSpeed;
            indentLeftLeg = 7;
            indentRightLeg = 22;
        }
        else if (move.equals("LEFT")) {
            if (moveDirection == -1) {
                moveSpeed = Math.min(moveSpeed + acceleration, maxMoveSpeed);
            } else {
                moveSpeed = minMoveSpeed;
                moveDirection = -1;
            }
            coordinateX -= moveSpeed;
            indentLeftLeg = 22;
            indentRightLeg = 7;
        } else {
            moveDirection = 0;
            moveSpeed = 0;
        }

        cycleDoodle();
    }

    private void cycleDoodle() {
        // if doodle go beyond the right edge of the screen, he will return from the left one
        double difference;
        if (this.coordinateX + WIDTH < 0) {
            difference = -(this.coordinateX + WIDTH);
            this.coordinateX = Main.getScreenWidth() - difference;
        } else if (this.coordinateX > Main.getScreenWidth()) {
            difference = this.coordinateX - Main.getScreenWidth();
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

}
