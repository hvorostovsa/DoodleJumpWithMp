package com.example.doodlejumpwithmp.model.serverwork;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Locale;

public class Server {
    private static final int PORT = 41456;
    private static LinkedList<ServerCell> serverList = new LinkedList<>();

    public static void main(String[] args) throws IOException {
        try (ServerSocket server = new ServerSocket(PORT)) {
            System.out.println("Server Started");
            while (true) {  // endless checking for new connections
                Socket socket = server.accept();  // Block program until new connection
                try {
                    serverList.add(new ServerCell(socket)); // add new connection
                } catch (IOException e) {
                    socket.close();  // Close socket if it failed
                }
            }
        }
    }

    public static LinkedList<ServerCell> getServerList() {
        return serverList;
    }

    public static int getPORT() {
        return PORT;
    }
}

class ServerCell extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final BufferedWriter out;

    public ServerCell(Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.start();
    }

    private void close() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                LinkedList<ServerCell> serverList = Server.getServerList();
                for (ServerCell cell : serverList) {
                    if (cell.equals(this)) {
                        cell.interrupt();
                        serverList.remove(this);
                        break;
                    }
                }
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
            while (true) {
                String messageIn = this.read();
                System.out.println("Get message: " + messageIn);
                if (messageIn.toLowerCase(Locale.ROOT).equals("stop")) {
                    this.close();
                    break;
                } else {
                    this.send(messageIn + "!!!");
                }
            }
        } catch (IOException ignored) {
            this.close();
        }
    }
}
