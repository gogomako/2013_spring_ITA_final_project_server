
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
public class WordFreqManager {

    static HashMap<String, Integer> wordFreq;
    static XMLOutputter outputer;
    static String filepath = "word count.xml";

    WordFreqManager() {
    }

    public void initWordFreq() {
        //todo
    }

    public void writeWordFreqToXML() {
        //todo
        Element root = new Element("WordCount");
        Document codeXML = new Document(root);
        Element word;
        for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
            String key = entry.getKey();
            word = new Element("word");
            String sym = entry.getKey();
            if (sym.equals(" ")) {
                sym = "space";
            }
            word.addContent(new Element("symbol").setText(sym));
            word.addContent(new Element("times").setText(String.valueOf(entry.getValue())));
            root.addContent(word);
        }
        outputer = new XMLOutputter();
        outputer.setFormat(Format.getPrettyFormat());
        try {
            outputer.output(codeXML, new FileOutputStream(filepath));
        } catch (Exception e) {
            e.printStackTrace();
            //TODO error handling
        }
    }

    public void readWordFreqFromXML() {
        //todo
        //this.setDefaultFreqency();  //temperary
        wordFreq = new HashMap<String, Integer>();
        SAXBuilder builder = new SAXBuilder(false);
        Document doc;
        try {
            doc = builder.build(filepath);
            Element root = doc.getRootElement();
            List wordList = root.getChildren("word");
            Iterator iter = wordList.iterator();
            int a = 0;
            while (iter.hasNext()) {
                Element word = (Element) iter.next();
                //read into codebook
                String symbol = word.getChildText("symbol");
                int times = Integer.parseInt(word.getChildText("times"));
                if (symbol.equals("space")) {
                    symbol = " ";
                }
                wordFreq.put(symbol, times);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDefaultFreqency() {
        wordFreq = new HashMap<String, Integer>();
        System.out.println("setDefaultFreqency()");
        for (int i = 32; i < 127; i++) {
            String word = new Character((char) i).toString();
            System.out.println("put " + word + " 1");
            wordFreq.put(word, 1);
        }
        for (int i = 0; i < 1; i++) {
            this.countFrequency("aaaaaaaaabbcccdddddeeeeeeeeeeeeefffggghhhhhhhiiiiiiijklllllmmmnnnnnnnooooooooppqrrrrrrsssssssttttttttttuuuvwwwxyyz");
        }
        wordFreq.put("end", 100);
        this.writeWordFreqToXML();
    }

    public void setWordFrequency(String word, int freq) {
        wordFreq.put(word, freq);
    }

    public int getFrquency(String word) {
        System.out.println("try to get " + word);
        if (wordFreq != null) {
            return wordFreq.get(word);
        }
        return 0;
    }

    public HashMap<String, Integer> getFrquency() {
        return wordFreq;
    }

    public void countFrequency(String inputString) {
        System.out.println("count " + inputString);
        for (int i = 0; i < inputString.length(); i++) {
            Character c = inputString.charAt(i);
            String word = c.toString();
            int times = wordFreq.get(word);
            wordFreq.put(word, times + 1);
        }
        ServerScreen.updateWordCountArea();
    }

    public ArrayList<Node> setNodeFreqency(ArrayList<Node> nodes) {
        if (nodes != null) {
            for (Map.Entry<String, Integer> entry : wordFreq.entrySet()) {
                Node n = new Node();
                n.setWord(entry.getKey());
                n.setFrequency(entry.getValue());
                nodes.add(n);
            }
        }
        return nodes;
    }

    public ArrayList<Node> sortFrequency(ArrayList<Node> nodeFrequency) {
        ArrayList<Node> sortedNodeFrequency = nodeFrequency;
        // customize sorting function
        Collections.sort(sortedNodeFrequency, new Comparator<Node>() {
            @Override
            public int compare(Node arg0, Node arg1) {
                // increase
                return arg0.getFrequency() - arg1.getFrequency();
                // decrease
                // return arg1.getFrequency()-arg0.getFrequency();
            }
        });
        return sortedNodeFrequency;
    }
}
