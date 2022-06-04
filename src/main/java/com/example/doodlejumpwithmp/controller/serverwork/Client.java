package com.example.doodlejumpwithmp.controller.serverwork;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;

public class Client extends Thread {
    private static Socket socket;
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток чтения в сокет

    private String ip;
    private int port;
    private int clientId;

    private final LinkedList<JSONObject> receivedRequests = new LinkedList<>();
    private ArrayList<Integer> connections = new ArrayList<>();

    private boolean stopped = false;

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getClientId() {
        return clientId;
    }

    public LinkedList<JSONObject> getReceivedRequests() {
        return receivedRequests;
    }

    public ArrayList<Integer> getConnections() {
        return connections;
    }

    public Client(String ip, int port) throws IOException {
        this.ip = ip;
        this.port = port;
        socket = new Socket(ip, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!stopped) {
                String response = read();
//                System.out.println(response);
                synchronized (this) {
                    receivedRequests.add(JSON.parseObject(response));
                }
            }
        } catch (IOException exception) {
            this.close();
        } finally {
            this.close();
        }
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

    private static void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}
    }

    private static String read() throws IOException {
        return in.readLine();
    }

    public void close() {
        stopped = true;
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }

    // different requests
    public void sendConnected() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(ServerParameter.CODE.toString(), ServerKey.CONNECTED.getCode());
        send(jsonObject.toString());
    }

    public void sendNewInfo(JSONObject newInfo) {
        newInfo.put(ServerParameter.CODE.toString(), ServerKey.NEW_INFO.getCode());
        send(newInfo.toString());
    }
}
