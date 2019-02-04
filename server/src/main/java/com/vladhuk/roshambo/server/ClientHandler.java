package com.vladhuk.roshambo.server;

import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Commander commander;
    private Account account;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.commander = new Commander(socket);
    }

    @Override
    public void run() {
        try {
            while (true) {
                ServerCommand command = (ServerCommand) commander.receiveObject();

                switch (command) {
                    case LOGIN:
                        login();
                        break;
                    case CREATE_ACCOUNT:
                        createAccount();
                        break;
                    case USERS_NUMBER:
                        sendUsersNumber();
                        break;
                    case ROOMS_LIST:
                        sendAvailableRoomsList();
                        break;
                    case ADD_ROOM:
                        addRoom();
                        break;
                }
            }
        } catch (DisconnectException e) {
            System.out.println(socket.getRemoteSocketAddress().toString() + " was disconnected.");
            if (account != null) {
                Server.getOnlineAccounts().remove(account.hashCode());
            }
            commander.closeSocket();
        }
    }


    private void login() throws DisconnectException {
        int id = commander.receiveInteger();

        boolean answer = isAccountExist(id);
        commander.sendAnswer(answer);
        if (answer) {
            account = Server.getAccounts().get(id);
            commander.sendObject(account);
            Server.getOnlineAccounts().add(id);
        }
    }

    private void createAccount() throws DisconnectException {
        Account account = (Account) commander.receiveObject();
        int id = account.hashCode();

        boolean answer = !isAccountExist(id) && !isAccountOnline(id);
        commander.sendAnswer(answer);

        if (answer) {
            Server.getAccounts().put(id, account);
            Server.getOnlineAccounts().add(id);
        }
    }

    private boolean isAccountExist(int id) {
        return Server.getAccounts().keySet().contains(id);
    }

    private boolean isAccountOnline(int id) {
        return Server.getOnlineAccounts().contains(id);
    }

    private void sendUsersNumber() throws DisconnectException {
        commander.sendInteger(Server.getOnlineAccounts().size());
    }

    private void sendAvailableRoomsList() throws DisconnectException {
        commander.sendObject(Server.getAvailableRooms());
    }

    private void addRoom() throws DisconnectException {
        Room room = (Room) commander.receiveObject();
        Server.getRooms().add(room);
        Server.getAvailableRooms().add(room);
    }

}
