/***
 * EchoClient
 * Example of a TCP client
 * Date: 10/01/04
 * Authors:
 */

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Client extends JFrame implements ActionListener {
    private String server;
    private int port;
    Socket socket = null;
    private BufferedReader socketInput = null;
    BufferedReader stdIn = null;
    private PrintStream socketOutput = null;
    ArrayList<String> messages;


    private javax.swing.JButton sendBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private static javax.swing.JTextArea conversationTextArea;
    private javax.swing.JTextField messageInputTextField;

    public Client(String server, int port) {
        messages = new ArrayList<String>();
        this.server = server;
        this.port = port;
        this.sendBtn = new JButton("send");
        this.sendBtn.setBounds(440, 600, 50, 50);
        this.sendBtn.addActionListener(this);
        this.conversationTextArea = new JTextArea();
        this.conversationTextArea.setBounds(10,10,480, 580);
        this.messageInputTextField = new JTextField();
        this.messageInputTextField.setBounds(10, 600, 420, 50);

        this.add(conversationTextArea);
        this.add(messageInputTextField);
        this.add(sendBtn);
        this.setSize(500, 700);
        this.setLayout(null);
        this.setVisible(true);
    }

    public boolean start() throws IOException {

        try {
            socket = new Socket(server, port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        conversationTextArea.setText("[DEBUG] Connected. Connection : " + socket.getInetAddress() + ":" + socket.getPort());

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
//            System.out.println("From keyboard " + line);
//            System.out.println("Waiting for input... ");
//        	if (line.equals(".")) break;
//        	socOut.println(line);
//            byte[] b = line.getBytes();
//            os.write(b);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String message = messageInputTextField.getText();
        if (!message.isEmpty()) {
            sendMessage(message);
            messageInputTextField.setText("");
            messageInputTextField.requestFocus();
        }
    }

    public void sendMessage(String message) {
        this.socketOutput.println(message);
    }

    class ListenFromServer extends Thread {
        public void run() {
            while (true) {
                String message = null;
                try {
                    message = socketInput.readLine();
                    messages.add(message);
                    conversationTextArea.append("\n" + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(message);
            }
        }
    }

}

