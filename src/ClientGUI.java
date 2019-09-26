import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;

public class ClientGUI extends JFrame implements ActionListener, KeyListener {
    private JButton sendBtn;
    private JButton startBtn;
    private JLabel idLabel;
    private javax.swing.JScrollPane jScrollPane1;
    private static JTextArea conversationTextArea;
    private JTextField messageInputTextField;
    private int id;
    private Client client;

    public ClientGUI() {
        sendBtn = new JButton("send");
        sendBtn.setBounds(440, 600, 50, 50);
        sendBtn.addActionListener(this);
        startBtn = new JButton("join");
        startBtn.setBounds(this.getSize().width/2, this.getSize().height, 50, 50);
        startBtn.addActionListener(this);
        conversationTextArea = new JTextArea();
        conversationTextArea.setBounds(10,10,480, 550);
        messageInputTextField = new JTextField();
        messageInputTextField.setBounds(10, 600, 420, 50);
        messageInputTextField.addKeyListener(this);
        idLabel = new JLabel();
        idLabel.setBounds(10,575,100,10);

        add(startBtn);
        setSize(500, 700);
        setLayout(null);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        client = new Client();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == sendBtn) {
            client.sendMessage();
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

    public static void main (String[] args) {
        ClientGUI cGui = new ClientGUI();
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getSource() == messageInputTextField) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                client.sendMessage();
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }

    public int getId() {return id;}

    class Client {
        private String server;
        private int port;
        Socket socket = null;
        private BufferedReader socketInput = null;
        BufferedReader stdIn = null;
        private PrintStream socketOutput = null;
        ArrayList<String> messages;

        public Client() {
            server = "localhost";
            port = 5100;
            messages = new ArrayList<String>();
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

            try {
                id = Integer.parseInt(socketInput.readLine());
                idLabel.setText("Your id: " + id);
                add(idLabel);
                System.out.println("Id received: " + id);
            } catch (IOException e) {
                e.printStackTrace();
            }
            new ListenFromServer().start();
            return true;
        }

        public void sendMessage() {
            String message = messageInputTextField.getText();
            if (!message.isEmpty()) {
                this.socketOutput.println(message);
                messageInputTextField.setText("");
                messageInputTextField.requestFocus();
            }

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

}
