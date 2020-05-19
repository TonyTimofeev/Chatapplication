package com.tony;

import java.io.BufferedWriter;
import java.io.IOException;

public class HandleLogin {

    public static String login(ClientHandle clientHandle, BufferedWriter out) throws IOException {
        String user;
        out.write("Type 'quit' to close program\n");
        out.write("Type 'yourlogin yourpassword' to login'\n");
        out.flush();
        while (true) {
            String str = "";
            try {
                str = clientHandle.in.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(str);
            if (str.equalsIgnoreCase("quit"))
                return null;
            String[] cmd = str.split(" ", 2);
            if (cmd.length == 2) {
                user = DBHandle.getUser(cmd[0], cmd[1]);
                if (user != null) {
                    if (clientHandle.isOnline(user)) {
                        System.out.println("return " + user);
                        return user;
                    }
                    out.write("This user is already online!\n");
                    out.flush();
                } else {
                    out.write("Login or/and password incorrect\n");
                    out.flush();
                }
            }
        }
    }
}
