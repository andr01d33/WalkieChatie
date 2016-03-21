/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walkiechatieserver;

import WalkieChatieLibrary.MailboxServer;

/**
 *
 * @author Administrator
 */
public class WalkieChatieServer
{
    private MailboxServer _mailbox;

    public WalkieChatieServer() {
    }

    public void startServer() {
        _mailbox = new MailboxServer();
        _mailbox.start();
    }

    public static void main(String[] args) {
        WalkieChatieServer server = new WalkieChatieServer();
        server.startServer();
    }
}
