/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WalkieChatieLibrary;

import DataContract.Config;
import DataContract.DataTypes;
import DataContract.Letter;
import DataContract.Message;
import DataContract.Contact;
import DataContract.DataTypes.MessageType;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 *
 * @author AndyChen
 */
public class Mailbox implements DataTypes.MessageListener
{
    private final Inbox inbox;
    private final Outbox outbox;
    public final AddressBook addressBook;
    public final Contact onwer;
    public final boolean isServer;
    public Queue<Letter> LetterQueue;
    
    public Mailbox(Contact ownerInfo)
    {
        this.listeners = new ArrayList<>();
        outbox = new Outbox(ownerInfo.getPort());   
        inbox = new Inbox(ownerInfo.getPort());
        addressBook = new AddressBook();
        onwer = ownerInfo;
        
        isServer = (onwer.getName() == null ? Config.SERVER_NAME == null : onwer.getName().equals(Config.SERVER_NAME));
        LetterQueue = new LinkedList<>();
    }
    
    //start receiving messages
    public void start()
    {
        inbox.addNewMessageListener(this);
        inbox.start();
    }
    
    public boolean send(String userName, String msg)
    {
        Letter letter = new Letter(
                DataTypes.MessageType.Message_Individual,
                new Contact(userName, Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP),
                onwer,
                new Message(msg)
        );
        
        return outbox.send(letter);
    }
    
    public boolean sendAll(String msg)
    {
        Letter letter = new Letter(
                DataTypes.MessageType.Message_Broadcast,
                new Contact(Config.SERVER_NAME, Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP),
                onwer,
                new Message(msg)
        );
        
        return outbox.send(letter);
    }
    
    //client login
    private boolean updateClientStatus(DataTypes.MessageType type)
    {
        if (isServer) return false;
        Letter letter = new Letter(
                type,
                new Contact(onwer.getName(), Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP),
                onwer,
                new Message("N/A")
        );
        
        return outbox.send(letter);
    }
    
    public boolean login()
    {
        return updateClientStatus(DataTypes.MessageType.User_Login);
    }
    
    public boolean logout()
    {
        inbox.stopService();
        return updateClientStatus(DataTypes.MessageType.User_Logout);
    }
    
    //server to client
    public boolean forward(Letter letter)
    {
        //look up real ip address and port for recipient
        String name = letter.getRecipient().getName();
        
        Contact recipient = addressBook.Lookup(name);
        if (recipient == null)
        {
            returnLetter(letter);
            return false;
        }
        
        letter.setRecipient(recipient);
        if (!outbox.send(letter))
        {
            returnLetter(letter);
            return false;
        }
        
        return true;
    }
    
    public void returnLetter(Letter letter)
    {
        //look up real ip address and port for recipient
        String name = letter.getRecipient().getName();
        
        String msg = "Failed to send \"" + letter.getMessage().getContent() + "\", " +
                name + " is offline or is invisible.";
        
        letter.setMessageType(MessageType.Message_Delivery_Failed.ordinal());
        letter.setMessage(new Message(msg));
        letter.setRecipient(letter.getSender());
        
        outbox.send(letter);
    }

    public void broadcast(Letter letter)
    {
        for (Map.Entry<String, Contact> entry : addressBook.map.entrySet()) {
            letter.setRecipient(entry.getValue());
            outbox.send(letter);
        }    
    }
    
    private synchronized void stackLetter(Letter letter) {
        if (letter != null) {
            LetterQueue.add(letter);

            for (DataTypes.MessageListener item : listeners) {
                item.newMessageArrived();
            }
        }
    }
    
    @Override
    public void newMessageArrived() 
    {
        do {
            Letter letter = inbox.MessageQueue.poll();
            if (letter == null) {
                break;
            }
            
            MessageType msgType = MessageType.parse(letter.getMessageType());
            switch(msgType)
            {
                case Message_Individual:
                    if (isServer) {
                        forward(letter);
                        //add sender to active list
                        addressBook.add(letter.getSender());
                    }
                    else
                    {
                        stackLetter(letter);
                    }
                    break;
                case Message_Broadcast:
                    if (isServer) {
                        if (isServer) broadcast(letter);
                        //add sender to active list
                        addressBook.add(letter.getSender());
                    }
                    else
                    {
                        stackLetter(letter);
                    }
                    break;
                case Message_Delivery_Successful:
                    break;
                case Message_Delivery_Failed:
                    stackLetter(letter);
                    break;
                case User_Update:
                    break;
                case User_Login:
                    if (isServer) addressBook.add(letter.getSender());
                    System.out.println("User logged in: " + letter.getSender().getName() + "\t" + letter.getMessage().getDate());
                    break;
                case User_Logout:
                    if (isServer) addressBook.remove(letter.getSender());
                    System.out.println("User logged out: " + letter.getSender().getName() + "\t" + letter.getMessage().getDate());
                    break;
            }
        } while (true);
    }
    
    private final List<DataTypes.MessageListener> listeners;

    public void addNewMessageListener(DataTypes.MessageListener listener) {
        listeners.add(listener);
    }
}
