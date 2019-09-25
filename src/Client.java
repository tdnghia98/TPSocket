/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */

import java.io.*;
import java.net.*;
import java.util.Scanner;


public class Client {
    private String server;
    private int port;
    Socket socket = null;
    private BufferedReader socketInput = null;
    BufferedReader stdIn = null;
    private PrintStream socketOutput = null;


    public Client(String server, int port) {
        this.server = server;
        this.port = port;
    }

    public boolean start() throws IOException {
        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Connected. Connection : " + socket.getInetAddress() + ":" + socket.getPort());

        try {
            socketInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            socketOutput = new PrintStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        new ListenFromServer().start();
        return true;
    }

    public static void main(String[] args) throws IOException {
        String server = "localhost";
        int port = 5100;

        Client c = new Client(server, port);
        c.start();

        String line = "";
        Scanner in = new Scanner(System.in);
        while (true) {
//            System.out.println("Input something...");
            while(in.hasNextLine()) {
                line = in.nextLine();
//                System.out.println("From keyboard in while " + line);
                c.socketOutput.println(line);

                break;
            }
            if (line.equals(".")) break;
//            System.out.println("From keyboard " + line);
//            System.out.println("Waiting for input... ");
//        	if (line.equals(".")) break;
//        	socOut.println(line);
//            byte[] b = line.getBytes();
//            os.write(b);

        }
    }

    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                String message = null;
                try {
                    message = (String) socketInput.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(message);
            }
        }
    }
}


