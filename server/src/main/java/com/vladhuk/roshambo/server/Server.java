package com.vladhuk.roshambo.server;

import com.vladhuk.roshambo.server.handler.ClientHandler;
import com.vladhuk.roshambo.server.model.Account;
import com.vladhuk.roshambo.server.model.Room;
import com.vladhuk.roshambo.server.util.ServerSessionFactory;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final int PORT = 5543;

    private static String databaseURL;
    private static String databaseUsername;
    private static String databasePassword;

    private static Map<Account, ClientHandler> onlineAccounts = Collections.synchronizedMap(new HashMap<>());
    private static Map<Integer, Room> rooms = Collections.synchronizedMap(new HashMap<>());
    private static Set<Room> availableRooms = Collections.synchronizedSet(new HashSet<>());

    public static Map<Account, ClientHandler> getOnlineAccounts() {
        return onlineAccounts;
    }

    public static Map<Integer, Room> getRooms() {
        return rooms;
    }

    public static Set<Room> getAvailableRooms() {
        return availableRooms;
    }

    public static void main(String[] args) {
        try {
            readDatabasePropertiesFromUser();
            ServerSessionFactory.configure(databaseURL, databaseUsername, databasePassword);
            startServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void readDatabasePropertiesFromUser() throws IOException {
        try(BufferedReader in = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Enter MySQL url (example: \"jdbc:mysql://localhost:3306/java_test?" +
                                       "useSSL=false&serverTimezone=Europe/Kiev\"):");
            databaseURL = in.readLine();

            System.out.println("Enter MySQL username:");
            databaseUsername = in.readLine();

            System.out.println("Enter MySQL password:");
            databasePassword = in.readLine();
        }
    }

    private static void startServer() throws IOException {
        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                executorService.execute(new ClientHandler(clientSocket));
            }

        } finally {
            executorService.shutdown();
            System.out.println("Server shutdown...");
        }
    }
}
