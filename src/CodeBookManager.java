
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * @author Ashley,Mako
 */
public class CodeBookManager implements java.io.Serializable {

    private static final long serialVersionUID = 1L;
    static CodeBook codebook;
    static String codebookPath = "codebook.xml"; //codebook file path    
    static XMLOutputter fmt;
    static WordFreqManager wfm;
    static Broadcaster broadcaster;

    CodeBookManager() {
    }

    CodeBookManager(CodeBook cb) {
        codebook = cb;
        wfm = new WordFreqManager();
    }

    public void initCodebook() { //load codebook.xml if exist.
        System.out.println("initCodebook()");
        File cbFile = new File(codebookPath);
        if (cbFile.exists()) {
            this.readCodeBookFromXML();
            wfm.readWordFreqFromXML();
        } else {
            this.setDefaultCodeBook(); //if codebook not exist. create a default codebook
            wfm.writeWordFreqToXML();
            this.writeCodeBookToXML();
        }
    }

    public void readCodeBookFromXML() {
        SAXBuilder builder = new SAXBuilder(false);
        Document doc;
        try {
            doc = builder.build(codebookPath);
            Element root = doc.getRootElement();
            List wordList = root.getChildren("word");
            Iterator iter = wordList.iterator();
            int a = 0;
            while (iter.hasNext()) {
                Element word = (Element) iter.next();
                //read into codebook
                String symbol = word.getChildText("symbol");
                String codeword = word.getChildText("codeword");
                if (symbol.equals("space")) {
                    symbol = " ";
                }
                codebook.put(symbol, codeword);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDefaultCodeBook() { //set initial codeword of codebook
        System.out.println("setDefaultCodeBook()");
        wfm.setDefaultFreqency();
        this.doHauffMan();
    }

    public void writeCodeBookToXML() { //write codebook to file
        Element root = new Element("Codebook");
        Document codeXML = new Document(root);
        Element word;
        for (Map.Entry<String, String> entry : codebook.getEntrySet()) {
            String key = entry.getKey();
            word = new Element("word");
            String sym = entry.getKey();
            if (sym.equals(" ")) {
                sym = "space";
            }
            word.addContent(new Element("symbol").setText(sym));
            word.addContent(new Element("codeword").setText(String.valueOf(entry.getValue())));
            root.addContent(word);
        }
        fmt = new XMLOutputter();
        fmt.setFormat(Format.getPrettyFormat());
        try {
            fmt.output(codeXML, new FileOutputStream("codebook.xml"));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO error handling
        }
    }

    public void setCodeword(String symbol, String codeword) { //set codeword
        codebook.put(symbol, codeword);
        this.writeCodeBookToXML();
    }

    public String getCodeword(String s) {  //get codeword
        String keyValue = s;
        return codebook.getCode(keyValue);
    }

    public String getWord(String s) {
        return codebook.getWord(s);
    }

    public static CodeBook getContent() { //get codebook hashmap
        return codebook;
    }

    public boolean containsCodeword(String word) {
        String keyValues = word;
        if (codebook.encodeCodebookContainsKey(keyValues)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean containsWord(String codeword) {
        if (codebook.decodeCodebookContainsKey(codeword)) {
            return true;
        } else {
            return false;
        }
    }

    public void updateCodeBook() {
        this.doHauffMan();
        this.writeCodeBookToXML();
        wfm.writeWordFreqToXML();
        ServerScreen.updateWordCountArea();
        broadcaster = new Broadcaster();
        broadcaster.broadcastCodeBook();
    }

    public boolean doHauffMan() {  //entrance
        codebook.cleanCoodbook();
        System.out.println("doHauffMan()");
        //count frequency        
        ArrayList<Node> newTree = new ArrayList<Node>(); //new Tree
        newTree = wfm.setNodeFreqency(newTree); // set every node's freqency 
        newTree = wfm.sortFrequency(newTree); //sort nodes base on frequency
        Node root = buildTree(newTree); //build tree
        // construct code
        if (!root.getWord().equals("-1") && root.left == null && root.right == null) {
            //only one node case
            root.setCode("0");
        } else {
            root = constructCode(root);
        }
        //generate codebook 
        this.setCodeBook(root);
        return true;
    }

    public void setCodeBook(Node root) {
        if (root != null) {
            if (!root.getWord().equals("-5")) {
                System.out.println("codebook put " + root.getWord() + "," + root.getCode());
                codebook.put(root.getWord(), root.getCode());
            }
            setCodeBook(root.left);
            setCodeBook(root.right);
        }
    }

    private Node buildTree(ArrayList<Node> nodeList) {
        Node internalNode;
        while (nodeList.size() > 1) {
            nodeList = wfm.sortFrequency(nodeList);
            internalNode = new Node();
            internalNode.setWord("-5");//indicate internal node            
            internalNode.setRight(nodeList.get(0));
            nodeList.remove(0); 
            internalNode.setLeft(nodeList.get(0));
            nodeList.remove(0);
            internalNode.setFrequency(internalNode.getLeft().getFrequency() + internalNode.getRight().getFrequency());
            nodeList.add(internalNode);
        }
        return nodeList.get(0);
    }

    private Node constructCode(Node root) {     //fill in 0/1
        if (root.getWord().equals("-5")) {
            root.getLeft().setCode(root.getCode() + "0");
            root.setLeft(constructCode(root.getLeft()));
            root.getRight().setCode(root.getCode() + "1");
            root.setRight(constructCode(root.getRight()));
        }
        return root;
    }
}
