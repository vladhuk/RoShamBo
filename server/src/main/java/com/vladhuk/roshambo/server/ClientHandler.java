package com.vladhuk.roshambo.server;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClientHandler implements Runnable {

    private Socket socket;
    private Commander commander;
    private Account account;
    private Room room;

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
                    case ENTER_ROOM:
                        enterRoom();
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

        boolean answer = isAccountExist(id) & !isAccountOnline(id);
        if (answer) {
            account = Server.getAccounts().get(id);
            commander.sendObject(account);
            Server.getOnlineAccounts().add(id);
        }
    }

    private boolean isAccountExist(int id) {
        return Server.getAccounts().keySet().contains(id);
    }

    private boolean isAccountOnline(int id) {
        return Server.getOnlineAccounts().contains(id);
    }

    private void createAccount() throws DisconnectException {
        Account account = (Account) commander.receiveObject();
        int id = account.hashCode();

        boolean answer = !isAccountExist(id) & !isNicknameExist(account.getNickname());
        System.out.println(answer);
        commander.sendAnswer(answer);

        if (answer) {
            Server.getAccounts().put(id, account);
            Server.getOnlineAccounts().add(id);
            this.account = account;
        }
    }

    private boolean isNicknameExist(String nickname) {
        for (Account account : Server.getAccounts().values()) {
            if (account.getNickname().equals(nickname)) {
                return true;
            }
        }

        return false;
    }

    private void sendUsersNumber() throws DisconnectException {
        commander.sendInteger(Server.getOnlineAccounts().size());
    }

    private void sendAvailableRoomsList() throws DisconnectException {
        List list = new LinkedList(Server.getAvailableRooms());
        commander.sendObject(list);
    }

    private void addRoom() throws DisconnectException {
        room = (Room) commander.receiveObject();
        int id = room.hashCode();

        Server.getRooms().put(id, room);
        Server.getAvailableRooms().add(room);

        while (!room.isFull()) {}

        commander.sendObject(room.getOpponent(account));
    }

    private void enterRoom() throws DisconnectException {
        Room selectedRoom = (Room) commander.receiveObject();
        boolean answer = false;
        Set<Room> availableRooms = Server.getAvailableRooms();

        if (availableRooms.contains(selectedRoom)) {
            availableRooms.remove(selectedRoom);
            room = Server.getRooms().get(selectedRoom.hashCode());
            answer = true;
        }

        commander.sendAnswer(answer);

        if (answer) {
            room.addPlayer(account);
            commander.sendObject(room.getOpponent(account));
        }
    }

}
