package com.example.doodlejumpwithmp.controller;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.Main;
import com.example.doodlejumpwithmp.ScreenMode;
import com.example.doodlejumpwithmp.controller.serverwork.Client;
import com.example.doodlejumpwithmp.controller.serverwork.Server;
import com.example.doodlejumpwithmp.controller.serverwork.ServerKey;
import com.example.doodlejumpwithmp.controller.serverwork.ServerParameter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ClientServerController {

    private String ip;
    private int port;
    private final GameController gameController;
    private final Main main;

    public ClientServerController(Main main, GameController gameController) {
        this.gameController = gameController;
        this.main = main;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void beforeConnectionStart(boolean isServer) {
        gameController.setIsServer(isServer);
        gameController.setIsClient(!isServer);
        if (isServer) {
            beforeStartServerWork();
        } else {
            beforeStartClientWork();
        }
    }

    public void beforeConnectionStart() {
        beforeConnectionStart(false);
    }


    private void beforeStartServerWork() {
        Server server = gameController.getServer();
        if (server != null) {
            JSONObject response = server.popRequest();
            while (response != null) { // while response remain;
                int code = response.getIntValue(ServerParameter.CODE.toString());
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case CONNECTED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        gameController.addShadowDoodle(newClientId);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
                response = server.popRequest();
            }
        }
    }

    private void beforeStartClientWork() {
        Client client = gameController.getClient();
        if (client != null) {
            LinkedList<JSONObject> receivedRequests = client.getReceivedRequests();
            JSONObject response;
            boolean gameStarted = false;
            while (receivedRequests.size() > 0) {
                if (gameStarted) {
                    break;
                }
                response = receivedRequests.pop();
                int code = response.getIntValue(ServerParameter.CODE.toString());
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case SET_ID -> {
                        int clientId = response.getIntValue(ServerParameter.NEW_ID.toString());
                        client.setClientId(clientId);
                        Integer[] connections =
                                response.getJSONArray(ServerParameter.CONNECTIONS.toString()).toArray(Integer[]::new);
                        List<Integer> list = Stream.of(connections).filter(it -> it != clientId).toList();
                        client.getConnections().addAll(list);
                        for (Integer id : list) {
                            gameController.addShadowDoodle(id);
                        }
                    }
                    case NEW_USER_ADDED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        gameController.addShadowDoodle(newClientId);
                    }
                    case START_GAME -> {
                        gameStarted = true;
                        long seed = response.getLong(ServerParameter.SEED.toString());
                        System.out.println("Seed: " + seed);
                        gameController.setSeed(seed);
//                        gameController.initialGamePreparations();
                        main.setScreenMode(ScreenMode.MULTIPLAYER_GAME);
                        main.changeScreenMode();
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    public void createServer() {
        Server server = gameController.getServer();
        if (server != null && !(server.getIp().equals(ip) && port == server.getPort())) { // if new server ip:port
            server.close();
            server = null;
        }
        if (server == null) {
            try {
                gameController.setServer(new Server(ip, port));
            } catch (IOException exception) {
                System.out.println("incorrect address " + ip + ":" + port);
            }
        }
    }

    public void createClient() {
        Client client = gameController.getClient();
        if (client != null && !(client.getIp().equals(ip) && port == client.getPort())) { // if new client ip:port
            client.close();
            client = null;
        }
        if (client == null) {
            try {
                client = new Client(ip, port);
                gameController.setClient(client);
                client.sendConnected();
            } catch (IOException exception) {
                System.out.println("can't connect to server " + ip + ":" + port);
            }
        }
    }
}
