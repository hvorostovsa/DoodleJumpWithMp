package com.example.doodlejumpwithmp;


import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.model.doodle.Doodle;
import com.example.doodlejumpwithmp.model.doodle.ShadowDoodle;
import com.example.doodlejumpwithmp.model.platform.MovingPlatform;
import com.example.doodlejumpwithmp.model.platform.OneJumpPlatform;
import com.example.doodlejumpwithmp.model.platform.Platform;
import com.example.doodlejumpwithmp.model.platform.ZeroJumpPlatform;
import com.example.doodlejumpwithmp.model.serverwork.Client;
import com.example.doodlejumpwithmp.model.serverwork.Server;
import com.example.doodlejumpwithmp.model.serverwork.ServerKey;

import java.util.*;

public class Controller {
    private final Main main;
    private final Doodle doodle;
    private final Map<Integer, ShadowDoodle> shadowDoodles = new HashMap<>();

    private final ArrayList<Platform> platforms = new ArrayList<>();
    private final ArrayList<String> input = new ArrayList<>();

    private int interval = 100;
    private boolean gameOver = false;
    private boolean isServer = false; // null if single player. True if user is host.
    private boolean isClient = false;

    private Server server;
    private Client client;

    private Random random = new Random();

    public void setSeed(long seed) {
        random = new Random(seed);
        System.out.println(random.nextLong());
    }

    public Map<Integer, ShadowDoodle> getShadowDoodles() {
        return shadowDoodles;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Server getServer() {
        return server;
    }

    public void setServer(Server server) {
        this.server = server;
    }

    private double score = 0;

    public ArrayList<Platform> getPlatforms() {
        return platforms;
    }

    public Doodle getDoodle() {
        return doodle;
    }

    public boolean getIsServer() {
        return isServer;
    }

    public void setIsServer(boolean isServer) {
        this.isServer = isServer;
    }

    public boolean getIsClient() {
        return isClient;
    }

    public void setIsClient(boolean isClient) {
        this.isClient = isClient;
    }

    public boolean ifDoodleFall() {
        return gameOver;
    }

    public ArrayList<String> getInput() {
        return input;
    }

    public Controller(Main main) {
        this.main = main;
        this.doodle = new Doodle(Main.doodleImage);
    }

    public void initialGamePreparations() {
        Platform oldPlatform = new Platform(Main.platformImage);
        oldPlatform.setPosition(185, Main.getScreenHeight() - 15);
        platforms.add(oldPlatform);
        doodle.setPosition(185, 560);
        double max = 0;
        while (max < Main.getScreenHeight()) {
            Platform newPlatform = placePlatform(oldPlatform);
            platforms.add(newPlatform);
            max += (oldPlatform.getY() - newPlatform.getY());
            oldPlatform = platforms.get(platforms.size() - 1);
        }
    }

    public void addShadowDoodle(int clientId) {
        ShadowDoodle shadowDoodle = new ShadowDoodle(Main.shadowDoodleImage);
        shadowDoodle.setClientId(clientId);
        shadowDoodles.put(clientId, shadowDoodle);
    }

    public boolean containsPlatform(Platform platform, double bottom, double top) {
        return platform.getY() > top && platform.getY() < bottom;
    }

    private int getRandomEventNumber(Double... numbers) {
        // pass numbers (1, 1.5, 2.5). Get 0 by chance 20%, 1 by chance 30%, 2 by chance 50%
        ArrayList<Double> newNumbers = new ArrayList<>(List.of(numbers));
        double sum = newNumbers.get(0);
        double currentNumber;
        for (int i = 1; i < numbers.length; i++) {
            currentNumber = newNumbers.get(i);
            sum += currentNumber;
            newNumbers.set(i, currentNumber + newNumbers.get(i - 1));
        }
        double randomNumber = random.nextDouble() * sum;
        System.out.println("randomNumber: " + randomNumber);
        int resultIndex = 0;
        while (randomNumber > newNumbers.get(resultIndex)) {
            resultIndex += 1;
        }
        return resultIndex;
    }

    private Platform createPlatform(
            Platform previous,
            double x,
            double y,
            double i,
            double j,
            int speed
    ) {
        Platform platform;
        int platformNumber = getRandomEventNumber(x, y, i, j);
        // 0 => Classic platform by x% chance
        // 1 => Moving platform by y% chance
        // 2 => One Jump Platform by i% chance
        // 3 => Zero Jump Platform by j% chance
        platform = switch (platformNumber) {
            case 0 -> new Platform(Main.platformImage); // Classic platform by x% chance
            case 1 -> new MovingPlatform(Main.movingPlatformImage).returnWithNewSpeed(speed); // Moving platform by y% chance
            case 2 -> new OneJumpPlatform(Main.oneJumpPlatformImage); // One Jump Platform by i% chance
            case 3 -> new ZeroJumpPlatform(Main.zeroJumpPlatformImage); // Zero Jump Platform by j% chance
            default -> throw new IllegalStateException("Unexpected value: " + platformNumber);
        };
        int nextInt1 = random.nextInt(380);
        int nextInt2 = random.nextInt(interval);
        System.out.println("nextInt1: " + nextInt1 + ", nextInt2: " + nextInt2);
        platform.setPosition(nextInt1, previous.getY() - 10 - nextInt2);
        if (!platform.intersectPlatform(previous)) {
            return platform;
        }
        return createPlatform(previous, x, y, i, j, speed);
    }

    public Platform placePlatform(Platform platform) {
        if (score < 3000) {
            interval = 70;
            return createPlatform(platform, 80, 15, 5, 0, 2);
        } else if (score < 7500) {
            interval = 100;
            return createPlatform(platform, 65, 15, 20, 0, 2);
        } else if (score < 15000) {
            interval = 120;
            return createPlatform(platform, 60, 25, 15, 0, 3);
        } else if (score < 25000) {
            interval = 150;
            return createPlatform(platform, 55, 25, 20, 0, 3);
        } else {
            interval = 190;
            return createPlatform(platform, 40, 35, 25, 0, 4);
        }
    }

    public String getScoreString() {
        int intScore = (int) Math.floor(score);
        return "Your score:" + intScore;
    }

    public void dragScreen() {
        if (doodle.getY() < 300) {
            doodle.setY(300);
            for (Platform platform : platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            score += doodle.getDifY() * -1;
            Platform oldPlatform = platforms.get(platforms.size() - 1);
            if (!containsPlatform(oldPlatform, 0, interval * (-2)))
                platforms.add(placePlatform(oldPlatform));
            if (platforms.get(0).getY() > Main.getScreenHeight()) platforms.remove(0);
        }  else if (doodle.getY() > Main.getScreenHeight()) {
            for (Platform platform: platforms) {
                platform.setPosition(platform.getX(), platform.getY() - doodle.getDifY());
            }
            if (platforms.get(0).getY() < -100) makeGameOver();
        }
    }

    private void makeGameOver() {
        gameOver = true;
        doodle.setLoose(true);
        for (ShadowDoodle shadowDoodle: shadowDoodles.values()) {
            shadowDoodle.moveOutFromScreen();
        }
    }

    private void updateCoordinateX() {
        if (input.contains("RIGHT")) {
            if (input.contains("LEFT")) {
                doodle.moveX(0); // no move
            } else {
                doodle.moveX(1); // move right
            }
        } else if (input.contains("LEFT")) {
            doodle.moveX(-1); // move left
        } else {
            doodle.moveX(0); // no move
        }
    }

    private void updateCoordinateY() {
        doodle.moveY(getPlatforms());
    }

    private void updateDoodle() {
        updateCoordinateY();
        updateCoordinateX();
    }

    private void getNewInfo() {
        if (isServer) { // server
            LinkedList<JSONObject> receivedRequests = server.getReceivedRequests();
            while (receivedRequests.size() > 0) {
                JSONObject response = receivedRequests.pop();
                int code = response.getIntValue("code");
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case NEW_INFO -> {
                        int clientId = response.getIntValue("clientId");
                        shadowDoodles.get(clientId).updateData(score, response);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        } else if (isClient) { // client
            LinkedList<JSONObject> receivedRequests = client.getReceivedRequests();
            while (receivedRequests.size() > 0) {
                JSONObject response = receivedRequests.pop();
                int code = response.getIntValue("code");
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case NEW_INFO -> {
                        JSONArray users = response.getJSONArray("users");
                        for (int i = 0; i < users.size(); i++) {
                            JSONObject user = users.getJSONObject(i);
                            int clientId = user.getIntValue("clientId");
                            JSONObject data = user.getJSONObject("data");
                            shadowDoodles.get(clientId).updateData(score, data);
                        }
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    public void updateShadowDoodles() {
        if (!gameOver) {
            for (ShadowDoodle shadowDoodle: shadowDoodles.values()) {
                if (shadowDoodle.isOnScreen() && !shadowDoodle.getLoose()) {
//                System.out.println("Yes, on screen");
                    shadowDoodle.moveY(platforms);
                    shadowDoodle.moveX();
                }
            }
        }
    }

    private void updatePlatforms() {
        for (Platform platform: platforms) {
            platform.update();
        }
    }

    private void sendInfo() {
        if (isServer) { // if server
            Map<Integer, JSONObject> map = new HashMap<>();
            for (ShadowDoodle shadowDoodle: shadowDoodles.values()) {
                JSONObject data = shadowDoodle.collectData();
                if (data == null) {
                    continue;
                }
                map.put(shadowDoodle.getClientId(), data);
            }
            JSONObject data = doodle.collectData();
            data.put("score", score);
            map.put(0, data);
            server.sendNewInfo(map);
        } else if (isClient) { // if client
            JSONObject newInfo = doodle.collectData();
            newInfo.put("score", score);
            client.sendNewInfo(newInfo);
        }
    }

    public void update() {
        dragScreen();
        updateDoodle();
        updatePlatforms();
        sendInfo();
        updateShadowDoodles();
        main.repaintScene();
        getNewInfo();
    }
}