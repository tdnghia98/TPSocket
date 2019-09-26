/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */


import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientThread
        extends Thread {

    private Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;
    private final String pattern = "dd-MM-yyyy HH:mm:ss";
    private SimpleDateFormat time = new SimpleDateFormat(pattern);
    public int id;

    ClientThread(Socket s, int id) throws IOException {
        this.id = id;
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
            String line;
            String receptionTime;
            String message;
            while (true) {
                // Receive msg from Client
                line = this.socIn.readLine();
                receptionTime = "[" + time.format(new Date()) + "]";
                message = receptionTime + " [" + id + "] : " + line;
                System.out.println(receptionTime + " Client Thread " + id + " received message: " + line);
                EchoServerMultiThreaded.broadcast(message);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void sendMessage(String msg) {
        this.socOut.println(msg);
    }

}