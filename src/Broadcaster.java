
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Ashley
 */
public class Broadcaster {

    ObjectOutputStream oos;
    static ArrayList<ClientUser> clientList;
    LogManager logManager;
    int port;

    Broadcaster() {
    }

    Broadcaster(int _port) {
        port = _port;
        clientList = new ArrayList<ClientUser>();
        logManager = new LogManager();
    }

    public void addCleint(InetAddress ip, String nickName) {
        ClientUser user = new ClientUser(ip, nickName);
        clientList.add(user);
        logManager.doLog("add " + ip.getHostAddress() + "to the Broadcast Client list");
        this.bradcastClientList();
    }
    
    public void deleteClient(String name){
        for(int i=0;i<clientList.size();i++){
            ClientUser user=clientList.get(i);
            String n=user.getUserNickName();
            if(n.equals(name)){
                clientList.remove(user);
                logManager.doLog("delete user" + name + "to the Broadcast Client list");
                break;
            }
        }
    }

    public boolean checkIsClientInList(InetAddress ip) { //check if the client already in the list or not
        boolean rlt = false;
        for (int i = 0; i < clientList.size(); i++) {
            InetAddress tmp = clientList.get(i).getUserIP();
            if (ip.getHostAddress().equals(tmp.getHostAddress())) { //check ip address
                rlt = true;
                break;
            }
        }
        return rlt;
    }

    public void bradcastClientList() {
        StringBuffer clist = new StringBuffer();
        clist.append("Not Selected");        
        for (int i = 0; i < clientList.size(); i++) {
            clist.append("," + clientList.get(i).getUserNickName());
        }
        Message clientListMsg = new Message(0, "Client List:", clist.toString());
        this.broadcast(clientListMsg);
    }

    public boolean broadcast(Message msg) {
        boolean rlt = true;
        Socket toClient;
        for (int i = 0; i < clientList.size(); i++) {
            try {
                toClient = new Socket(clientList.get(i).getUserIP(), port);
                oos = new ObjectOutputStream(toClient.getOutputStream());
                oos.writeObject(msg);
                oos.close();
                logManager.doLog("Server broadcast msg: " + msg + " to " + clientList.get(i).getUserIP().getHostAddress());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        logManager.doLog("Server broadcast msg: " + msg + " to all client finished.");
        return rlt;
    }

    public void sendToIP(InetAddress ip, Message msg) {
        Socket toClient;
        try {
            toClient = new Socket(ip, port);
            oos = new ObjectOutputStream(toClient.getOutputStream());
            oos.writeObject(msg);
            oos.close();
            logManager.doLog("Server broadcast msg: " + msg + " to " + ip.getHostAddress());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static ArrayList<ClientUser> getClientList() {
        return clientList;
    }

    public void sendCodebook(InetAddress ip) {
        try {
            CodeBookManager cbm = new CodeBookManager();
            CodeBook cb = new CodeBook();
            System.out.println("server codebook" + cbm.getContent().get("a"));
            cb.setContent(cbm.getContent().getMap());
            CodebookMessage cm = new CodebookMessage(cb);
            System.out.println("to be sent codebook" + cb.get("a"));
            Socket toClient = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(toClient.getOutputStream());
            oos.writeObject(cm);
            oos.close();
            logManager.doLog("Server send codebook to" + ip + " finished.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
