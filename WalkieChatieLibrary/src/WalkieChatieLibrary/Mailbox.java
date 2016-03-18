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
public abstract class Mailbox implements DataTypes.MessageListener
{
    protected final Inbox inbox;
    protected final Outbox outbox;
    public final AddressBook addressBook;
    public final Contact onwer;
    public Queue<Letter> LetterQueue;
    
    public Mailbox(Contact ownerInfo)
    {
        onwer = ownerInfo;
        
        addressBook = new AddressBook();
        LetterQueue = new LinkedList<>();
        
        outbox = new Outbox();   
        inbox = new Inbox(onwer.getPort());    
        
        this.listeners = new ArrayList<>();
    }
    
    //start receiving messages
    public void start()
    {
        inbox.addNewMessageListener(this);
        inbox.start();
    }
    
    protected boolean updateClientStatus(DataTypes.MessageType type)
    {
        Letter letter = new Letter(
                type,
                new Contact(onwer.getName(), Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP),
                onwer,
                new Message("N/A")
        );
        
        return outbox.send(letter);
    }
    
    protected synchronized void stackLetter(Letter letter) {
        if (letter != null) {
            LetterQueue.add(letter);

            for (DataTypes.MessageListener item : listeners) {
                item.newMessageArrived();
            }
        }
    }
    
    @Override
    public abstract void newMessageArrived();
    
    private final List<DataTypes.MessageListener> listeners;

    public void addNewMessageListener(DataTypes.MessageListener listener) {
        listeners.add(listener);
    }
}
