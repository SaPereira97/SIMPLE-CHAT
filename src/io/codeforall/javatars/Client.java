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

            welcome();
            help();
            out.println("If you need to access the commands again just write \u001b[32m/help\u001b[0m\n");

            server.sendAll(userName + " has entered the chat", this);

            while (true) {
                input = in.readLine();

                if (input.equals("/help")) {
                    help();
                    continue;
                }
                if (input.equals("/users")) {
                    listUsers();
                    continue;
                }
                if (input.equals("/whisper")) {
                    whisper();
                    continue;
                }
                if (input.equals("/bye")) {
                    exit();
                    break;
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

    public String getUserName() {
        return userName;
    }


    private void welcome() throws IOException {

        boolean nameAccepted = false;

        while (!nameAccepted) {
            out.println("Insert your user name: ");
            userName = "\u001b[31m" + in.readLine().toUpperCase() + "\u001b[0m";

            if (Server.userNames.contains(userName)) {
                out.println("\n" + Server.userNames);
                out.println("\nYou chose a name that is already in use. Please choose a different name.");
            } else {
                Server.userNames.add(userName);
                nameAccepted = true;
                out.println("\nYou have entered the chat.");
                out.println("These are the users currently online in the chat: " + Server.userNames);
            }
        }
    }


    private void help() {
        out.println("\u001b[32m\nCommands:\n/users - list of online users\n/whisper - private chat\n/exit - exit private chat\n" +
                "/block [username] - block user\n/unblock [username] - unblock user\n/bye - exit chat\n\u001b[0m");
    }


    private void listUsers() {
        out.println(Server.userNames);
    }


    private void whisper() throws IOException {

        boolean valid = false;
        out.println("Write the user name from the list that you want to send a private message");
        out.println(Server.userNames);

        Client whisperUser = null;

        // Loop until a valid username is entered
        while (!valid) {
            input = "\u001b[31m" + in.readLine().toUpperCase() + "\u001b[0m";
            valid = false;

            for (Client client : Server.clients) {
                if (input.equals(client.userName)) {
                    whisperUser = client;
                    out.println("You have entered a private chat with " + client.userName);
                    valid = true;
                    break;  // Exit the loop since we found a valid user
                }
            }
            if (!valid) {
                out.println("Invalid user name, choose a name from the list: " + Server.userNames);
            }
        }
        while (true) {

            try {
                String input = in.readLine();

                if (input.equals("exit")) {
                    out.println("You have left the private chat.");
                    break;
                }
                whisperUser.out.println("Private message from " + userName + ": " + input);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void exit() {

        try {

            out.println("See you soon!");
            in.close();
            out.close();
            socket.close();
            server.sendAll(userName + " has left the chat", this);
            server.removeClients(userName);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}