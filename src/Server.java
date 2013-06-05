
import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
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
public class Server extends Thread implements java.io.Serializable{

    ServerSocket serverSocket;
    Socket receiveClient;
    ObjectInputStream ois;
    Broadcaster broadcaster;
    LogManager logManager;
    int listeningPort = 5555; //listening port
    int sendingPort = 4444; //broadcasting port

    Server() {
    }

    public void run() {  //run server 
        logManager = new LogManager();
        broadcaster = new Broadcaster(4444);
        try {
            //server start listening on port 5555
            serverSocket = new ServerSocket(listeningPort);
            serverSocket.setReuseAddress(true);
            logManager.doLog("Server is runing! Listening port:" + listeningPort + ", Sending port:" + sendingPort);
            while (true) {
                receiveClient = serverSocket.accept();
                logManager.doLog("Received socket from " + receiveClient.getInetAddress() + " now");
                ois = new ObjectInputStream(receiveClient.getInputStream());
                Message msg = (Message)ois.readObject();               
                ois.close();
                this.checkMsg(msg,receiveClient);
                //broadcaster.addCleint(receiveClient.getInetAddress(),"User");
                //broadcaster.broadcast(msg);
            }
        } catch (Exception ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void checkMsg(Message msg,Socket client) {
        int type=msg.getType();
        String senderNickName=msg.getNickName();
        String text=msg.getMsg();
        InetAddress ip=client.getInetAddress();
        if(!broadcaster.checkIsClientInList(ip)&&(senderNickName!=null)){            
            broadcaster.addCleint(ip, senderNickName);
        }        
        switch(type) {
            case 0: //system msg case
                if(text.equals("request for codebook")){
                    broadcaster.sendCodebook(ip); 
                    Message welcomeMsg=new Message(0,"System Msg:","Welcome User \" "+senderNickName+" \" Join Our Chat Room");
                    broadcaster.broadcast(welcomeMsg);                    
                }else if(text.equals("request for client list")){
                    StringBuffer clist=new StringBuffer();
                    clist.append("Not Selected");
                    ArrayList<ClientUser> clients=broadcaster.getClientList();
                    for(int i=0;i<clients.size();i++){
                        clist.append(","+clients.get(i).getUserNickName());
                    }
                    Message clientListMsg=new Message(0,"Client List:",clist.toString());
                    broadcaster.sendToIP(ip, clientListMsg);
                    System.out.println("send client list"+clist.toString());
                }else if(text.equals("User Leaving")){
                    broadcaster.deleteClient(senderNickName);
                    Message leaveMsg=new Message(0,"System Msg:","User \" "+senderNickName+" \" Leave Our Chat Room");
                    broadcaster.broadcast(leaveMsg);
                    broadcaster.bradcastClientList();
                }                              
                break;
            case 1: //public msg
                logManager.doLog("Received msg from " + receiveClient.getInetAddress());
                broadcaster.broadcast(msg);
                break;
            case 2: //private msg
                
                break;           
            
        }
                
                
    }
}
