package com.vladhuk.roshambo.server;

import java.io.*;
import java.net.Socket;

public class Commander {

    private Socket socket;

    public Commander(Socket socket) {
        this.socket = socket;
    }

    private void sendObject(Serializable object) throws DisconnectException {
        try {
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(object);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    private Object receiveObject() throws DisconnectException {
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

    public void sendCommand(ServerCommand command) throws DisconnectException {
        sendObject(command);
    }

    public ServerCommand receiveCommand() throws DisconnectException {
        return (ServerCommand) receiveObject();
    }

    public void sendAccount(Account account) throws DisconnectException {
        sendObject(account);
    }

    public Account receiveAccount() throws DisconnectException {
        return (Account) receiveObject();
    }

    public void sendAccountID(int id) throws DisconnectException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeInt(id);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    public int receiveAccountID() throws DisconnectException {
        try {
            DataInputStream in = new DataInputStream(socket.getInputStream());
            int id = in.readInt();
            return id;
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

    public void sendAnswer(boolean answer) throws DisconnectException {
        try {
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            out.writeBoolean(answer);
            out.flush();
        } catch (IOException e) {
            throw new DisconnectException();
        }
    }

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
