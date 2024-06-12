package io.codeforall.javatars;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Client implements Runnable {

    private Socket socket;
    private Server server;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private String input;

    public static Set<String> blockedUsers = new HashSet<>();


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
            out.println("If you need to access the commands again just write \u001b[31m/help\u001b[0m\n");

            server.sendAll(username + " has entered the chat", this);

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
                if (input.equals("/block")) {
                    block();
                    continue;
                }
                if (input.equals("/unblock")) {
                    unblock();
                    continue;
                }
                if (input.equals("/bye")) {
                    exit();
                    break;
                }
                server.sendAll(username + ": " + input, this);
            }
        } catch (IOException e) {
            System.out.println("ERROR RUN" + e);
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public String getUsername() {
        return username;
    }


    private void welcome() throws IOException {

        boolean nameAccepted = false;

        while (!nameAccepted) {
            out.println("Insert your user name: ");
            username = "\u001B[32m" + in.readLine().toUpperCase() + "\u001b[0m";

            if (Server.usernames.contains(username)) {
                out.println("\n" + Server.usernames);
                out.println("\nYou chose a name that is already in use. Please choose a different name");
            } else {
                Server.usernames.add(username);
                nameAccepted = true;
                out.println("\nYou have entered the chat");
                out.println("These are the users currently online in the chat: " + Server.usernames);
            }
        }
    }


    private void help() {
        out.println("\u001b[31m\nCommands:\n/users - list of online users\n/whisper - private chat\n/exit - exit private chat\n" +
                "/block - block user\n/unblock - unblock user\n/bye - exit chat\n\u001b[0m");
    }


    private void listUsers() {
        out.println(Server.usernames);
    }


    private void whisper() throws IOException {

        boolean valid = false;
        out.println("\nWrite the user name from the list that you want to send a private message");
        out.println(Server.usernames);

        Client whisperUser = null;

        // Loop until a valid username is entered
        while (!valid) {

            input = "\u001B[32m" + in.readLine().toUpperCase() + "\u001b[0m";

            for (Client client : Server.clients) {
                if (input.equals(client.username)) {
                    whisperUser = client;
                    out.println("\nYou have entered a private chat with " + client.username);
                    valid = true;
                    break;  // Exit the loop since we found a valid user
                }
            }
            if (!valid) {
                out.println("\nInvalid user name, choose a name from the list: " + Server.usernames);
            }
        }
        while (true) {

            try {
                String input = in.readLine();

                if (input.equals("exit")) {
                    out.println("\nYou have left the private chat");
                    break;
                }
                whisperUser.out.println("Private message from " + username + ": " + input);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void block() throws IOException {

        boolean valid = false;

        out.println("\nWrite the user name from the list that you want to block");
        out.println(Server.usernames);

        while (!valid) {

            input = "\u001B[32m" + in.readLine().toUpperCase() + "\u001b[0m";

            for (Client client : Server.clients) {
                if (input.equals(client.username)) {
                    client.out.println("\n" + username + " \u001B[31mblocked you. The users of this chat no longer receive your messages\n\u001b[0m");
                    blockedUsers.add(client.username);
                    out.println("\u001B[31m\nYou blocked\u001b[0m " + client.username + "\n");
                    valid = true;
                    break;
                }
            }
            if (!valid) {
                out.println("\nInvalid user name, choose a name from the list: " + Server.usernames);
            }
        }
    }


   private void unblock() throws IOException {

       boolean valid = false;

       out.println("\nWrite the user name from the list that you want to unblock");
       out.println(blockedUsers);

       while (!valid) {

           input = "\u001B[32m" + in.readLine().toUpperCase() + "\u001b[0m";

           for (Client client : Server.clients) {
               if (input.equals(client.username)) {
                   client.out.println("\n" + username + " \u001B[34munblocked you. You can now comunicate again in this chat\n\u001b[0m");
                   blockedUsers.remove(client.username);
                   out.println("\u001B[34m\nYou unblocked\u001b[0m " + client.username + "\n");
                   valid = true;
                   break;
               }
           }
           if (!valid) {
               out.println("\nInvalid user name, choose a name from the list: " + blockedUsers);
           }
       }
    }


    private void exit() {

        try {

            out.println("\nSEE YOU SOON!");
            in.close();
            out.close();
            socket.close();
            server.sendAll(username + " has left the chat", this);
            server.removeClients(username);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

}