package com.example.doodlejumpwithmp.controller.serverwork;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread {
    private String ip;
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

    public List<Integer> getConnections() {
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
                    serverList.add(new ServerCell(socket)); // add new connection
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

    private class ServerCell extends Thread {
        private final Socket socket;
        private final BufferedReader in;
        private final BufferedWriter out;
        private int clientId; // technical final
        private boolean stopped = false;

        public ServerCell(Socket socket) throws IOException {
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
            this.interrupt();
            try {
                while (!stopped) {
                    try {
                        String responseString = read();
                        JSONObject responseJsonObject = JSON.parseObject(responseString);
                        handleResponse(responseJsonObject);
                    } catch (InterruptedIOException ignored) {
                        // pass
                    }
                }
            } catch (IOException ignored) {
                this.close();
            }
        }

        // requests work
        public void sendStartGame(long seed) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerParameter.CODE.toString(), ServerKey.START_GAME.getCode());
            jsonObject.put(ServerParameter.SEED.toString(), seed);
            send(jsonObject.toString());
        }

        public void sendNewId() {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerParameter.CODE.toString(), ServerKey.SET_ID.getCode());
            jsonObject.put(ServerParameter.NEW_ID.toString(), clientId);
            JSONArray jsonArrayConnections = new JSONArray();
            jsonArrayConnections.fluentAddAll(Server.this.getConnections());
            jsonObject.put(ServerParameter.CONNECTIONS.toString(), jsonArrayConnections);
            send(jsonObject.toString());
        }

        public void sendNewUser(int newUserId) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerParameter.CODE.toString(), ServerKey.NEW_USER_ADDED.getCode());
            jsonObject.put(ServerParameter.CLIENT_ID.toString(), newUserId);
            send(jsonObject.toString());
        }

        public void sendNewInfo(Map<Integer, JSONObject> map) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(ServerParameter.CODE.toString(), ServerKey.NEW_INFO.getCode());
            JSONArray jsonArray = new JSONArray();
            for (Integer key: map.keySet()) {
                if (key != clientId) {
                    JSONObject elem = new JSONObject();
                    elem.put(ServerParameter.CLIENT_ID.toString(), key);
                    elem.put(ServerParameter.DATA.toString(), map.get(key));
                    jsonArray.fluentAdd(elem);
                }
            }
            jsonObject.put(ServerParameter.USERS.toString(), jsonArray);
            send(jsonObject.toString());
        }

        public void handleResponse(JSONObject response) {
            int code = response.getIntValue(ServerParameter.CODE.toString());
            ServerKey serverKey = ServerKey.getKeyByCode(code);
            switch (serverKey) {
                case CONNECTED -> handleConnected(response);
                case NEW_INFO -> handleNewInfo(response);
            }
        }

        private void handleConnected(JSONObject response) {
            clientId = Server.this.getNewClientId();
            Server.this.getConnections().add(clientId);
            response.put(ServerParameter.CLIENT_ID.toString(), clientId);
            sendNewId();
            Server.this.sendNewUser(clientId);
            Server.this.addReceivedRequest(response);
        }

        private void handleNewInfo(JSONObject response) {
            response.put(ServerParameter.CLIENT_ID.toString(), clientId);
            Server.this.addReceivedRequest(response);
        }
    }
}
