/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ashley
 */
import java.awt.Label;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LogManager {    
    Date date;
    SimpleDateFormat sdf;
    FileWriter fw;
    LogManager() {
        sdf = new SimpleDateFormat("yyyy/MM/dd hh:mm:ss");
        date = new Date();
    }

    public void doLog(String msg) {
        String logMsg = "[" + sdf.format(date) + "]" + "  " + msg+"\r\n";
        this.updateServerStatus(logMsg);
        this.writeToLogFile(logMsg);
    }

    public void updateServerStatus(String msg) {
        ServerScreen.serverStatus.setText(ServerScreen.serverStatus.getText() + "\n" + msg);
    }
    
    public void writeToLogFile(String msg){        
        try {
            fw=new FileWriter("log.txt",true);
            fw.write(msg);
            fw.close();
        } catch (IOException ex) {
            Logger.getLogger(LogManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}
