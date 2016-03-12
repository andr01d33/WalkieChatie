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
        
        public static MessageType parse(int x) {
            switch (x) {
                case 0:
                    return Message_Individual;
                case 1:
                    return Message_Broadcast;
                case 2:
                    return Message_Delivery_Successful;
                case 3:
                        return Message_Delivery_Failed;
                case 4:
                        return User_Update;
                case 5:
                        return User_Login;
                case 6:
                        return User_Logout;
            }
            return null;
        }
    };
}
