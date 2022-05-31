package com.example.doodlejumpwithmp;

import com.alibaba.fastjson2.JSONObject;
import com.example.doodlejumpwithmp.model.serverwork.Client;
import com.example.doodlejumpwithmp.model.serverwork.Server;
import com.example.doodlejumpwithmp.model.serverwork.ServerKey;
import com.example.doodlejumpwithmp.model.serverwork.ServerParameter;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

public class ClientServerController {

    private String ip;
    private int port;
    private Controller controller;
    private Main main;

    public ClientServerController(Main main, Controller controller) {
        this.controller = controller;
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
        controller.setIsServer(isServer);
        controller.setIsClient(!isServer);
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
        Server server = controller.getServer();
        if (server != null) {
            LinkedList<JSONObject> receivedRequests = server.getReceivedRequests();
            JSONObject response;
            while (receivedRequests.size() > 0) {
                response = receivedRequests.pop();
                int code = response.getIntValue(ServerParameter.CODE.toString());
                ServerKey serverKey = ServerKey.getKeyByCode(code);
                switch (serverKey) {
                    case CONNECTED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        controller.addShadowDoodle(newClientId);
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    private void beforeStartClientWork() {
        Client client = controller.getClient();
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
                            controller.addShadowDoodle(id);
                        }
                    }
                    case NEW_USER_ADDED -> {
                        int newClientId = response.getIntValue(ServerParameter.CLIENT_ID.toString());
                        controller.addShadowDoodle(newClientId);
                    }
                    case START_GAME -> {
                        gameStarted = true;
                        long seed = response.getLong(ServerParameter.SEED.toString());
                        System.out.println("Seed: " + seed);
                        controller.setSeed(seed);
                        controller.initialGamePreparations();
                        main.setScreenMode(ScreenMode.MULTIPLAYER_GAME);
                        main.changeScreenMode();
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + serverKey);
                }
            }
        }
    }

    public void createServer() {
        Server server = controller.getServer();
        if (server != null && !(server.getIp().equals(ip) && port == server.getPort())) { // if new server ip:port
            server.close();
            server = null;
        }
        if (server == null) {
            try {
                controller.setServer(new Server(ip, port));
            } catch (IOException exception) {
                System.out.println("incorrect address " + ip + ":" + port);
            }
        }
    }

    public void createClient() {
        Client client = controller.getClient();
        if (client != null && !(client.getIp().equals(ip) && port == client.getPort())) { // if new client ip:port
            client.close();
            client = null;
        }
        if (client == null) {
            try {
                client = new Client(ip, port);
                controller.setClient(client);
                client.sendConnected();
            } catch (IOException exception) {
                System.out.println("can't connect to server " + ip + ":" + port);
            }
        }
    }
}
