/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WalkieChatieLibrary;

import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import DataContract.Message;
import java.util.Map;

/**
 *
 * @author Andy
 */
public class MailboxServer extends Mailbox{
    
    public MailboxServer(Contact ownerInfo) {
        super(ownerInfo);
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
        
        letter.setMessageType(DataTypes.MessageType.Message_Delivery_Failed);
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
    
    @Override
    public void newMessageArrived()
    {
        do {
            Letter letter = inbox.MessageQueue.poll();
            if (letter == null) {
                break;
            }

            DataTypes.MessageType msgType = letter.getMessageType();
            switch (msgType) {
                case Message_Individual:
                    forward(letter);
                    //add sender to active list
                    addressBook.add(letter.getSender());
                    break;
                case Message_Broadcast:
                    broadcast(letter);
                    //add sender to active list
                    addressBook.add(letter.getSender());
                    break;
                case Message_Delivery_Successful:
                    break;
                case Message_Delivery_Failed:
                    break;
                case User_Update:
                    break;
                case User_Login:
                    addressBook.add(letter.getSender());
                    System.out.println("User logged in: " + letter.getSender().getName() + "\t" + letter.getMessage().getDate());
                    break;
                case User_Logout:
                    addressBook.remove(letter.getSender());
                    System.out.println("User logged out: " + letter.getSender().getName() + "\t" + letter.getMessage().getDate());
                    break;
            }
        } while (true);
    }
}
