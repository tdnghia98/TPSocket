/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class EchoServerMultiThreaded  {
    static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
    static ArrayList<String> messages = new ArrayList<String>();
    private static final String pattern = "dd-MM-yyyy HH:mm:ss";
    private static SimpleDateFormat time = new SimpleDateFormat(pattern);
    /**
     * main method
     * @param EchoServer port
     *
     **/
    public static void main(String args[]){
        ServerSocket listenSocket;
        int uniqueId = 0;

        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        try {
            listenSocket = new ServerSocket(Integer.parseInt(args[0])); //port

            System.out.println("Server ready... Listen Socket: " + listenSocket);
            while (true) {
                Socket clientSocket = listenSocket.accept();
                System.out.println("Connection from:" + clientSocket.getInetAddress() + " || New client request received :" + clientSocket);
                new PrintStream(clientSocket.getOutputStream()).println(uniqueId);
                ClientThread ct = new ClientThread(clientSocket, uniqueId);
                clientThreads.add(ct);
                sendHistoryToClientThread(ct);
                announceNewJoin(uniqueId);
                uniqueId++;
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public static synchronized void broadcast(String msg) {
        // Store message to history
        messages.add(msg);
        for (ClientThread clientThread: clientThreads) {
            clientThread.sendMessage(msg);
            System.out.println("Sending message to client -- " + clientThread.id + " -- : " + msg);
        }

    }

    private static void announceNewJoin(int clientId) {
        String joinTime  = "[" + time.format(new Date()) + "]";
        String announcement = "~ ANNOUNCEMENT ~ "+ joinTime + " [" + clientId + "] has joined the chat!";
        broadcast(announcement);
    }
    private static void sendHistoryToClientThread(ClientThread ct) {
        for (String message: messages) {
            ct.sendMessage(message);
        }
    }
}