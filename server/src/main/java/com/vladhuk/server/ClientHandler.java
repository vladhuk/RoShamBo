package com.vladhuk.server;

import com.sun.tools.javac.Main;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()))) {

            ServerCommand command = ServerCommand.values()[in.readInt()];

            switch (command) {
                case LOGIN:
                    login();
                    break;
                case CREATE_ACCOUNT:
                    createAccount();
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void login() throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))
        ) {
            Account account = getAccount();

            if (isAccountExist(account)) {
                out.writeBoolean(true);
            } else {
                out.writeBoolean(false);
            }

            out.flush();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void createAccount() throws IOException {
        try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()))
        ) {
            Account account = getAccount();

            if (isAccountExist(account)) {
                out.writeBoolean(false);
            } else {
                out.writeBoolean(true);
                Server.ACCOUNTS.add(account);
            }

            out.flush();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private boolean isAccountExist(Account account) {
        return Server.ACCOUNTS.contains(account);
    }

    private Account getAccount() throws IOException, ClassNotFoundException {
        try (ObjectInputStream objectIn = new ObjectInputStream(socket.getInputStream())) {
            return (Account) objectIn.readObject();
        }
    }

}
