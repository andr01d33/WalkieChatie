/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author AndyChen
 */
package WalkieChatieLibrary;

import DataContract.Message;
import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import DataContract.DataTypes.MessageListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Inbox extends Thread
{
    private boolean _keepRunning = true;
    public String address;
    public Queue<Letter> MessageQueue;
    
    public final int Port;

    public Inbox(int listeningPort) {
        this.listeners = new ArrayList<>();
        this.Port = listeningPort;
        MessageQueue = new LinkedList<>();
    }
    
    public void stopService()
    {
        _keepRunning = false;
    }

    // Connect to the server, loop getting messages
    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Port);
            address = serverSocket.getLocalSocketAddress().toString();
            System.out.println("Inbox started: " + address);
            
            while (_keepRunning) {
                Socket socket = serverSocket.accept();

                NewDelivery echo = new NewDelivery(socket);
                Thread thread = new Thread(echo);
                thread.start();
                
                Thread.yield();
            }
            
            System.out.println("Inbox closed: " + address);
        } catch (Exception e) {
            System.err.println("Server error: " + e);
        }
    }
    
    private synchronized void stackMessage(Letter msgData) 
    {
        if (msgData != null)
        {
            MessageQueue.add(msgData);
            
            for (MessageListener item : listeners) {
                item.newMessageArrived();
            }
        }
    }
    
    private final List<MessageListener> listeners;

    public void addNewMessageListener(MessageListener listener) {
        listeners.add(listener);
    }
    
    private class NewDelivery implements Runnable {

        private final Socket socket;
        private final Letter acknowledgeMessage;

        public NewDelivery(Socket socket) {
            this.socket = socket;
            
            acknowledgeMessage = new Letter(
                    DataTypes.MessageType.Message_Delivery_Successful,
                    new Contact(),
                    new Contact(),
                    new Message("OK")
            );
        }

        @Override
        public void run() {
            try {
                //Receive letter
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                String xmlString = (String) ois.readObject();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
                Letter letter = (Letter) decoder.readObject();

                //send respond
                OutputStream memStream = new ByteArrayOutputStream();
                try (XMLEncoder encoder = new XMLEncoder(memStream)) {
                    encoder.writeObject(acknowledgeMessage);
                }
                String xmlStringReplay = memStream.toString();
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(xmlStringReplay);
                oos.flush();
                
                Contact sender = new Contact(
                        letter.getSender().getName(), 
                        socket.getInetAddress().toString().replace("/", ""),
                        letter.getSender().getPort());
                
                letter.setSender(sender);
                stackMessage(letter);
            } catch (Exception e) {
                System.err.println("Server error: " + e);
            } finally {
                try {
                    if (socket != null) {
                        socket.close();
                    }
                } catch (IOException e) {
                    System.err.println("Failed to close streams: " + e);
                }
            }
        }
    }
}
