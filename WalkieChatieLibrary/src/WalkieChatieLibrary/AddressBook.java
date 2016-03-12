/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WalkieChatieLibrary;

import DataContract.Contact;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author AndyChen
 */
public class AddressBook {
    public Map<String, Contact> map;

    public AddressBook()
    {
        map = new HashMap<>();
        
    }
    
    public Contact Lookup(String userName)
    {
        return map.get(userName);
    }
    
    public boolean add(Contact contact)
    {
        if      (contact == null || 
                contact.getName() == null || contact.getName().isEmpty() ||
                contact.getAddress() == null || contact.getAddress().isEmpty() ||
                contact.getPort() <= 0)
            return false;
        
        map.put(contact.getName(), contact);
        return true;
    }
    public boolean remove(Contact contact)
    {
        return map.remove(contact.getName())!=null? true : false;
    }
    
    public boolean remove(String userName)
    {
        return map.remove(userName)!=null? true : false;
    }
}
