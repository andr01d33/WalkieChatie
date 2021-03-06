/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package walkiechatieserver;

import DataContract.Contact;
import DataContract.DataTypes;
import DataContract.Letter;
import WalkieChatieLibrary.MailboxServer;
import java.awt.event.KeyEvent;
import java.net.InetAddress;
import java.util.*;
import javax.swing.JOptionPane;
import java.awt.datatransfer.*;
import java.awt.Toolkit;
import javax.swing.KeyStroke;
//import javax.swing.text.DefaultCaret;
//import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

/**
 *
 * @author andyc
 */
public class WalkieChatieServerGUI extends javax.swing.JFrame implements DataTypes.MessageListener, DataTypes.UserListListener{

    /**
     * Creates new form NewJFrame
     */
    public WalkieChatieServerGUI() {
        initComponents();
        
        try 
        {
          log(InetAddress.getLocalHost().getHostAddress() + ", " + InetAddress.getLocalHost().getHostName());
        }
        catch (Exception e) 
        {
          log("Couldn't get host address or name: " + e.getLocalizedMessage());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
  // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
  private void initComponents() {

    jButton1 = new javax.swing.JButton();
    tfServer = new javax.swing.JTextField();
    jLabel1 = new javax.swing.JLabel();
    jLabel2 = new javax.swing.JLabel();
    tfPort = new javax.swing.JTextField();
    btnStart = new javax.swing.JButton();
    btnStop = new javax.swing.JButton();
    jScrollPane1 = new javax.swing.JScrollPane();
    taConsole = new javax.swing.JTextArea();
    btnList = new javax.swing.JButton();
    btnBoot = new javax.swing.JButton();

    jButton1.setText("jButton1");

    setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    setTitle("WalkieChatie Server");
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowActivated(java.awt.event.WindowEvent evt) {
        formWindowActivated(evt);
      }
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
      public void windowOpened(java.awt.event.WindowEvent evt) {
        formWindowOpened(evt);
      }
    });

    tfServer.setText("localhost");
    tfServer.setToolTipText("Input you server's IP Address here.");

    jLabel1.setLabelFor(tfServer);
    jLabel1.setText("Server Address");

    jLabel2.setLabelFor(tfPort);
    jLabel2.setText("Port");

    tfPort.setText("8888");

    btnStart.setText("Start");
    btnStart.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStartActionPerformed(evt);
      }
    });

    btnStop.setText("Stop");
    btnStop.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnStopActionPerformed(evt);
      }
    });

    taConsole.setEditable(false);
    taConsole.setBackground(new java.awt.Color(51, 51, 51));
    taConsole.setColumns(20);
    taConsole.setForeground(new java.awt.Color(0, 204, 153));
    taConsole.setRows(5);
    taConsole.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        taConsoleKeyPressed(evt);
      }
    });
    jScrollPane1.setViewportView(taConsole);

    btnList.setText("List Users");
    btnList.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnListActionPerformed(evt);
      }
    });

    btnBoot.setText("Boot User...");
    btnBoot.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        btnBootActionPerformed(evt);
      }
    });

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addComponent(jScrollPane1)
        .addContainerGap())
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
          .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
            .addComponent(btnList)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(btnBoot))
          .addGroup(layout.createSequentialGroup()
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addComponent(tfServer, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)))
        .addGap(12, 12, 12)
        .addComponent(jLabel2)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(tfPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnStart, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(btnStop, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap(8, Short.MAX_VALUE))
    );
    layout.setVerticalGroup(
      layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
      .addGroup(layout.createSequentialGroup()
        .addContainerGap()
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(tfServer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(jLabel1)
          .addComponent(jLabel2)
          .addComponent(tfPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
          .addComponent(btnStart)
          .addComponent(btnStop))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
          .addComponent(btnList)
          .addComponent(btnBoot))
        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
        .addContainerGap())
    );

    pack();
  }// </editor-fold>//GEN-END:initComponents

    private MailboxServer _mailbox;
    private boolean connected = false;
    
    private void btnStartActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStartActionPerformed
        blockControls(false);
        log("Server starting...");
        
        String serverAddr = tfServer.getText();
        int serverPort = Integer.parseInt(tfPort.getText());
        
        _mailbox = new MailboxServer(serverAddr, serverPort);
        _mailbox.addNewMessageListener(this);
        _mailbox.addressBook.addUserListListener(this);
        _mailbox.start();
        connected = true;
    }//GEN-LAST:event_btnStartActionPerformed

    private void btnStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnStopActionPerformed
        
        if (connected) {
          log("Server stopping");
          _mailbox.stop();
        }
        connected = false;
        
        updateControlStates();
    }//GEN-LAST:event_btnStopActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        blockControls(false);
        if (connected)
          _mailbox.stop();
        connected = false;
    }//GEN-LAST:event_formWindowClosing

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        
    }//GEN-LAST:event_formWindowActivated

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        updateControlStates();
//        DefaultCaret caret = (DefaultCaret) taConsole.getCaret();
//        caret.setUpdatePolicy(ALWAYS_UPDATE);
        log("Click \"Start\" to start the server.");
    }//GEN-LAST:event_formWindowOpened

  private void btnListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnListActionPerformed
    for (Map.Entry<String, Contact> entry : _mailbox.addressBook.map.entrySet()) {
      Contact info = entry.getValue();
      log(entry.getKey() + " from " + info.getAddress() + " is " + (info.getIsOnline() ? "online" : "offline"));
    }

  }//GEN-LAST:event_btnListActionPerformed

  private void btnBootActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBootActionPerformed
    Vector<String> list = new Vector<String>();
    for (Map.Entry<String, Contact> entry : _mailbox.addressBook.map.entrySet())
      if (entry.getValue().getIsOnline())
        list.add(entry.getValue().getName());
    if (list.size() > 0) {
      Collections.sort(list);
      String bootUser = JOptionPane.showInputDialog(this, null, "Boot Box", JOptionPane.PLAIN_MESSAGE, null, list.toArray(), null).toString();
      if (bootUser != null) {
        _mailbox.send(_mailbox.addressBook.Lookup(bootUser), 
                new Letter(DataTypes.MessageType.User_Logout, bootUser, "Server", "You have been booted."));
      }
    }
  }//GEN-LAST:event_btnBootActionPerformed

  private void taConsoleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taConsoleKeyPressed

    if (evt.getKeyCode() == KeyEvent.VK_C && evt.isControlDown()) {
      StringSelection stringSelection = new StringSelection(taConsole.getText());
      Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
      clpbrd.setContents(stringSelection, null);
    }
    
  }//GEN-LAST:event_taConsoleKeyPressed

    public void updateControlStates() {
        btnStart.setEnabled(!connected);
        btnStop.setEnabled(connected);
        tfServer.setEnabled(!connected);
        tfPort.setEnabled(!connected);
        btnList.setEnabled(connected);
        btnBoot.setEnabled(connected);
    } // updateControlStates
    
    public void blockControls(boolean enabled) {
        btnStart.setEnabled(enabled);
        btnStop.setEnabled(enabled);
        tfServer.setEnabled(enabled);
        tfPort.setEnabled(enabled);
        btnList.setEnabled(enabled);
        btnBoot.setEnabled(enabled);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(WalkieChatieServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(WalkieChatieServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(WalkieChatieServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(WalkieChatieServerGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new WalkieChatieServerGUI().setVisible(true);
            }
        });
    }

  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JButton btnBoot;
  private javax.swing.JButton btnList;
  private javax.swing.JButton btnStart;
  private javax.swing.JButton btnStop;
  private javax.swing.JButton jButton1;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel2;
  private javax.swing.JScrollPane jScrollPane1;
  private javax.swing.JTextArea taConsole;
  private javax.swing.JTextField tfPort;
  private javax.swing.JTextField tfServer;
  // End of variables declaration//GEN-END:variables

    @Override
    public void newMessageArrived() {
        Letter letter;
        while ((letter = _mailbox.LetterQueue.poll()) != null) {
            if (null != letter.getMessageType())
            switch (letter.getMessageType()) {
                case Server_Started:
                    log("Server started: " + letter.getMessage());
                    connected = true;
                    break;
                case Server_Failed:
                    log("Server failed to start: " + letter.getMessage());
                    connected = false;
                    break;
                case Server_Stopped:
                    log("Server stopped: " + letter.getMessage());
                    connected = false;
                    break;
                default:
                    break;
            }
        }
        updateControlStates();
    }

    @Override
    public void userStatusChanged(String userName, boolean isOnline) {
        log(userName + " is now " + (isOnline? "online." : "offline."));
    }
    
    private void log(String str)
    {
        taConsole.append(str + "\n");
        taConsole.setCaretPosition(taConsole.getDocument().getLength());
    }
}
