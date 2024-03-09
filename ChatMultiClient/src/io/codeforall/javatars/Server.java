package io.codeforall.javatars;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public final int PORT = 9000;
    public ExecutorService service = Executors.newCachedThreadPool();
    public ArrayList<Client> clientSocket = new ArrayList();

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
                this.clientSocket.add(client);
                System.out.println("Accepted");

            }
        } catch (IOException e) {
            System.out.println("ERROR INIT " + e);
        }
    }

    public void sendAll(String input, Client client) {
        for (int i = 0; i < clientSocket.size(); i++) {
            if (clientSocket.get(i).equals(client)) {
                continue;
            }
            try {
                PrintWriter out = new PrintWriter(clientSocket.get(i).getSocket().getOutputStream(), true);
                out.println(input);
            } catch (IOException e) {
                System.out.println("ERROR SEND ALL" + e);
            }
        }
    }
}




