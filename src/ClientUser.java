
import java.net.InetAddress;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ashley
 */
public class ClientUser {
    InetAddress ip;
    String nickName;
    ClientUser(){}
    ClientUser(InetAddress _ip,String _nick){ip=_ip;nickName=_nick;}
    
    public InetAddress getUserIP(){
        return ip;
    }
    
    public String getUserNickName(){
        return nickName;
    }
    
    public void setUserNickName(String nick){
        nickName=nick;
    }   
    
}
