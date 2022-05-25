package com.example.doodlejumpwithmp.model.serverwork;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

public class Server extends Thread {
    private String ip = "127.0.0.1";
    private int port;

    private final ServerSocket server;
    private final LinkedList<ServerCell> serverList = new LinkedList<>();
    private final LinkedList<JSONObject> receivedRequests = new LinkedList<>();
    private final ArrayList<Integer> connections = new ArrayList<>();

    private boolean stopped = false;
    private int lastIdSet = 0; // server id = 0

    public LinkedList<JSONObject> getReceivedRequests() {
        return receivedRequests;
    }

    public void addReceivedRequest(JSONObject jsonObject) {
        receivedRequests.add(jsonObject);
    }

    public ArrayList<Integer> getConnections() {
        return connections;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Server(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        this.server = new ServerSocket(port, 50, InetAddress.getByName(ip));
        connections.add(0); // server id;
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        try {
            System.out.println("Server Started");
            while (!stopped) {  // endless checking for new connections
                Socket socket = server.accept();  // Block program until new connection
                try {
                    serverList.add(new ServerCell(this, socket)); // add new connection
                } catch (IOException e) {
                    socket.close();  // Close socket if it failed
                }
            }
        } catch (IOException exception) {
            this.close();
        } finally {
            this.close();
        }
    }

    public void stopAccept() {
        stopped = true;
    }

    public void close() {
        stopped = true;
        for (ServerCell cell: serverList) {
            cell.close();
        }
    }

    public LinkedList<ServerCell> getServerList() {
        return serverList;
    }

    public int getNewClientId() {
        return ++lastIdSet;
    }

    // requests work
    public void sendStartGame(long seed) {
        for (ServerCell serverCell: serverList) {
            serverCell.sendStartGame(seed);
        }
    }

    public void sendNewUser(int newUserId) {
        for (ServerCell serverCell: serverList) {
            if (serverCell.getClientId() != newUserId) {
                serverCell.sendNewUser(newUserId);
            }
        }
    }

    public void sendNewInfo(Map<Integer, JSONObject> map) {
        for (ServerCell serverCell: serverList) {
            serverCell.sendNewInfo(map);
        }
    }
}

class ServerCell extends Thread {
    private final Server server;
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;
    private int clientId; // technical final
    private boolean stopped = false;

    public ServerCell(Server server, Socket socket) throws IOException {
        this.server = server;
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.setDaemon(true);
        this.start();
    }

    public int getClientId() {
        return clientId;
    }

    public void close() {
        try {
            if (!socket.isClosed()) {
                stopped = true;
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private String read() throws IOException {
        return in.readLine();
    }

    @Override
    public void run() {
        try {
            while (!stopped) {
                String responseString = read();
                JSONObject responseJsonObject = JSON.parseObject(responseString);
                handleResponse(responseJsonObject);
            }
        } catch (IOException ignored) {
            this.close();
        }
    }

    // requests work
    public void sendStartGame(long seed) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", ServerKey.START_GAME.getCode());
        jsonObject.put("seed", seed);
        send(jsonObject.toString());
    }

    public void sendNewId() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", ServerKey.SET_ID.getCode());
        jsonObject.put("newId", clientId);
        JSONArray jsonArrayConnections = new JSONArray();
        jsonArrayConnections.fluentAddAll(server.getConnections());
        jsonObject.put("connections", jsonArrayConnections);
        send(jsonObject.toString());
    }

    public void sendNewUser(int newUserId) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", ServerKey.NEW_USER_ADDED.getCode());
        jsonObject.put("clientId", newUserId);
        send(jsonObject.toString());
    }

    public void sendNewInfo(Map<Integer, JSONObject> map) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", ServerKey.NEW_INFO.getCode());
        JSONArray jsonArray = new JSONArray();
        for (Integer key: map.keySet()) {
            if (key != clientId) {
                JSONObject elem = new JSONObject();
                elem.put("clientId", key);
                elem.put("data", map.get(key));
                jsonArray.fluentAdd(elem);
            }
        }
        jsonObject.put("users", jsonArray);
        send(jsonObject.toString());
    }

    public void handleResponse(JSONObject response) {
        int code = response.getIntValue("code");
        ServerKey serverKey = ServerKey.getKeyByCode(code);
        switch (serverKey) {
            case CONNECTED -> handleConnected(response);
            case NEW_INFO -> handleNewInfo(response);
        }
    }

    private void handleConnected(JSONObject response) {
        clientId = server.getNewClientId();
        server.getConnections().add(clientId);
        response.put("clientId", clientId);
        sendNewId();
        server.sendNewUser(clientId);
        server.addReceivedRequest(response);
    }

    private void handleNewInfo(JSONObject response) {
        response.put("clientId", clientId);
        server.addReceivedRequest(response);
    }
}
