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
public abstract class DataTypes {
    

    public interface MessageListener 
    {
        void newMessageArrived();
    }
    
    public static enum MessageType
    {
        Message_Individual,
        Message_Broadcast,
        Message_Delivery_Successful,
        Message_Delivery_Failed,
        User_Update,
        User_Login,
        User_Logout;
    };
}
