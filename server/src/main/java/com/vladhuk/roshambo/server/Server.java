package com.vladhuk.roshambo.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 5543;

    private static Map<Integer, Account> accounts = Collections.synchronizedMap(new HashMap<>());
    private static Set<Integer> onlineAccounts = Collections.synchronizedSet(new HashSet<>());
    private static List<Room> rooms = Collections.synchronizedList(new LinkedList<>());
    private static List<Room> availableRooms = Collections.synchronizedList(new LinkedList<>());

    public static Map<Integer, Account> getAccounts() {
        return accounts;
    }

    public static Set<Integer> getOnlineAccounts() {
        return onlineAccounts;
    }

    public static List<Room> getRooms() {
        return rooms;
    }

    public static List<Room> getAvailableRooms() {
        return availableRooms;
    }

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getRemoteSocketAddress().toString());
                executorService.execute(new ClientHandler(clientSocket));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            System.out.println("Server shutdown...");
        }
    }
}
