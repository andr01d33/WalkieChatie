/*
 * MailboxServer is a subclass of Mailbox designed to be instantiated by the
 * chat server program. This class takes care of extracting Letters received to
 * the Inbox, and maintaining the AddressBook of clients who have connected to the
 * system, passing on Letters to the appropriate clients via the server's Outbox.
 * In addition, a regularly scheduled timer sends out a list of users on the 
 * system to all connected clients via UDP - the MailboxClient class listens
 * for these packets.
 */
package WalkieChatieLibrary;

import DataContract.Config;
import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.*;
import javax.swing.Timer;

/**
 *
 * @author AndyChen
 */
public class MailboxServer extends Mailbox {
  
    private Timer timer;
    
    public MailboxServer(String address, int port) {
        super(new Contact(Config.SERVER_NAME, address, port, 0, true));
        
        // set timer to update client list
        timer = new Timer(Config.UDP_INTERVAL, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                broadcastClients();
            }
        });
        timer.start();
    }
    
    public void stop() {
      timer.stop();
      
      // disconnect all online clients with a final message, and shut down:
      Vector<Contact> list = new Vector<Contact>();
      for (Map.Entry<String, Contact> entry : addressBook.map.entrySet())
        if (entry.getValue().getIsOnline())
          list.add(entry.getValue());
      for (Contact contact : list)
        outbox.send(contact, new Letter(DataTypes.MessageType.User_Logout, contact.getName(), "Server", "Server stopping."));
      
      
      super.stop();
    }
    
    // server to client
    private boolean forward(Letter letter)
    {
        //look up real ip address and port for recipient
        String name = letter.getRecipient();
        
        Contact recipient = addressBook.Lookup(name);
        if (recipient == null)
        {
            returnLetter(letter);
            return false;
        }
        
        letter.setRecipient(recipient.getName());
        if (outbox.send(recipient, letter) == null)
        {
            returnLetter(letter);
            return false;
        }
        
        return true;
    }
    
    // echo a message back to its sender 
    private boolean echo(Letter letter) {
      Contact recipient = addressBook.Lookup(letter.getSender());
      if (recipient != null) {
        outbox.send(recipient, letter);
        return true;
      }       
      return false;
    }
    
    
    private void returnLetter(Letter letter)
    {
        // look up real ip address and port for recipient
        String name = letter.getRecipient();
        
        String msg = "Failed to send \"" + letter.getMessage() + "\", " +
                name + " is offline or is invisible.";
        
        letter.setMessageType(DataTypes.MessageType.Message_Delivery_Failed);
        letter.setMessage(msg);
        letter.setRecipient(letter.getSender());
        
        Contact recipient = addressBook.Lookup(letter.getSender());
        if (recipient != null)
        {
            outbox.send(recipient, letter);
        }  
    }

    private synchronized void broadcast(Letter letter)
    {
        for (Map.Entry<String, Contact> entry : addressBook.map.entrySet()) {
            if (!entry.getValue().getIsOnline()) continue;
            
            letter.setRecipient(entry.getValue().getName());
            outbox.send(entry.getValue(), letter);
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
                    echo(letter);
                    break;
                case Message_Broadcast:
                    broadcast(letter);
                    break;
                case Message_Delivery_Successful:
                    break;
                case Message_Delivery_Failed:
                    break;
                case User_Update:
                    break;
                case User_Login:
                    String []ports = letter.getMessage().split(":");
                    if (ports.length == 3) {

                        timer.stop();
                      
                        // NOTE: the contact is added to the address book with an offline status at first. 
                        // updateStatus changes it to true so that the event listeners are called only once.
                        Contact sender = new Contact(letter.getSender(), ports[0], Integer.parseInt(ports[1]), Integer.parseInt(ports[2]), false);
                        addressBook.add(sender);
                        addressBook.updateStatus(sender.getName(), true);
                        
                        readLetter(letter);
                        
                        //inform others                        
                        broadcastClients();
                        
                        timer.restart();
                    }
                    break;
                case User_Logout:
                  
                    timer.stop();
                  
                    readLetter(letter);
                    addressBook.updateStatus(letter.getSender(), false);
                    
                    // inform others
                    broadcastClients();
                    
                    timer.restart();
                    
                    break;
                default:
                    stackLetter(letter);
                    break;
            }
        } while (true);
    }
    
    private void readLetter(Letter letter)
    {
        Contact sender = addressBook.Lookup(letter.getSender());
        if (sender == null)
        {
            System.out.println("Unknown Sender: " + letter.getMessage() );
            return;
        }
        
        
        Contact recipient = addressBook.Lookup(letter.getRecipient());
        if (recipient == null) recipient = owner;

        String msg = letter.getMessage();
        StringBuilder log = new StringBuilder();
        log.append(letter.getMessageType().toString() + ": ");
        log.append(sender.getName() + "@" + sender.getAddress() + ":" + sender.getPort());
        log.append(" --> " + recipient.getName() + "@" + recipient.getAddress() + ":" + recipient.getPort());
        log.append(" Content: " + msg);
        
        System.out.println(log.toString());
    }
    
    public synchronized void broadcastClients()
    {
/*      
        String msg;
        for (Map.Entry<String, Contact> you : addressBook.map.entrySet()) {
            if (!you.getValue().getIsOnline()) continue;
            
            for (Map.Entry<String, Contact> other : addressBook.map.entrySet()) {
//                if (you.getValue().getName().equals(other.getValue().getName())) continue;
                
                msg = other.getValue().getIsOnline() ? "1:":"0:";
                msg += other.getValue().getName();
                sendUDP(you.getValue(), msg);
            }
        } 
*/

      // send the whole userlist & online status in one packet (maximum 64KB).
      
      // make comma separated list:
      String msg = new String();
      for (Map.Entry<String, Contact> entry : addressBook.map.entrySet()) {
        if (msg.length() > 0)
          msg += ";";
        msg += entry.getValue().getName() + ":";
        msg += entry.getValue().getIsOnline() ? "1" : "0";
      }
      
      // now send to all connected clients:
      for (Map.Entry<String, Contact> entry : addressBook.map.entrySet())
        if (entry.getValue().getIsOnline())
          sendUDP(entry.getValue(), msg);
      
    }
    
    private void sendUDP(Contact receiver, String msg)
    {
        DatagramSocket socket = null;
        InetAddress hostAddress = null;
        
        try {
            socket = new DatagramSocket();
            byte[] data = msg.getBytes();
            hostAddress = InetAddress.getByName(receiver.getAddress());
            
            DatagramPacket sendDatagram = new DatagramPacket(data,
                    data.length, hostAddress, receiver.getPortUdp());
            socket.send(sendDatagram);
        } 
        catch (SocketException e) {
            System.err.println("Unable to create socket: " + e);
        } 
        catch (UnknownHostException e) {
            System.err.println("Unknown host: " + e);
        } 
        catch (IOException e) {
            System.err.println("IOException: " + e);
        }
        finally {
          socket.close();
        }
    }
    
}
