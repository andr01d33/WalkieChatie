/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContract;

import java.util.Date;

/**
 *
 * @author AndyChen
 */
public class Message {
    public Message(){}
    public Message(String content)
    {
        _content = content;
        _date = new Date();
    }
    public Message(String content, Date date)
    {
        _content = content;
        _date = date;
    }
    
    private Date _date;
    private String _content;
    
    public String getContent()
    {
        return _content;
    }
    public void setContent(String content)
    {
        _content  = content;
    }
    
    public Date getDate()
    {
        return _date;
    }
    public void setDate(Date date)
    {
        _date  = date;
    }
}