
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

    static ObjectOutputStream oos;
    static ArrayList<ClientUser> clientList;
    static LogManager logManager;
    static int port;

    Broadcaster(){}
    
    Broadcaster(ArrayList<ClientUser> c) {
        clientList=c;
    }

    Broadcaster(int _port) {
        port = _port;        
        logManager = new LogManager();
    }

    public void addCleint(InetAddress ip, String nickName) {        
        System.out.println("ADD client "+ip);
        ClientUser user = new ClientUser(ip, nickName);
        clientList.add(user);
        logManager.doLog("add " + ip.getHostAddress() + "to the Broadcast Client list");
        this.bradcastClientList();
    }
    
    public void deleteClient(String name){
        System.out.println("DELETE client "+name);
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
    
    public InetAddress findIPByName(String name){
        InetAddress rlt=null;
        for(int i=0;i<clientList.size();i++){
            ClientUser cu=clientList.get(i);
            if(cu.getUserNickName().equals(name)){
                rlt=cu.getUserIP();
            }
        }
        return rlt;
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
        System.out.println("broadcast msg:"+msg.getMsg());
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
            System.out.println("server codebook" + cbm.getContent().getCode("a"));
            cb.setEncodeMap(cbm.getContent().getEncodeMap());
            cb.setDecodeMap(cbm.getContent().getDecodeMap());            
            CodebookMessage cm = new CodebookMessage(cb);
            System.out.println("to be sent codebook" + cb.getCode("a"));
            Socket toClient = new Socket(ip, port);
            ObjectOutputStream oos = new ObjectOutputStream(toClient.getOutputStream());
            oos.writeObject(cm);
            oos.close();
            logManager.doLog("Server send codebook to" + ip + " finished.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public void broadcastCodeBook(){
        System.out.println("broadcastCodeBook()");
        Message msg=new Message(0,"System Msg:","Server Update Codebook");
        this.broadcast(msg);
        System.out.println(clientList.size());
        for (int i = 0; i < clientList.size(); i++) {
            InetAddress ip=clientList.get(i).getUserIP();
            System.out.println(ip.getHostAddress());
            try {
                this.sendCodebook(ip);               
                logManager.doLog("Server broadcast codebook to" + clientList.get(i).getUserIP().getHostAddress());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
