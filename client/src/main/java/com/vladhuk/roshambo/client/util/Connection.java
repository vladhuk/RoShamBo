package com.vladhuk.roshambo.client.util;

import com.vladhuk.roshambo.client.Client;
import com.vladhuk.roshambo.server.util.*;

import java.io.*;
import java.net.Socket;


public class Connection implements Connector {

    private static final File IP_FILE = new File(Client.DOC_PATH + "ip.dat");
    private static final int PORT = 5543;

    private ServerConnector connector;
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
            ServerConnector connector = new ServerConnector(socket);
            connection.setConnector(connector);
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

    private void setConnector(ServerConnector connector) {
        this.connector = connector;
    }

    private Connector getConnector() {
        return connector;
    }

    public boolean reconnect() {
        buildConnection(getIP());

        return isConnected();
    }

    private void disconnect() {
        if (connector != null) {
            connector.closeSocket();
        }

        setConnected(false);
    }

    @Override
    public void sendObject(Object object) throws DisconnectException {
        try {
            connector.sendObject(object);
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public Object receiveObject() throws DisconnectException {
        Object object;

        try {
            object = connector.receiveObject();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return object;
    }

    @Override
    public void sendInteger(int i) throws DisconnectException {
        try {
            connector.sendInteger(i);
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public int receiveInteger() throws DisconnectException {
        int i;

        try {
            i = connector.receiveInteger();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return i;
    }

    @Override
    public void sendAnswer(boolean answer) throws DisconnectException {
        try {
            connector.sendAnswer(answer);
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }
    }

    @Override
    public boolean receiveAnswer() throws DisconnectException {
        boolean answer;

        try {
            answer = connector.receiveAnswer();
        } catch (DisconnectException e) {
            disconnect();
            throw e;
        }

        return answer;
    }

}
