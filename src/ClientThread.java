/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */


import java.io.*;
import java.net.*;

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;

    ClientThread(Socket s) throws IOException {
        this.clientSocket = s;
        socIn = null;
        socIn = new BufferedReader(
                new InputStreamReader(this.clientSocket.getInputStream()));
        socOut = new PrintStream(this.clientSocket.getOutputStream());
    }

    /**
     * receives a request from client then sends an echo to the client
     * @param clientSocket the client socket
     **/
    public void run() {
        try {

            while (true) {
                // Receive msg from Client
                String line = this.socIn.readLine();
                // Add msg to msg history list
                System.out.println("Client Thread received message: " + line);
                EchoServerMultiThreaded.broadcast(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void sendMessage(String msg) {
        this.socOut.println("Message from server: " + msg);
    }

}