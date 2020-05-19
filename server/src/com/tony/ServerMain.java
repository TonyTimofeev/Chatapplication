package com.tony;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerMain {

    private final ArrayList<ClientHandle> users;
    private ServerSocket serverSocket;
    private Socket socket;

    public ServerMain() {
        users = new ArrayList<>();
        try {
            serverSocket = new ServerSocket(6569);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server running");
        DBHandle.connect();

        try {
            while (true) {
                socket = serverSocket.accept();
                System.out.println("Client connected " + socket.toString());
                new ClientHandle(this, socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            DBHandle.disconnect();
            System.out.println("DB disconnect");
        }
    }

    public ArrayList<ClientHandle> listOfUsers() {
        return users;
    }

    public void addUser(ClientHandle user) {
        synchronized (users) {
            users.add(user);
        }
    }

    public void removeUser(ClientHandle user) {
        synchronized (users) {
            users.remove(user);
        }
    }

    public void sendMsgAll(String msg, ClientHandle from) {
        for (ClientHandle c : users)
            if (c != from)
                c.getMsg(msg, from);
    }

    public void sendMsgDirect(String to, String msg, ClientHandle from) {
        for (ClientHandle c : users)
            if(c.userName.equals(to))
                c.getMsg(msg,from);
    }
}
