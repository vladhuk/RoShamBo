package com.vladhuk.roshambo.client.util;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.server.Commander;
import com.vladhuk.roshambo.server.DisconnectException;

import java.io.*;
import java.net.Socket;


public class Connection {

    private static final File IP_FILE = new File(Client.DOC_PATH + "ip.dat");
    private static final int PORT = 5543;

    private Commander commander;
    private String ip;
    private boolean isConnected;

    private Connection() {}

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

    private static class ConnectionFactory {
        private static Connection connection = new Connection();
    }

    public static Connection getConnection() {
        return ConnectionFactory.connection;
    }

    public static Connection buildConnection(String ip) {
        Connection connection = getConnection();
        connection.disconnect();
        connection.setIP(ip);

        try {
            Socket socket = new Socket(ip, PORT);
            Commander commander = new Commander(socket);
            connection.setCommander(commander);
            connection.setConnected(true);
        } catch (IOException e) {
            connection.setConnected(false);
        }

        return connection;
    }

    public String getIP() {
        return ip;
    }

    private void setIP(String ip) {
        this.ip = ip;
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    private void setCommander(Commander commander) {
        this.commander = commander;
    }

    private Commander getCommander() {
        return commander;
    }

    public boolean reconnect() {
        buildConnection(getIP());

        return isConnected();
    }

    private void disconnect() {
        if (commander != null) {
            commander.closeSocket();
        }

        setConnected(false);
    }

    public void sendObject(Object object) throws DisconnectException {
        try {
            commander.sendObject(object);
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }
    }

    public Object receiveObject() throws DisconnectException {
        Object object;

        try {
            object = commander.receiveObject();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return object;
    }

    public void sendInteger(int i) throws DisconnectException {
        try {
            commander.sendInteger(i);
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }
    }

    public int receiveInteger() throws DisconnectException {
        int i;

        try {
            i = commander.receiveInteger();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return i;
    }

    public boolean receiveAnswer() throws DisconnectException {
        boolean answer;

        try {
            answer = commander.receiveAnswer();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return answer;
    }

}
