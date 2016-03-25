/*
 * MailboxClient is a Mailbox subclass designed to be instantiated by the chat
 * client. It uses its Outbox & Inbox objects to send & receive Letters
 * to & from the chat server (via TCP), and its AddressBook object to maintain a 
 * list of users currently online (obtained via UDP). The innerclass ClientWatch 
 * takes care of listening for UDP datagrams sent by the server's MailboxServer 
 * object and updates the addressbook with the server's up-to-date user list.
 */
package WalkieChatieLibrary;

import DataContract.Config;
import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Arrays;

/**
 *
 * @author Andy
 */
public class MailboxClient extends Mailbox {
  
    public boolean firstContact;
  
    public MailboxClient(String userName, String serverAddr, int serverPort) {
        super(new Contact(userName, Config.SERVER_ADDRESS, 0, 0, true));
        
        server = new Contact("Server", serverAddr, serverPort, 0, true);
    }
    
    public final Contact server;
    
    private ClientWatcher _clientWatcher;
    
    // client login
    public Letter login()
    { 
        //this will get a dynamic port TCP for inbox
        super.start();
        //set client's port (dynamically assigned by the system)
        owner.setPort(inbox.Port); 
        
        
        // set to false after first UDP packet received;
        // this is so we know when we have received the first full list of 
        // client statuses.
        firstContact = true; 
        
        
        //this starts receiving user list, this will get a dynamic UDP port for updating user list
        startClientWatch();
        
        Letter returnLetter = updateClientStatus(DataTypes.MessageType.User_Login);
        if (returnLetter == null || returnLetter.getMessageType() != DataTypes.MessageType.Message_Delivery_Successful) {
            //login failed?
            super.stop();
            stopClientWatch();
        }
        
        return returnLetter;
    }
    
    public boolean logout()
    {
        super.stop();
        stopClientWatch();
        return updateClientStatus(DataTypes.MessageType.User_Logout) != null;
    }
    
    protected Letter updateClientStatus(DataTypes.MessageType type)
    {
        Letter letter = new Letter(
                type,
                server.getName(),
                owner.getName(),
                Integer.toString(owner.getPort())
                        + ":" + Integer.toString(owner.getPortUdp())
        );
        
        return outbox.send(server,letter);
    }
    
    public boolean send(String recipient, String msg)
    {
        if (recipient.equals(owner))
          return false;
      
        Letter letter = new Letter(
                DataTypes.MessageType.Message_Individual,
                recipient,
                owner.getName(),
                msg
        );
        
        outbox.sendAsync(server, letter);
        //return outbox.sendletter);
        return true;
    }
    
    public void sendAll(String msg)
    {
        Letter letter = new Letter(
                DataTypes.MessageType.Message_Broadcast,
                server.getName(),
                owner.getName(),
                msg
        );
        
        outbox.sendAsync(server, letter);
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
                    stackLetter(letter);
                    break;
                case Message_Broadcast:
                    stackLetter(letter);
                    break;
                case Message_Delivery_Successful:
                    break;
                case Message_Delivery_Failed:
                    stackLetter(letter);
                    break;
                case User_Update:
                    break;                  
                case User_Logout:
                    if (letter.getRecipient().equalsIgnoreCase(owner.getName())) {
                      stackLetter(letter);
                    }
                    break;
            }
        } while (true);
    }
    
    // Use UDP to refresh online client list
    private void startClientWatch()
    {
        _clientWatcher = new ClientWatcher();
        Thread thread = new Thread(_clientWatcher);
        thread.start();
    }
    
    private void stopClientWatch()
    {
        if (_clientWatcher!=null)_clientWatcher.stopService();
    }
    
    private class ClientWatcher implements Runnable {
        private boolean bKeepRunning = true;
        private DatagramSocket socket = null;
        private DatagramPacket receiveDatagram = null;
        
        public ClientWatcher() {
            
            try {
                socket = new DatagramSocket();
                socket.setSoTimeout(Config.PORT_UDP_TIMEOUT);
                owner.setPortUdp(socket.getLocalPort());
            } catch (SocketException e) {
                System.err.println("Unable to create socket: " + e);
            } 
        }

        public void stopService()
        {
            bKeepRunning = false;
        }
        
        @Override
        public void run() {
            
            byte[] buffer = new byte[65535];
            String msg;
            boolean isOnline = false;
            
            try {
                receiveDatagram = new DatagramPacket(buffer, buffer.length);
                
                while (bKeepRunning) {
                  
                    // clear buffer before receiving each new datagram
                    Arrays.fill(buffer, (byte)0); 
                  
                    try {
                        socket.receive(receiveDatagram);
                    }
                    catch (SocketTimeoutException e) 
                    {
                        continue;
                    }
                    
                    msg = new String(receiveDatagram.getData()).trim();
                    
                    String[] users = msg.split(";");
                    for (int i = 0; i < users.length; i++) {
                      String[] parts = users[i].split(":");
                      if (parts.length > 1) {
                        isOnline = parts[1].equals("1");
                        addressBook.updateStatus(parts[0], isOnline);
                      }
                    }
                    
                    firstContact = false; 
                    
/*                    
                    String[] strs = strMsg.split(":");
                    if (strs.length >= 2) {
                        isOnline = strs[0].equals("1");
                        
                        addressBook.updateStatus(strs[1], isOnline);
                    } 
*/
                }
                
                // clean up
                socket.close();
                
            } 
            catch (SocketException e) 
            {
                System.err.println("Unable to create socket: " + e);
            }
            catch (IOException ex) 
            {
                System.err.println("IO Exception: " + ex);
            } 
        } //run()
        
    } // ClientWatcher
    
} // MailboxClient
