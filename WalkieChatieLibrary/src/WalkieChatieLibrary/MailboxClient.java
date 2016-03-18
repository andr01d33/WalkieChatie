/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WalkieChatieLibrary;

import DataContract.Config;
import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import DataContract.Message;

/**
 *
 * @author Andy
 */
public class MailboxClient extends Mailbox{
    
    public MailboxClient(Contact ownerInfo) {
        super(ownerInfo);
    }
    
        //client login
    public boolean login()
    {
        onwer.setPort(inbox.Port);
        return updateClientStatus(DataTypes.MessageType.User_Login);
    }
    
    public boolean logout()
    {
        inbox.stopService();
        return updateClientStatus(DataTypes.MessageType.User_Logout);
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
    
    public void sendAll(String msg)
    {
        Letter letter = new Letter(
                DataTypes.MessageType.Message_Broadcast,
                new Contact(Config.SERVER_NAME, Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP),
                onwer,
                new Message(msg)
        );
        
        outbox.send(letter);
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
            }
        } while (true);
    }
}
