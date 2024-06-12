package io.codeforall.javatars;

public class Main {

    public static void main(String[] args) {
        try {
            Server myServer = new Server();
            myServer.init();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
        }
    }
}
