/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walkiechatie;

import DataContract.Config;
import DataContract.DataTypes;
import WalkieChatieLibrary.Mailbox;
import DataContract.Contact;
import DataContract.Letter;

/**
 *
 * @author Administrator
 */
public class WalkieChatie implements DataTypes.MessageListener
{
    private Mailbox _mailbox;
    public static final String USER_NAME = "User 1";

    public WalkieChatie() {
    }

    public void test() {
        //mailbox setup
        _mailbox = new Mailbox(new Contact(USER_NAME, Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP+10)); 
        _mailbox.addNewMessageListener(this);
        _mailbox.start();
        
        //mailbox test
        _mailbox.login();
        //to a user
        _mailbox.send(USER_NAME, "This is a private message");
        //to all
        _mailbox.sendAll("This is a broadcasting message");
        //to someone offline
        _mailbox.send("Ted", "Test sending message to offline user.");
        _mailbox.logout();     
        
    }

    public static void main(String[] args) {
        WalkieChatie client = new WalkieChatie();
        client.test();
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
        String msg = letter.getSender().getName() + type + "\t" + letter.getMessage().getDate() + "\n" + letter.getMessage().getContent();
        System.out.println(msg);
    }
}