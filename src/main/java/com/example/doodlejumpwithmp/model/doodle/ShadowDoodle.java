package com.example.doodlejumpwithmp.model.doodle;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.model.serverwork.ServerKey;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.LinkedList;

public class ShadowDoodle extends Doodle {
    // Other players will be shown as ShadowDoodle

    private int lastDirection = 0;
    private boolean loose = false;
    private int clientId;
    private double score = 0.;

    private JSONObject lastDataGot;

    public ShadowDoodle(Image characterImage) {
        super(characterImage);
    }

    @Override
    public void moveX(int newDirection) {
        if (isOnScreen()) {
            super.moveX(newDirection);
        }
    }

    @Override
    public void moveY(ArrayList<Platform> platforms) {
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
        return -300 <= getY() && getY() <= Main.getScreenHeight() + 300;
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

        setX(data.getDouble("coordinateX"));
        setY(data.getDouble("coordinateY"));
        setMoveDirection(data.getIntValue("moveDirection"));
        setLastDirection(getMoveDirection());
        setMoveSpeed(data.getDouble("moveSpeed"));
        setDoodleSide(data.getIntValue("doodleSide"));
        setDifY(data.getDouble("difY"));
        this.score = data.getDouble("score");

        // set realY by scores' difference
        double YOffset = this.score - score;
        double realY = this.getY() - YOffset;
        this.setY(realY);
    }
}
