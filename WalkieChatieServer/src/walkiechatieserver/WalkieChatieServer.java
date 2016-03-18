/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walkiechatieserver;

import DataContract.Config;
import DataContract.Contact;
import DataContract.Letter;
import WalkieChatieLibrary.*;
import DataContract.DataTypes.MessageListener;

/**
 *
 * @author Administrator
 */
public class WalkieChatieServer implements MessageListener
{
    private MailboxServer _mailbox;

    public WalkieChatieServer() {
    }

    public void startServer() {
        _mailbox = new MailboxServer(new Contact(Config.SERVER_NAME, Config.SERVER_ADDRESS, Config.SERVER_PORT_TCP));
        _mailbox.addNewMessageListener(this);
        _mailbox.start();
    }

    public static void main(String[] args) {
        WalkieChatieServer server = new WalkieChatieServer();
        server.startServer();
    }

    @Override
    public void newMessageArrived() {
        /*
        do {
            Letter msg = _mailbox.inbox.MessageQueue.poll();
            if (msg == null) {
                break;
            }

            System.out.println("New Message: " + msg.getMessage().getContent());

            
            Message msgTest = new Message(
                    "localhost", 8889, DataContract.MessageType.Message_Individual,
                    "Reply from Server", "", ""
            );

            _mailbox.outbox.send(msgTest);

        } while (true);
*/
    }
}
