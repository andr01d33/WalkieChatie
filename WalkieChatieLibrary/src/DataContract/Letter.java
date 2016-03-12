/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContract;

/**
 *
 * @author AndyChen
 */
public class Letter {
    private int _messageType;
    private Contact _recipient;
    private Contact _sender;
    private Message _message;
    
    public Letter()
    {
        
    }
    public Letter(DataTypes.MessageType type, Contact recipient, Contact sender, Message msg)
    {
        _messageType = type.ordinal();
        _recipient = recipient;
        _sender = sender;    
        _message = msg;
    }
    
    public int getMessageType()
    {
        return _messageType;    
    }
    public void setMessageType(int messageType)
    {
        _messageType = messageType;    
    }
    public Contact getSender()
    {
        return _sender;    
    }
    public void setSender(Contact sender)
    {
        _sender = sender;    
    }
    public Contact getRecipient()
    {
        return _recipient;    
    }
    public void setRecipient(Contact recipient)
    {
        _recipient = recipient;    
    }
    public Message getMessage()
    {
        return _message;    
    }
    public void setMessage(Message message)
    {
        _message = message;    
    }
}
