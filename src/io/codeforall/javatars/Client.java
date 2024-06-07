package io.codeforall.javatars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class Client implements Runnable {

    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private String input;


    public Client(Socket socket, Server server) throws IOException {
        this.socket = socket;
        this.server = server;
    }


    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("Insert your user name: ");
            userName = "\u001b[31m" + in.readLine().toUpperCase() + "\u001b[0m";
            Server.clients.add(userName);

            out.println("\nYou have enter the chat");
            out.println("This users are also in the chat: " + Server.clients + "\n");

            server.sendAll(userName + " has entered the chat", this);

            while (true) {
                input = in.readLine();
                if (input.equals("exit")) {
                    in.close();
                    out.close();
                    socket.close();
                    server.sendAll(userName + " has left the chat", this);
                    break;
                }
                if (input.equals("whisper")) {
                    whisper();
                    continue;
                }
                server.sendAll(userName + ": " + input, this);
            }
        } catch (IOException e) {
            System.out.println("ERROR RUN" + e);
        }
    }

    public Socket getSocket() {
        return socket;
    }


    public void whisper() throws IOException {
        out.println("Write the user name you want to send a private message");
        input = "\u001b[31m" + in.readLine().toUpperCase() + "\u001b[0m";
        for (Client client : Server.clientSocket) {
            if (input.equals(client.userName)) {
                input = in.readLine();
                client.out.println(userName + " Whisper: " + input);
                break;
            }
            out.println("Invalid username");
            input = "\u001b[31m" + in.readLine().toUpperCase() + "\u001b[0m";
        }
    }

}


