package com.vladhuk.roshambo.server;

import com.vladhuk.roshambo.client.Account;

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
