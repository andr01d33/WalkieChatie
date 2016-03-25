/*
 * Mailbox abstract class.
 * Contains Inbox and Outbox classes for 
 * Subclasses: MailboxClient and MailboxServer.
 */
package WalkieChatieLibrary;

import DataContract.DataTypes;
import DataContract.Letter;
import DataContract.Contact;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
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
    public final Contact owner;
//    protected Contact receiver;
    public Queue<Letter> LetterQueue;
    
    public Mailbox(Contact ownerInfo)
    {
        owner = ownerInfo;
        
        addressBook = new AddressBook();
        LetterQueue = new LinkedList<>();
        
        outbox = new Outbox();   
        inbox = new Inbox(owner, addressBook);    

        this.listeners = new ArrayList<>();
    }
    
    //start receiving messages
    public void start()
    {
        inbox.addNewMessageListener(this);
        inbox.start();
    }
    
    public void stop() 
    {
        inbox.stopService();
        addressBook.map.clear();
    }
    
    public void send(Contact contact, Letter letter) 
    {
         outbox.sendAsync(contact, letter);
    }
    
    protected synchronized void stackLetter(Letter letter) 
    {
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
