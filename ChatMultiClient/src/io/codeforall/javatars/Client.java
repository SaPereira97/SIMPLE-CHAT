package io.codeforall.javatars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket socket;
    private Server server;

    public Client(Socket socket, Server server) {
        this.socket = socket;
        this.server = server;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Insert your user name: ");
            String name = in.readLine();

            server.sendAll(name + " has entered the chat", this);

            while (true) {
                String input = in.readLine();
                server.sendAll(name + ": " + input, this);
                if (input.equals("exit")) {
                    in.close();
                    out.close();
                    socket.close();
                    server.sendAll(name + " has left the chat", this);
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("ERROR RUN" + e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}


