/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

public class Server {
    private int clientNumber = 0;
    static ArrayList<Message> messages = new ArrayList<Message>();
    private static final String pattern = "dd-MM-yyyy HH:mm:ss";
    private static SimpleDateFormat time = new SimpleDateFormat(pattern);
    public int id;
    static MulticastSocket listenSocket;
    private static String server = "239.0.0.0";
    private static int port = 1234;
    private static InetAddress serverAddress = null;

    public Server () throws UnknownHostException {
        serverAddress = InetAddress.getByName(server);
        try {
            listenSocket = new MulticastSocket(port); //port
            System.setProperty("java.net.preferIPv4Stack", "true");
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> addressesFromNetworkInterface = networkInterface.getInetAddresses();
                while (addressesFromNetworkInterface.hasMoreElements()) {
                    InetAddress inetAddress = addressesFromNetworkInterface.nextElement();
                    if (inetAddress.isSiteLocalAddress()
                            && !inetAddress.isAnyLocalAddress()
                            && !inetAddress.isLinkLocalAddress()
                            && !inetAddress.isLoopbackAddress()
                            && !inetAddress.isMulticastAddress()) {
                        listenSocket.setNetworkInterface(NetworkInterface.getByName(networkInterface.getName()));
                    }
                }
            }
            listenSocket.joinGroup(serverAddress);
            System.out.println("Distributor ready... Listen Socket: " + listenSocket);
            JPAUtil.init();
            fetchHistoryMessages();
            while (true) {
                byte[] buf = new byte[256];
                DatagramPacket msgPacket = new DatagramPacket(buf, buf.length);
                try {
                    listenSocket.receive(msgPacket);
                    String message = new String(msgPacket.getData(), 0, msgPacket.getLength());
                    treatClientRequest(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public static void main(String args[]) throws UnknownHostException {
        Server s = new Server();
    }

    private void treatClientRequest(String content) throws IOException {
        String[] splitContent = content.split("__");
        System.out.println("Treating client request: " + content + "\nSplit content: ");
        for (String s: splitContent) {
            System.out.print(s +" ");
        }
        String contentType = splitContent[0];
        System.out.println();
        if (splitContent.length == 3) {
            String uniqueKey = splitContent[1];
            String contentRequest = splitContent[2];
            if (contentType.equals("sys")) {
                if (!uniqueKey.equals("")) {
                    if (contentRequest.equals("id")) {
                        String idResponse = createOutputMessage(uniqueKey, "id", String.valueOf(clientNumber));
                        sendMessage(idResponse);
                        clientNumber++;
                        for (Message message : messages) {
                            String distributedMessageResponse = createOutputMessage(uniqueKey, "mess", message.format());
                            sendMessage(distributedMessageResponse);
                        }
                    }
                }
            }
        } else if (contentType.equals("mess")) {
        }
    }

    private Message createMessageFromReceivedContent (String content) {

    }

    private String createOutputMessage(String uniqueKey, String topic, String content) {
        String formattedMessage = "__" + "sys" + "__" + uniqueKey + "__" + topic + "__" + content;
        System.out.println("Output message: " + formattedMessage);
        return formattedMessage;
    }

    private void sendMessage(String msg) throws IOException {
        if (!msg.isEmpty()) {
            DatagramPacket msgPacket = new DatagramPacket(msg.getBytes(), msg.getBytes().length, serverAddress, port);
            listenSocket.send(msgPacket);
        }
    }


    private static void announceNewJoin(int clientId) {
        String joinTime = time.format(new Date());
        String announcement = " [" + clientId + "] has joined the chat!";
        Message message = new Message(-1, joinTime, announcement, true);
    }

    private void fetchHistoryMessages() {
        messages = Service.getAllMessages();
        System.out.println("Fetched messages: ");
        for (Message message : messages) {
            System.out.println(message.format());
        }
    }

    public static void announceClientQuit(int clientId) {
        String quitTime = time.format(new Date());
        String announcement = " [" + clientId + "] has left the chat!";
        Message message = new Message(-1, quitTime, announcement, true);
    }
}