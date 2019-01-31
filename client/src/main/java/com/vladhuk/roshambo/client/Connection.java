package com.vladhuk.roshambo.client;

import java.io.*;
import java.net.Socket;

public class Connection {

    private static final File IP_FILE = new File(Client.DOC_PATH + "ip.dat");
    private static final int PORT = 5543;

    private static Socket socket;
    private static String ip = "";
    private static boolean isConnected = false;

    public static String getIp() {
        return ip;
    }

    public static void saveIpToFile(String ip) {
        try (PrintWriter writer = new PrintWriter(IP_FILE)) {
            writer.println(ip);
        } catch (FileNotFoundException e) {
            // File creates automatically
        }
    }

    public static String loadIpFromFile() throws IOException {
        String ip = "";

        try (BufferedReader reader = new BufferedReader(new FileReader(IP_FILE))) {
            if (!reader.ready()) {
                return ip;
            }
            ip = reader.readLine();
        } catch (FileNotFoundException e) {
            // File creates automatically
        }

        return ip;
    }

    public static boolean connect(String ip) {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            socket = new Socket(ip, PORT);
        } catch (IOException e) {
            isConnected = false;
            return false;
        }

        Connection.ip = ip;
        isConnected = true;
        return true;
    }

    public static boolean reconnect() {
        if (isConnected) {
            return true;
        }

        if (!connect(ip)) {
            isConnected = false;
            return false;
        }

        isConnected = true;
        return true;
    }

    public static boolean isConnected() {
        return isConnected;
    }

    public static void sendMessage(String message) {
        // TODO
    }

    public static String receiveMessage() {
        // TODO
        return "";
    }

    public static void sendAccount(Account account) {
        // TODO
    }

    public static Account receiveAcoount() {
        // TODO
        return null;
    }

}
