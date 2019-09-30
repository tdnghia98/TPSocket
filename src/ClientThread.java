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


    public void run() {
        try {
            String line;
            while (true) {
                // Receive msg from Client
                line = this.socIn.readLine();
                messageManager(line);
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void messageManager(String content) {
        String receptionTime;
        if (content.equals("/quit")) {
            quit();
        } else {
            receptionTime = time.format(new Date());
            Message message = new Message(this.id, receptionTime, content, false);
            EchoServerMultiThreaded.broadcast(message);
            Service.addMessage(message);
        }
    }

    public void sendMessage(String msg) {
        this.socOut.println(msg);
    }

    public void quit() {
        EchoServerMultiThreaded.clientThreads.remove(this);
        EchoServerMultiThreaded.announceClientQuit(id);
    }
}