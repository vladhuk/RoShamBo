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
    private Thread waitForOpponentThread;

    public ClientHandler(Socket socket) {
        System.out.println("New connection: " + socket.getRemoteSocketAddress().toString());
        this.socket = socket;
        this.commander = new Commander(socket);
    }

    public Commander getCommander() {
        return commander;
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
                        waitForOpponent();
                        break;
                    case ENTER_ROOM:
                        enterRoom();
                        break;
                    case ITEM:
                        resendItem();
                        break;
                    case LEAVE_ROOM:
                        commander.sendObject(ServerCommand.STOP);
                        leaveRoom();
                        break;
                    case NEW_OPPONENT:
                        waitForOpponent();
                        break;
                }

                Thread.yield();
            }
        } catch (DisconnectException e) {
            disconnect();
        }
    }


    private void login() throws DisconnectException {
        int id = commander.receiveInteger();

        boolean answer = isAccountExist(id) & !isAccountOnline(id);
        commander.sendAnswer(answer);
        if (answer) {
            account = Server.getAccounts().get(id);
            commander.sendObject(account);
            Server.getOnlineAccounts().put(id, this);
        }
    }

    private boolean isAccountExist(int id) {
        return Server.getAccounts().keySet().contains(id);
    }

    private boolean isAccountOnline(int id) {
        return Server.getOnlineAccounts().keySet().contains(id);
    }

    private void createAccount() throws DisconnectException {
        Account account = (Account) commander.receiveObject();
        int id = account.hashCode();

        boolean answer = !isAccountExist(id) & !isNicknameExist(account.getNickname());
        commander.sendAnswer(answer);

        if (answer) {
            Server.getAccounts().put(id, account);
            Server.getOnlineAccounts().put(id, this);
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
        List<Room> list = new LinkedList<>(Server.getAvailableRooms());
        commander.sendObject(list);
    }

    private void addRoom() throws DisconnectException {
        room = (Room) commander.receiveObject();
        int id = room.hashCode();

        Server.getRooms().put(id, room);
        Server.getAvailableRooms().add(room);
    }

    public void waitForOpponent() {
        waitForOpponentThread = new Thread(new WaitForOpponent(room));
        waitForOpponentThread.start();
    }

    private class WaitForOpponent implements Runnable {
        private Room room;

        private WaitForOpponent(Room room) {
            this.room = room;
        }

        @Override
        public void run() {
            while ((room != null) && !room.isFull()) {
                Thread.yield();
            }

            if (room == null) {
                return;
            }

            try {
                commander.sendObject(ServerCommand.NEW_OPPONENT);
                commander.sendObject(room.getOpponent(account));
            } catch (DisconnectException e) {
                disconnect();
            }
        }
    }

    private void enterRoom() throws DisconnectException {
        Room selectedRoom = (Room) commander.receiveObject();
        boolean answer = false;
        Set<Room> availableRooms = Server.getAvailableRooms();

        if (availableRooms.contains(selectedRoom)) {
            availableRooms.remove(selectedRoom);
            room = Server.getRooms().get(selectedRoom.hashCode());
            room.addPlayer(account);
            answer = true;
        }

        commander.sendAnswer(answer);

        if (answer) {
            commander.sendObject(ServerCommand.NEW_OPPONENT);
            commander.sendObject(room.getOpponent(account));
        }
    }

    private void resendItem() throws DisconnectException {
        String item = (String) commander.receiveObject();

        ClientHandler opponentsHandler = Server.getOnlineAccounts().get(room.getOpponent(account).hashCode());
        Commander opponentsCommander = opponentsHandler.getCommander();
        try {
            opponentsCommander.sendObject(ServerCommand.ITEM);
            opponentsCommander.sendObject(item);
        } catch (DisconnectException e) {
            opponentsHandler.disconnect();
        }
    }

    private void leaveRoom() {
        if (isThreadRunning(waitForOpponentThread)) {
            waitForOpponentThread.interrupt();
        }

        Account opponent = room.getOpponent(account);

        if (opponent == null) {
            Server.getRooms().remove(room.hashCode());
            Server.getAvailableRooms().remove(room);
            room = null;
            return;
        }

        room.removePlayer(account);

        ClientHandler opponentsHandler = Server.getOnlineAccounts().get(opponent.hashCode());
        Commander opponentsCommander = opponentsHandler.getCommander();
        try {
            opponentsCommander.sendObject(ServerCommand.LEAVE_ROOM);
        } catch (DisconnectException e) {
            opponentsHandler.disconnect();
        }

        Server.getAvailableRooms().add(room);
    }

    private boolean isThreadRunning(Thread thread) {
        return (thread != null) && thread.isAlive();
    }

    public void disconnect() {
        System.out.println(socket.getRemoteSocketAddress().toString() + " was disconnected.");

        if (account != null) {
            Server.getOnlineAccounts().remove(account.hashCode());
        }

        if (room != null) {
            leaveRoom();
        }

        commander.closeSocket();
    }

}
