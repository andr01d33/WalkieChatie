/*
 * Inbox is a Thread subclass which creates a TCP serverSocket and continuously
 * listens for incoming Letters. Upon reception of a Letter, a new thread is 
 * spawned (innerclass NewDelivery), which processes the message, returns an 
 * appropriate response, then closes its socket and finishes. The Inbox 
 * listening thread should be closed down by the caller/owner when appropriate 
 * by using the stopService() method or _keepRunning boolean.
 */
/**
 *
 * @author AndyChen
 */
package WalkieChatieLibrary;

import DataContract.*;
import DataContract.DataTypes.*;
import java.beans.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.logging.*;


public class Inbox extends Thread
{
    private boolean _keepRunning = true;
    public String address;
    public Queue<Letter> MessageQueue;
    private ServerSocket serverSocket;
    public int Port;
    private AddressBook addressBook;
    public final Contact owner;

    public Inbox(Contact ownerInfo, AddressBook addrBook) {
        this.listeners = new ArrayList<>();
        this.owner = ownerInfo;
        this.Port = owner.getPort();
        this.addressBook = addrBook;
        MessageQueue = new LinkedList<>();
        
        Letter msg = new Letter(
                MessageType.Server_Started,
                Config.SERVER_NAME,
                Config.SERVER_NAME,
                ""
        );
        
        try {
            serverSocket = new ServerSocket(Port);
            serverSocket.setSoTimeout(Config.PORT_TCP_TIMEOUT);
            Port = serverSocket.getLocalPort();
            owner.setPort(Port);
            
            //address = serverSocket.getInetAddress().toString();
            address = InetAddress.getLocalHost().getHostAddress();
            
            msg.setMessage(address);
        } catch (IOException ex) {
            msg.setMessageType(MessageType.Server_Failed);
            msg.setMessage(ex.getMessage());
            Logger.getLogger(Inbox.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        // to report inbox status
        stackMessage(msg);
    }
    
    public void stopService()
    {
        _keepRunning = false;
    }

    // Connect to the server, loop getting messages
    @Override
    public void run() {
        try {
            System.out.println("Inbox started: " + address);
            
            while (_keepRunning) {
                try {
                    // create a new socket when a connection is made; the socket
                    // is closed by NewDelivery
                    Socket socket = serverSocket.accept();
                    
                    NewDelivery ndelivery = new NewDelivery(socket);
                    Thread thread = new Thread(ndelivery);
                    thread.start();
                }
                catch(SocketTimeoutException e) {
                }
                catch(Exception e) {
                }
                finally {
                    Thread.yield();
                }   
            }
            
            // finished listening, so close server socket
            serverSocket.close();
            
            // report inbox status            
            Letter msg = new Letter(
                    MessageType.Server_Stopped,
                    Config.SERVER_NAME,
                    Config.SERVER_NAME,
                    address
            );
            stackMessage(msg);
            
            System.out.println("Inbox closed: " + address);
        } catch (Exception e) {
            System.err.println("Inbox error: " + e);
        }
    }
    
    private synchronized void stackMessage(Letter msgData) 
    {
        if (msgData != null)
        {
            MessageQueue.add(msgData);
            
            raiseEvent();
        }
    }
    
    private void raiseEvent()
    {
        for (MessageListener item : listeners) {
            item.newMessageArrived();
        }
    }
    
    private final List<MessageListener> listeners;

    public void addNewMessageListener(MessageListener listener) {
        listeners.add(listener);
        
        if (MessageQueue.peek() != null)
        {
            raiseEvent();
        }
    }
    
    private class NewDelivery implements Runnable {

        private final Socket socket;

        public NewDelivery(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ObjectInputStream ois = null;
            ObjectOutputStream oos = null;
          
            try {
                boolean invalidMsg = false;
                //Receive letter
                ois = new ObjectInputStream(socket.getInputStream());
                String xmlString = (String) ois.readObject();
                XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlString.getBytes()));
                Letter letter = (Letter) decoder.readObject();

                //process message
                MessageType type = MessageType.Message_Delivery_Successful;
                String replyMsg = "OK";
                if (letter.getMessageType() == MessageType.User_Login || letter.getMessageType() == MessageType.User_Logout)
                {
                    //append client's information to message.
                    String senderInfo = socket.getInetAddress().toString().replace("/", "") + ":";
                    senderInfo += letter.getMessage();
                    letter.setMessage(senderInfo);
                }

                if (letter.getMessageType() == MessageType.User_Login && addressBook != null) {
                    //Send fail login message.
                    Contact user = addressBook.Lookup(letter.getSender());
                    boolean isValidUserName = user == null || !user.getIsOnline();
                    
                    if ( !isValidUserName){
                        type = MessageType.User_Name_Invalid;
                        replyMsg = "User name \"" + letter.getSender() + "\" is already taken. Please change your name and try again.";
                        invalidMsg = true;
                    }
                }
                
                Letter acknowledgeMessage = new Letter(
                        type,
                        letter.getSender(),
                        owner.getName(),
                        replyMsg
                );
                
                //send respond
                OutputStream memStream = new ByteArrayOutputStream();
                try (XMLEncoder encoder = new XMLEncoder(memStream)) {
                    encoder.writeObject(acknowledgeMessage);
                }
                String xmlStringReplay = memStream.toString();
                oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(xmlStringReplay);
                oos.flush();
                
                if (!invalidMsg) stackMessage(letter);
            } catch (Exception e) {
                System.err.println("Server error: " + e);
            } finally {
                try {
                    if (ois != null)
                        ois.close();
                    if (oos != null)
                        oos.close();
                    if (socket != null) 
                        socket.close();
                } catch (IOException e) {
                    System.err.println("Failed to close streams: " + e);
                }
            }
        }
        
    }
    
} // Inbox
