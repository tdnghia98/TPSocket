import javax.swing.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class ClientGUI extends JFrame implements ActionListener, KeyListener, WindowListener {
    private JButton sendBtn;
    private JButton startBtn;
    private JLabel idLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private static JTextArea conversationTextArea;
    private JTextField messageInputTextField;
    private int id;
    private Client client;

    public ClientGUI() throws UnknownHostException {
        sendBtn = new JButton("Send");
        sendBtn.setBounds(410, 600, 80, 50);
        sendBtn.addActionListener(this);

        startBtn = new JButton("Join");
        startBtn.setBounds(this.getWidth() / 2, this.getSize().height / 2, 100, 50);
        startBtn.addActionListener(this);

        conversationTextArea = new JTextArea();
        conversationTextArea.setBounds(10, 10, 480, 550);

        messageInputTextField = new JTextField();
        messageInputTextField.setBounds(10, 600, 390, 50);
        messageInputTextField.addKeyListener(this);
        idLabel = new JLabel();
        idLabel.setBounds(10, 575, 100, 10);

        add(startBtn);
        setSize(500, 700);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(this);

        client = new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendBtn) {
            try {
                client.sendMessage();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == startBtn) {
            try {
                client.start();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            remove(startBtn);
            add(conversationTextArea);
            add(messageInputTextField);
            add(sendBtn);
            repaint();
        }
    }


    public static void main(String[] args) {
        try {
            ClientGUI cGui = new ClientGUI();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == messageInputTextField) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                try {
                    client.sendMessage();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int getId() {
        return id;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            client.sendMessage("/quit");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }

    class Client {
        private String server;
        private int port;
        InetAddress serverAddress;
        MulticastSocket socket = null;
        ArrayList<String> messages;

        public Client() throws UnknownHostException {
            server = "239.0.0.0";
            port = 1234;
            serverAddress = InetAddress.getByName(server);
            messages = new ArrayList<String>();
        }

        void start() throws IOException {
            byte[] buf = new byte[256];

            try {
                socket = new MulticastSocket(port);
            } catch (IOException e) {
                e.printStackTrace();
            }

            socket.joinGroup(serverAddress);
            conversationTextArea.setText("[DEBUG] Connected. Connection : " + socket.getInetAddress() + ":" + socket.getPort());

            //id = Integer.parseInt();
            idLabel.setText("Your id: " + id);
            add(idLabel);
            System.out.println("Id received: " + id);
            new ListenFromServer().start();
        }

        private void sendMessage() throws IOException {
            String message = messageInputTextField.getText();
            sendMessage(message);
        }

        private void sendMessage(String msg) throws IOException {
            if (!msg.isEmpty()) {
                DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, serverAddress, port);
                socket.send(msgPacket);
                messageInputTextField.setText("");
                messageInputTextField.requestFocus();

                if (msg.equals("/quit")) {
                    System.exit(0);
                }


            }
        }

        class ListenFromServer extends Thread {
            public void run() {
                while (true) {
                    byte[] buf = new byte[256];
                    DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                    try {
                        socket.receive(msgPacket);
                        String message = new String(buf, 0, buf.length);
                        messages.add(message);
                        conversationTextArea.append("\n" + message);
                        System.out.println(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

            }
        }
    }

}

}
