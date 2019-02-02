package com.vladhuk.roshambo.server;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Commander commander;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.commander = new Commander(socket);
    }

    @Override
    public void run() {
        try {
            ServerCommand command = commander.receiveCommand();

            switch (command) {
                case LOGIN:
                    login();
                    break;
                case CREATE_ACCOUNT:
                    createAccount();
                    break;
            }
        } catch (DisconnectException e) {
            System.out.println(socket.getRemoteSocketAddress().toString() + " was disconnected.");
            disableSocket();
        }
    }

    private void login() throws DisconnectException {
        int id = commander.receiveAccountID();

        boolean answer = isAccountExist(id);
        commander.sendAnswer(answer);

        if (answer) {
            commander.sendAccount(Server.getAccounts().get(id));
        }
    }

    private void createAccount() throws DisconnectException {
        Account account = commander.receiveAccount();
        int id = account.hashCode();

        boolean answer = !isAccountExist(id);
        commander.sendAnswer(answer);

        if (answer) {
            Server.getAccounts().put(id, account);
        }
    }

    private boolean isAccountExist(int id) {
        return Server.getAccounts().keySet().contains(id);
    }

    private void disableSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
