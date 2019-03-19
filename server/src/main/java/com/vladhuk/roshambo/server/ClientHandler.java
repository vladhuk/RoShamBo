package com.vladhuk.roshambo.server;

import com.vladhuk.roshambo.server.models.Account;
import com.vladhuk.roshambo.server.services.AccountService;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClientHandler implements Runnable {

    private AccountService accountService = new AccountService();
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
                    case EXIT:
                        exitFromAccount();
                        break;
                }

                Thread.yield();
            }
        } catch (DisconnectException e) {
            disconnect();
        }
    }


    private void login() throws DisconnectException {
        account = (Account) commander.receiveObject();
        account = accountService.find(account);

        boolean answer = (account != null) && !isAccountOnline();
        commander.sendAnswer(answer);

        if (answer) {
            Server.getOnlineAccounts().put(account, this);
        }
    }

    private boolean isAccountOnline() {
        return Server.getOnlineAccounts().containsKey(account);
    }

    private void createAccount() throws DisconnectException {
        account = (Account) commander.receiveObject();

        Account foundedAccount = accountService.find(account);

        boolean answer = (foundedAccount == null) && !accountService.isUsernameExist(account);
        commander.sendAnswer(answer);

        if (answer) {
            accountService.save(account);
            Server.getOnlineAccounts().put(account, this);
        }
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

    private void waitForOpponent() {
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

        ClientHandler opponentsHandler = Server.getOnlineAccounts().get(room.getOpponent(account));
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

        ClientHandler opponentsHandler = Server.getOnlineAccounts().get(opponent);
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

        exitFromAccount();

        if (room != null) {
            leaveRoom();
        }

        commander.closeSocket();
    }

    private void exitFromAccount() {
        if (account != null) {
            Server.getOnlineAccounts().remove(account);
        }
    }

}
