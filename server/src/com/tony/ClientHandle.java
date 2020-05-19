package com.tony;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandle {

    private final ServerMain serverMain;
    private final Socket socket;
    BufferedReader in;
    BufferedWriter out;
    Thread curThread;
    ArrayList<String> blacklist;
    String userName;

    public ClientHandle(ServerMain serverMain, Socket socket) {
        this.serverMain = serverMain;
        this.socket = socket;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.blacklist = new ArrayList<>();

        curThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    userName = HandleLogin.login(ClientHandle.this, out);
                    if (userName == null) {
                        disconnect();
                    }
                    out.write("You are log in like " + userName + "\n");
                    out.flush();
                    serverMain.addUser(ClientHandle.this);
                    out.write("Type 'quit' to close program\n");
                    out.write("Type 'addBlacklist username' to add user to the blacklist\n");
                    out.write("Type 'removeBlacklist username' to remove user from the blacklist\n");
                    out.write("Type 'direct username message' to send direct message to user\n");
                    out.write("Or just type whatever in the chat to send a message to all users(except those who add you to the blacklist)\n");
                    out.flush();
                    while (!curThread.isInterrupted()) {
                        String str = in.readLine();
                        String[] cmd = str.split(" ");
                        if (str.equalsIgnoreCase("quit"))
                                disconnect();
                        else if(cmd[0].equals("addBlacklist")) {
                            if (cmd[1].equals(userName)) {
                                out.write("You can`t add yourself in the blacklist!");
                                out.flush();
                            }
                            else {
                                blacklist.add(cmd[1]);
                                out.write(cmd[1] + " add in the blacklist\n");
                                out.flush();
                            }
                        }
                        else if (cmd[0].equals("removeBlacklist")) {
                            if (cmd[1].equals(userName)) {
                                out.write("You are not on your blacklist!\n");
                                out.flush();
                            }
                            else {
                                blacklist.remove(cmd[1]);
                                out.write(cmd[1] + " remove from the blacklist\n");
                                out.flush();
                            }
                        }
                        else if(cmd[0].equals("direct")) {
                            cmd = str.split(" ", 3);
                            serverMain.sendMsgDirect(cmd[1], cmd[2], ClientHandle.this);
                        }
                        else
                            serverMain.sendMsgAll(str, ClientHandle.this);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        curThread.start();
    }

    private void disconnect() {
        try {
            serverMain.removeUser(ClientHandle.this);
            curThread.interrupt();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isOnline (String user) {
        for (ClientHandle o : serverMain.listOfUsers()) {
            if (o.userName.equals(user))
                return false;
        }
        return true;
    }

    public void getMsg(String msg, ClientHandle from) {
        if (blacklist.contains(from.userName))
            return;
        System.out.println(msg);
        try {
            out.write(from.userName + ":" + msg + "\n");
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
