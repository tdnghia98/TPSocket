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
                ClientThread ct = new ClientThread(clientSocket, ++uniqueId);
                clientThreads.add(ct);
                ct.start();
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public static synchronized void broadcast(String msg, int senderID) {
        String message = "[" + senderID + "] : " + msg;
        for (ClientThread clientThread: clientThreads) {
            clientThread.sendMessage(message);
        }
    }
}