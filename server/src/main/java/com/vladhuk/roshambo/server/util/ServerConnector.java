package com.vladhuk.roshambo.server.util;

import java.io.*;
import java.net.Socket;

public class ServerConnector implements Connector {

    private Socket socket;

    public ServerConnector(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void sendObject(Object object) throws DisconnectException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    @Override
    public Object receiveObject() throws DisconnectException {
        Object object = null;

        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            object = in.readObject();
        } catch (IOException e) {
            throw new DisconnectException();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return object;
    }

    @Override
    public void sendInteger(int i) throws DisconnectException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(i);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    @Override
    public int receiveInteger() throws DisconnectException {
        try {
            DataInputStream i = new DataInputStream(socket.getInputStream());
            int id = i.readInt();
            return id;
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    @Override
    public void sendAnswer(boolean answer) throws DisconnectException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBoolean(answer);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    @Override
    public boolean receiveAnswer() throws DisconnectException {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            boolean answer = in.readBoolean();
            return answer;
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
