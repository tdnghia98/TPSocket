/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

import java.io.*;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;

public class EchoServerMultiThreaded  {
    static ArrayList<ClientThread> clientThreads = new ArrayList<ClientThread>();
    static ArrayList<String> messages = new ArrayList<String>();
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

    public static synchronized void broadcast(String msg, int senderID) {
        String message = msg;
        // -1 is reserved for system message
        if (senderID != -1) {
             message = "[" + senderID + "] : " + msg;
        }
        messages.add(message);
        for (ClientThread clientThread: clientThreads) {
            clientThread.sendMessage(message);
            System.out.println("Sending message to client -- " + clientThread.id + " -- : " + message);
        }

    }

    private static void announceNewJoin(int clientId) {
        String announcement = "[" + clientId + "] has joined the chat!";
        broadcast(announcement, -1);
    }
    private static void sendHistoryToClientThread(ClientThread ct) {
        for (String message: messages) {
            ct.sendMessage(message);
        }
    }
}