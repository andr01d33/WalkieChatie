/*
 * Outbox class takes care of opening a TCP socket to the Contact and transmits
 * the Letter. The socket is closed after the reply Letter is received.
 */
/**
 *
 * @author AndyChen
 */
package WalkieChatieLibrary;

import DataContract.Contact;
import static DataContract.DataTypes.MessageType.Message_Delivery_Successful;
import DataContract.Letter;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Outbox
{
    public Outbox()
    {
    }
    
    public Letter send(Contact receiver, Letter msg)
    {
        Socket socket = null;
        ObjectOutputStream oos = null;
        ObjectInputStream ois = null;
        Letter replyMsg = null;
        
        try {
            socket = new Socket(receiver.getAddress(), receiver.getPort());
            
            //send letter
            OutputStream memStream = new ByteArrayOutputStream();
            try (XMLEncoder encoder = new XMLEncoder(memStream)) {
                encoder.writeObject(msg);
            }
            String xmlString = memStream.toString();
            
            oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(xmlString);
            oos.flush();
            
            //receive reply letter
            ois = new ObjectInputStream(socket.getInputStream());

            String xmlStringReply = (String) ois.readObject();
            XMLDecoder decoder = new XMLDecoder(new ByteArrayInputStream(xmlStringReply.getBytes()));
            replyMsg = (Letter) decoder.readObject();
            
            
            memStream.close();
        } 
        catch (IOException | ClassNotFoundException e) 
        {
            System.err.println("Client could not make connection: " + e);
        }
        finally 
        {
            try {
                if (socket != null) 
                    socket.close();
                if (ois != null)                
                    ois.close();
                if (oos != null)
                    oos.close();
            } 
            catch (IOException e) 
            {
                System.err.println("Failed to close streams: " + e);
            }
        }
        
        return replyMsg;
    }
    
    public void sendAsync(Contact receiver, Letter msg)
    {
        NewLetter letter = new NewLetter(receiver, msg);
        Thread thread = new Thread(letter);
        thread.start();
    }
    
    private class NewLetter implements Runnable {

        private final Letter letter;
        private final Contact receiver;
        public NewLetter(Contact rec, Letter msg) {
            this.letter = msg;
            this.receiver = rec;
        }

        @Override
        public void run() {
            send(receiver, letter);
        }
    }
}

