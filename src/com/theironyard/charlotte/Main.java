package com.theironyard.charlotte;

import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import org.h2.tools.Server;
import spark.Spark;

import java.sql.*;
import java.util.ArrayList;

public class Main {

    public static void createTables(Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE IF NOT EXISTS messages (id IDENTITY, author VARCHAR, text VARCHAR)");
    }

    public static void insertMessage(Connection conn, String author, String text) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO messages VALUES (NULL, ?, ?)");
        stmt.setString(1, author);
        stmt.setString(2, text);
        stmt.execute();
    }

    public static ArrayList<Message> selectMessages(Connection conn) throws SQLException {
        ArrayList<Message> messages = new ArrayList<>();

        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM messages");
        ResultSet results = stmt.executeQuery();
        while (results.next()) {
            int id = results.getInt("id");
            String author = results.getString("author");
            String text = results.getString("text");
            messages.add(new Message(id, author, text));
        }

        return messages;
    }

    public static void main(String[] args) throws SQLException {
        Server.createWebServer().start();
        Spark.staticFileLocation("/public");
        Spark.init();

        Connection conn = DriverManager.getConnection("jdbc:h2:./main");
        createTables(conn);

        Spark.get(
                "/get-messages",
                ((request, response) -> {
                    ArrayList<Message> messages = selectMessages(conn);
                    JsonSerializer s = new JsonSerializer();
                    return s.serialize(messages);
                })
        );

        Spark.post(
                "/add-message",
                ((request, response) -> {
                    String body = request.body();
                    JsonParser p = new JsonParser();
                    Message msg = p.parse(body, Message.class);
                    insertMessage(conn, msg.author, msg.text);
                    return "";
                })
        );
    }
}
