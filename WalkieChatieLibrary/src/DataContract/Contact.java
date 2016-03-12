/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package DataContract;

/**
 *XML User data
 * @author AndyChen
 */
public class Contact {
    public Contact(){}
    public Contact(String name, String address, int port)
    {
        _name = name;
        _address = address;
        _port = port;
    }
    private String _name;
    public String getName()
    {
        return _name;
    }
    public void setName(String name)
    {
        _name  = name;
    }
    private String _address;
    public String getAddress()
    {
        return _address;
    }
    public void setAddress(String address)
    {
        _address  = address;
    }
    private int _port;
    public int getPort()
    {
        return _port;
    }
    public void setPort(int port)
    {
        _port  = port;
    }
}
