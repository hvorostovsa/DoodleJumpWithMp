package com.example.doodlejumpwithmp;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static Socket socket;
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток чтения в сокет
    private static String addr; // ip адрес клиента
    private static final int port = Server.getPORT();

    public static void main(String[] args) {
        try {
            socket = new Socket(addr, port);
        } catch (IOException e) {
            System.err.println("Socket failed");
        }
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            Scanner console = new Scanner(System.in);
            String messageOut;
            String messageIn;
            while (true) {
                System.out.println("Enter the message: ");
                messageOut = console.nextLine();
                Client.send(messageOut);
                System.out.println("Message was sent: " + messageOut);
                messageIn = Client.read();
                System.out.println("Message returned: " + messageIn);
            }
        } catch (IOException e) {
            Client.close();
        }
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

    public static void close() {
        try {
            if (!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
            }
        } catch (IOException ignored) {}
    }
}
