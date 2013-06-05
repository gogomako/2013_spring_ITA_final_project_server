
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ashley
 */
public class CodeBookManager {
    static CodeBook codebook;
    String codebookPath = "codebook.xml"; //codebook file path
    String code = "space,!,\",#,$,%,&,',(,),*,+,-,.,/,0,1,2,3,4,5,6,7,8,9,:,;,<,=,>,?,@,A,B,C,D,E,F,G,H,I,J,K,L,M,N,O,P,Q,R,S,T,U,V,W,X,Y,Z,[,\\,],^,_,`,a,b,c,d,e,f,g,h,i,j,k,l,m,n,o,p,q,r,s,t,u,v,w,x,y,z,{,|,},~";
    XMLOutputter fmt;
    CodeBookManager(){}
    CodeBookManager(CodeBook cb){
        codebook=cb;
    }
    
    public void initCodebook() { //load codebook.xml if exist.
        File cbFile = new File(codebookPath);
        if (cbFile.exists()) {
            SAXBuilder builder = new SAXBuilder(false);
            Document doc;
            try {
                doc = builder.build(codebookPath);
                Element root = doc.getRootElement();
                List wordList = root.getChildren("word");
                Iterator iter = wordList.iterator();
                while(iter.hasNext()){
                    Element word=(Element) iter.next();
                    codebook.put(word.getChildText("symbol"),word.getChildText("codeword"));
                    System.out.println("codebook put "+word.getChildText("symbol")+" in");
                }
            }  catch (Exception ex) {
                ex.printStackTrace();
            }
        }else{
            this.setDefaultCodeBook(); //if codebook not exist. create a default codebook
            this.writeCodeBookToXML();
        }
    }

    public void setDefaultCodeBook() { //set initial codeword of codebook
        String tmp[] = code.split(",");
        for (int i = 0; i < tmp.length; i++) {
            codebook.put(tmp[i], "00");
            System.out.println("[" + i + "]" + tmp[i] + "=>00");
        }
        codebook.put(",", "00");
        System.out.println("codebook size=" + codebook.size());
    }

    public void writeCodeBookToXML() { //write codebook to file
        Element root = new Element("Codebook");
        Document codeXML = new Document(root);
        Element word;
        for (Map.Entry<String, String> entry : codebook.getEntrySet()) {
            word = new Element("word");
            word.addContent(new Element("symbol").setText(entry.getKey()));
            word.addContent(new Element("codeword").setText(String.valueOf(entry.getValue())));
            root.addContent(word);
        }
        fmt = new XMLOutputter();
        fmt.setFormat(Format.getPrettyFormat());
        try {
            fmt.output(codeXML, new FileOutputStream("codebook.xml"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setCodeword(String symbol,String codeword){ //set codeword
        codebook.put(symbol,codeword);
        this.writeCodeBookToXML();
    }
    
    public String getCodeword(String s){  //get codeword
        String keyValue=s;
        if(s.equals(" "))keyValue="space";
        return codebook.get(keyValue);
    }
    
    public static CodeBook getContent(){ //get codebook hashmap
        return codebook;
    }
            
    
}
