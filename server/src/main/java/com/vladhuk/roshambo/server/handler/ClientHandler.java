package com.vladhuk.roshambo.server.handler;

import com.vladhuk.roshambo.server.util.ServerConnector;
import com.vladhuk.roshambo.server.model.Room;
import com.vladhuk.roshambo.server.Server;
import com.vladhuk.roshambo.server.model.Account;
import com.vladhuk.roshambo.server.service.AccountService;
import com.vladhuk.roshambo.server.util.DisconnectException;
import com.vladhuk.roshambo.server.util.ServerCommand;

import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ClientHandler implements Runnable {

    private AccountService accountService = new AccountService();
    private Socket socket;
    private ServerConnector connector;
    private Account account;
    private Room room;
    private Thread waitForOpponentThread;

    public ClientHandler(Socket socket) {
        System.out.println("New connection: " + socket.getRemoteSocketAddress().toString());
        this.socket = socket;
        this.connector = new ServerConnector(socket);
    }

    public ServerConnector getConnector() {
        return connector;
    }

    @Override
    public void run() {
        try {
            while (true) {
                ServerCommand command = (ServerCommand) connector.receiveObject();

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
                        connector.sendObject(ServerCommand.STOP);
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
        account = (Account) connector.receiveObject();
        account = accountService.find(account);

        boolean answer = (account != null) && !isAccountOnline();
        connector.sendAnswer(answer);

        if (answer) {
            Server.getOnlineAccounts().put(account, this);
        }
    }

    private boolean isAccountOnline() {
        return Server.getOnlineAccounts().containsKey(account);
    }

    private void createAccount() throws DisconnectException {
        account = (Account) connector.receiveObject();

        Account foundedAccount = accountService.find(account);

        boolean answer = (foundedAccount == null) && !accountService.isUsernameExist(account);
        connector.sendAnswer(answer);

        if (answer) {
            accountService.save(account);
            Server.getOnlineAccounts().put(account, this);
        }
    }

    private void sendUsersNumber() throws DisconnectException {
        connector.sendInteger(Server.getOnlineAccounts().size());
    }

    private void sendAvailableRoomsList() throws DisconnectException {
        List<Room> list = new LinkedList<>(Server.getAvailableRooms());
        connector.sendObject(list);
    }

    private void addRoom() throws DisconnectException {
        room = (Room) connector.receiveObject();
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
                connector.sendObject(ServerCommand.NEW_OPPONENT);
                connector.sendObject(room.getOpponent(account));
            } catch (DisconnectException e) {
                disconnect();
            }
        }
    }

    private void enterRoom() throws DisconnectException {
        Room selectedRoom = (Room) connector.receiveObject();
        boolean answer = false;
        Set<Room> availableRooms = Server.getAvailableRooms();

        if (availableRooms.contains(selectedRoom)) {
            availableRooms.remove(selectedRoom);
            room = Server.getRooms().get(selectedRoom.hashCode());
            room.addPlayer(account);
            answer = true;
        }

        connector.sendAnswer(answer);

        if (answer) {
            connector.sendObject(ServerCommand.NEW_OPPONENT);
            connector.sendObject(room.getOpponent(account));
        }
    }

    private void resendItem() throws DisconnectException {
        String item = (String) connector.receiveObject();

        ClientHandler opponentsHandler = Server.getOnlineAccounts().get(room.getOpponent(account));
        ServerConnector opponentsCommander = opponentsHandler.getConnector();
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
        ServerConnector opponentsCommander = opponentsHandler.getConnector();
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

        connector.closeSocket();
    }

    private void exitFromAccount() {
        if (account != null) {
            Server.getOnlineAccounts().remove(account);
        }
    }

}
