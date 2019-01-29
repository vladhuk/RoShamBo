package com.vladhuk.server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static final int PORT = 5543;

    public static final List<Account> ACCOUNTS = Collections.synchronizedList(new LinkedList<>());

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(PORT);
             BufferedReader command = new BufferedReader(new InputStreamReader(System.in))
        ) {
            System.out.println("Server is running... Press Enter to shutdown.");

            while (!serverSocket.isClosed()) {

                if (command.ready()) {
                    System.out.println("Server shutdown...");
                    serverSocket.close();
                    break;
                }

                System.out.println("Checking for new client...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());
                executorService.execute(new ClientHandler(clientSocket));
            }

            executorService.shutdown();
            System.out.println("Server shutdown...");

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
