package com.tony;

import java.sql.*;

public class DBHandle {

    private final static String DBPATH = "";
    public static Connection connection;
    public static Statement statement;

    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DBPATH);
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static String getUser(String name, String password) {
        try {
            String query = String.format("SELECT name FROM users WHERE name = '%s' AND password = '%s'", name, password);
            ResultSet resultSet = statement.executeQuery(query);
            if (resultSet.next())
                return resultSet.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static void disconnect() {
        try {
            statement.close();
            connection.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
