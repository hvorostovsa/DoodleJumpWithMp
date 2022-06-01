package com.example.doodlejumpwithmp.model.doodle;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.model.Direction;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.controller.serverwork.ServerParameter;
import javafx.scene.image.Image;

import java.util.List;

public class ShadowDoodle extends Doodle {
    // Other players will be shown as ShadowDoodle

    private Direction lastDirection = Direction.NONE;
    private int clientId;
    private double score = 0.;

    private JSONObject lastDataGot;

    public ShadowDoodle(Image characterImage) {
        super(characterImage);
    }

    @Override
    public void moveX(Direction newDirection) {
        if (isOnScreen()) {
            super.moveX(newDirection);
        }
    }

    @Override
    public void moveY(List<Platform> platforms) {
        if (isOnScreen()) {
            super.moveY(platforms);
        }
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public void setLastDirection(Direction lastDirection) {
        this.lastDirection = lastDirection;
    }

    public Direction getLastDirection() {
        return lastDirection;
    }

    public void moveX() {
        moveX(lastDirection);
    }

    public boolean isOnScreen() {
        return -300 <= getY() && getY() <= Main.SCREEN_HEIGHT + 300;
    }

    @Override
    public JSONObject collectData() {
//        JSONObject data = super.collectData();
//        data.put("score", score);
//        return data;
        JSONObject result = lastDataGot;
        lastDataGot = null;
        return result;
    }

    public void updateData(double score, JSONObject data) {
//        System.out.println("x: " + getX() + ", y: " + getY());
        this.lastDataGot = data;

        if (getLoose()) { // we don't need to update data if this doodle is looser
            return;
        }
        if (data.containsKey(ServerParameter.LOOSE.toString()) && data.getBoolean(ServerParameter.LOOSE.toString())) {
            setLoose(true);
        } else {
            setX(data.getDouble(ServerParameter.COORDINATE_X.toString()));
            setY(data.getDouble(ServerParameter.COORDINATE_Y.toString()));
            setMoveDirection(Direction.getByValue(data.getIntValue(ServerParameter.MOVE_DIRECTION.toString())));
            setLastDirection(getMoveDirection());
            setMoveSpeed(data.getDouble(ServerParameter.MOVE_SPEED.toString()));
            setDoodleSide(Direction.getByValue(data.getIntValue(ServerParameter.DOODLE_SIDE.toString())));
            setDiffY(data.getDouble(ServerParameter.DIFF_Y.toString()));
            this.score = data.getDouble(ServerParameter.SCORE.toString());

            // set realY by scores' difference
            double YOffset = this.score - score;
            double realY = this.getY() - YOffset;
            this.setY(realY);
        }
    }

    public void moveOutFromScreen() {
        setX(-100 - getWidth());
    }
}
