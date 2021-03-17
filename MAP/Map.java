package MAP;

import model.Country;
import okhttp3.OkHttpClient;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Map {
    public Map(List<String> countryNames, JPanel myPanel, Date[] myDates) throws IOException {

        EntityManager em;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("3hGE_lib_testPU");
        em = emf.createEntityManager();

        StringBuilder sbFinal = new StringBuilder();

        for (String name : countryNames) {
            //Ανάκτηση των δεδομένων για τις επιλεγμένες χώρες
            //στο επιλεγμένο εύρος
            //Αν δεν δοθεί εύρος στις ημερομηνίες, τότε
            //ανακτούμε τα συνολικά δεδομένα
            TypedQuery<Country> query = em.createQuery("select c from Country c where c.name =:selectedCountry", Country.class);
            query.setParameter("selectedCountry", name);
            Country myCountry = query.getSingleResult();

            List<Integer> result = new ArrayList<>();
            for (short i = 1; i <= 3; i++) {
                if(myDates[0] == null) {
                    TypedQuery<Integer> max = (TypedQuery<Integer>) em.createQuery("select max(cd.proodqty) from Coviddata cd where cd.datakind =:datakind and cd.country.name =:selectedCountry");
                    max.setParameter("selectedCountry", myCountry.getName());
                    max.setParameter("datakind", i);
                    result.add(max.getSingleResult());
                }else{
                    TypedQuery<Integer> max = (TypedQuery<Integer>) em.createQuery("select max(cd.proodqty) from Coviddata cd where cd.datakind =:datakind and cd.country.name =:selectedCountry and cd.trndate between :dateFrom and :dateTo");
                    max.setParameter("selectedCountry", myCountry.getName());
                    max.setParameter("datakind", i);
                    max.setParameter("dateFrom", myDates[0]);
                    max.setParameter("dateTo", myDates[1]);
                    Integer resultMax = max.getSingleResult();
                    TypedQuery<Integer> min = (TypedQuery<Integer>) em.createQuery("select min(cd.proodqty) from Coviddata cd where cd.datakind =:datakind and cd.country.name =:selectedCountry and cd.trndate between :dateFrom and :dateTo");
                    min.setParameter("selectedCountry", myCountry.getName());
                    min.setParameter("datakind", i);
                    min.setParameter("dateFrom", myDates[0]);
                    min.setParameter("dateTo", myDates[1]);
                    Integer resultMin = min.getSingleResult();
                    result.add((resultMax - resultMin));
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("['" + name + ", confirmed = " + result.get(0) + ", deaths = " + result.get(1) + ", recovered = " + result.get(2) + "', " + myCountry.getLat() + ", " + myCountry.getLong1() + ", 1], ");
            sbFinal.append(sb);
        }

        String content = "";
        try {
            FileReader fr = new FileReader("src/MAP/mappage.html");
            BufferedReader br = new BufferedReader(fr);
            String line = null;
            while ((line = br.readLine()) != null) {
                int i = 0;
                while (line.charAt(i) == 32) {
                    i++;
                }
                if (line.charAt(i++) == '[') {
                    line = "    " + sbFinal.toString();
                }
                content = content + line + "\n";
            }
            fr.close();
            br.close();
        } catch (
                IOException e) {
            e.printStackTrace();
        }

        FileWriter writer = new FileWriter("src/MAP/mappage.html");
        writer.write(content);
        writer.close();

        String webpage = "src/MAP/mappage.html";
        try {
            //Έλεγχος λειτουργικού συστήματος
            String OS = System.getProperty("os.name").toLowerCase();
            if(OS.indexOf("win") >= 0){
                //System.out.println("Windows");
                Runtime.getRuntime().exec("cmd /c start "+ webpage);
            }else if(OS.indexOf("mac") >= 0){
                //System.out.println("macOS");
                Runtime.getRuntime().exec("open " + webpage);
            }
            else if(OS.indexOf("nux") >= 0){
                //System.out.println("Linux");
                Runtime.getRuntime().exec("xdg-open " + webpage);
            }
        } catch (IOException ex) {
            Logger.getLogger(myPanel.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
