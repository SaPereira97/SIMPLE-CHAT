package io.codeforall.javatars;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final int PORT = 9000;
    private ExecutorService service = Executors.newCachedThreadPool();

    public static Set<String> usernames = new HashSet<>();
    public static ArrayList<Client> clients = new ArrayList();


    public static void main(String[] args) {
        Server myServer = new Server();
        myServer.init();
    }


    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("The port number is: " + PORT);

            while (true) {

                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection");

                Client client = new Client(clientSocket, this);
                service.submit(client);
                this.clients.add(client);
                System.out.println("Accepted");

            }
        } catch (IOException e) {
            System.out.println("ERROR INIT " + e);
        }
    }


    public void removeClients(String username) {
        for (Client client : clients) {
            if (username.equals(client.getUsername())) {
                usernames.remove(client.getUsername());
                clients.remove(client);
            }
        }
    }


    public void sendAll(String input, Client client) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).equals(client) || Client.blockedUsers.contains(client.getUsername())) {
                continue;
            }
            try {
                PrintWriter out = new PrintWriter(clients.get(i).getSocket().getOutputStream(), true);
                out.println(input);
            } catch (IOException e) {
                System.out.println("ERROR SEND ALL" + e);
            }
        }
    }
}