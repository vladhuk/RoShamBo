package com.vladhuk.roshambo.client;

import com.vladhuk.roshambo.server.Commander;
import com.vladhuk.roshambo.server.DisconnectException;

import java.io.*;
import java.net.Socket;

public class Connection {

    private static final File IP_FILE = new File(Client.DOC_PATH + "ip.dat");
    private static final int PORT = 5543;

    private static Socket socket;
    private static Commander commander;
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
        if (commander != null) {
            commander.closeSocket();
        }

        try {
            socket = new Socket(ip, PORT);
        } catch (IOException e) {
            isConnected = false;
            return false;
        }

        commander = new Commander(socket);
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

    public static void sendObject(Object object) throws DisconnectException {
        try {
            commander.sendObject(object);
        } catch (DisconnectException e) {
            isConnected = false;
            throw e;
        }
    }

    public static Object receiveObject() throws DisconnectException {
        Object object;

        try {
            object = commander.receiveObject();
        } catch (DisconnectException e) {
            isConnected = false;
            throw e;
        }

        return object;
    }

    public static void sendInteger(int i) throws DisconnectException {
        try {
            commander.sendInteger(i);
        } catch (DisconnectException e) {
            isConnected = false;
            throw e;
        }
    }

    public static int receiveInteger() throws DisconnectException {
        int i;

        try {
            i = commander.receiveInteger();
        } catch (DisconnectException e) {
            isConnected = false;
            throw e;
        }

        return i;
    }

    public static boolean receiveAnswer() throws DisconnectException {
        boolean answer;

        try {
            answer = commander.receiveAnswer();
        } catch (DisconnectException e) {
            isConnected = false;
            throw e;
        }

        return answer;
    }

}
