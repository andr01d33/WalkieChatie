/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walkiechatie;

import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import WalkieChatieLibrary.MailboxClient;
import java.util.Map;
import java.util.Random;


/**
 *
 * @author Administrator
 */
public class WalkieChatie implements DataTypes.MessageListener, DataTypes.UserListListener
{
    private MailboxClient _mailbox;
    private String USER_NAME = "User_";

    public WalkieChatie() {
    }

    public static void main(String[] args) {
        WalkieChatie client = new WalkieChatie();
        client.test(); 
    }
    
    private void setup()
    {
        //mailbox setup
        _mailbox = new MailboxClient(USER_NAME); 
        _mailbox.addNewMessageListener(this);
        _mailbox.addressBook.addUserListListener(this);
        _mailbox.start();
    }
    
    @Override
    public void newMessageArrived() {
        do {
            Letter letter = _mailbox.LetterQueue.poll();
            if (letter == null) {
                break;
            }
            showMessage(letter);
        } while (true);
    }
    
    //todo: connect these methods to GUI
    public void test() {
        //mailbox test
        Random rand = new Random();
        int n = rand.nextInt(5000) + 1;
        USER_NAME += Integer.toString(n);
        
        setup();
        
        _mailbox.login();
        //to a user
        _mailbox.send(USER_NAME, "This is a private message");  
        //to all
        _mailbox.sendAll("This is a broadcasting message");
        //to someone offline
        _mailbox.send("Ted", "Test sending message to offline user.");
        
        n = rand.nextInt(20) + 3;
        try {
            Thread.sleep(n*1000);
        } catch (InterruptedException ex) {}
        
        //print out user list
        System.out.println("Current Users: (including offline users)");
        for (Map.Entry<String, Contact> entry : _mailbox.addressBook.map.entrySet()) {
            String userName = entry.getValue().getName();
            boolean isOnline = entry.getValue().getIsOnline();
            System.out.println("User: " + userName + " -> " + (isOnline? "online." : "offline."));
        }
        
        _mailbox.logout();          
    }

    //todo: implementation of methods userStatusChanged, and showMessage with GUI
    @Override
    public void userStatusChanged(String userName, boolean isOnline) {
        System.out.println("User: " + userName + " is now " + (isOnline? "online." : "offline."));
    }
    
    public void showMessage(Letter letter)
    {
        DataTypes.MessageType msgType = letter.getMessageType();
        
        String type = " ";
        switch (msgType)
        {
            case Message_Individual:
                type += "said";
                break;
            case Message_Broadcast:
                type += "announced";
                break;
            case Message_Delivery_Failed:
                type += "server replyed";
                break;
        }
        String msg = letter.getSender() + type + "\t" + letter.getMessage();
        System.out.println(msg);
    }
}