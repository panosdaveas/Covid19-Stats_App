package GRID;

import model.Coviddata;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.swing.text.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.awt.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static FILL.Fill.num2string;

public class Grid {

    public Grid(String selectedCountry, Date[] myDates, EntityManager em){
        //Παίρνουμε το επιλεγμένο ημερομηνιακό εύρος από την κλάση Gui και
        //αντλούμε τα αντίστοιχα δεδομένα
        for(short i = 1; i <= 3; i++) {
            List<Coviddata> results;
            if(myDates[1] != null) {
                TypedQuery<Coviddata> query = em.createQuery("select cd from Coviddata cd where cd.country.name = :selectedCountry and cd.datakind = :datakind and cd.trndate between :dateFrom and :dateTo order by cd.trndate", Coviddata.class);
                query.setParameter("selectedCountry", selectedCountry);
                query.setParameter("datakind", i);
                query.setParameter("dateFrom", myDates[0]);
                query.setParameter("dateTo", myDates[1]);
                results = query.getResultList();
            }else {
                TypedQuery<Coviddata> query = em.createQuery("select cd from Coviddata cd where cd.country.name = :selectedCountry and cd.datakind = :datakind order by cd.trndate", Coviddata.class);
                query.setParameter("selectedCountry", selectedCountry);
                query.setParameter("datakind", i);
                results = query.getResultList();
            }
            try {
                //Δημιουργία των XML αρχείων και αποθήκευση στο Package XML
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();

                Element rootElement = doc.createElement(selectedCountry);
                doc.appendChild(rootElement);

                Element dataType = doc.createElement("dataType");
                rootElement.appendChild(dataType);

                Attr attr = doc.createAttribute("Type");
                String dataTypeName = num2string(i);
                attr.setValue(dataTypeName);
                dataType.setAttributeNode(attr);

                for (Coviddata cd : results) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yy");
                    String dateStr = format.format(cd.getTrndate());

                    Element date = doc.createElement("date");
                    dataType.appendChild(date);
                    Text text1 = doc.createTextNode(dateStr);
                    date.appendChild(text1);

                    Element qnty = doc.createElement("qnty");
                    date.appendChild(qnty);
                    Text text2 = doc.createTextNode(Integer.toString(cd.getQty()));
                    qnty.appendChild(text2);
                }

                TransformerFactory tf = TransformerFactory.newInstance();
                Transformer tr = tf.newTransformer();
                tr.setOutputProperty(OutputKeys.INDENT, "yes");
                tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
                DOMSource source = new DOMSource(doc);
                String fileName = dataTypeName + ".xml";
                StreamResult result = new StreamResult(new File("src/XML/" + fileName));
                tr.transform(source, result);
            } catch (ParserConfigurationException | TransformerConfigurationException e) {
                e.printStackTrace();
            } catch (TransformerException e) {
                e.printStackTrace();
            }
        }
    }

    public StyledDocument[] readXML() throws FileNotFoundException {
        //Δημιουργία του Styled Document που θα προβάλλουμε
        String[] fileName = {"confirmed", "deaths", "recovered"};
        StyledDocument[] doc = new DefaultStyledDocument[3];
        MutableAttributeSet attrs = new SimpleAttributeSet();
        for(int i = 0; i < 3; i++){
            doc[i] = new DefaultStyledDocument();
            try{
                FileReader fr = new FileReader("src/XML/" + fileName[i] + ".xml");
                BufferedReader br = new BufferedReader(fr);
                int c = 0;
                while ((c = br.read()) != -1){
                    char ch = (char) c;
                    if ((ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') ) {
                        StyleConstants.setBold(attrs, true);
                        StyleConstants.setForeground(attrs, Color.RED);
                        doc[i].insertString(doc[i].getLength(), String.valueOf(ch), attrs);
                    } else {
                        StyleConstants.setBold(attrs, false);
                        StyleConstants.setForeground(attrs, Color.BLACK);
                        doc[i].insertString(doc[i].getLength(), String.valueOf(ch), attrs);
                    }
                }
                fr.close();
                br.close();
            } catch (IOException | BadLocationException e) {
                e.printStackTrace();
            }
        }
        return doc;
    }

}
