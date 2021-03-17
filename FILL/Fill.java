package FILL;

import com.google.gson.*;
import model.Country;
import model.Coviddata;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Fill {
    //Η κλάση Fill είναι υπεύθυνη για την κλήση του api και την
    //εγγραφή των δεδομένων στην ΒΔ

    public static void responseStr(String requestString, String timeseriesCase){
        //Γράφουμε τα αρχεία που παίρνουμε σε ένα δικό μας
        //φάκελο στο project (Package JSON)
        //το όρισμα timeseries αναφέρεται στις επιλογές
        //confirmed, deaths, recovered
        String responseString = null;
        String urlToCall = requestString;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(urlToCall).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                responseString = response.body().string();
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                JsonParser jp = new JsonParser();
                JsonElement je = jp.parse(responseString);
                responseString = gson.toJson(je);
                try {
                    StringBuilder fileName = new StringBuilder();
                    fileName.append("src/JSON/" + timeseriesCase + ".json");
                    FileWriter file = new FileWriter(fileName.toString());
                    file.write(responseString);
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void selectType (Short dataKind) throws FileNotFoundException, ParseException {
        //Η μέθοδος που καλούμε για να γεμίσουμε τον πίνακα Countries
        String[] requestName = {"confirmed", "deaths", "recovered"};
        if (dataKind == 1) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/confirmed", "confirmed");
            populateCountryTable(requestName[0], dataKind);
        } else if (dataKind == 2) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/deaths", "deaths");
            populateCountryTable(requestName[1], dataKind);
        } else if (dataKind == 3) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/recovered", "recovered");
            populateCountryTable(requestName[2], dataKind);
        }
    }

    public static void selectDataType (Short dataKind) throws FileNotFoundException, ParseException {
        //Η μέθοδος που καλούμε για να γεμίσουμε τον πίνακα CovidData
        String[] requestName = {"confirmed", "deaths", "recovered"};
        if (dataKind == 1) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/confirmed", "confirmed");
            populateCovidDataTable(requestName[0], dataKind);
        } else if (dataKind == 2) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/deaths", "deaths");
            populateCovidDataTable(requestName[1], dataKind);
        } else if (dataKind == 3) {
            responseStr("https://covid2019-api.herokuapp.com/timeseries/recovered", "recovered");
            populateCovidDataTable(requestName[2], dataKind);
        }
    }

    public static void populateCountryTable(String requestType, Short dataKind) throws FileNotFoundException, ParseException {
        //Η μέθοδος που γράφει τα δεδομένα στον πίνακα Country
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = (JsonObject) jp.parse(new FileReader("src/JSON/" + requestType + ".json"));
        JsonArray jsonArray = (JsonArray) jsonObject.get(requestType);

        EntityManager em;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("3hGE_lib_testPU");
        em = emf.createEntityManager();

        List<Country> myCountries = new ArrayList<>();
        Integer id = 1;

        for (Object obj : jsonArray) {

            JsonObject record = (JsonObject) obj;

            JsonElement State = record.get("Province/State");
            JsonElement Name = record.get("Country/Region");
            JsonElement Lat = record.get("Lat");
            JsonElement Long = record.get("Long");

            Country myCountry = new Country();
            List<Coviddata> mydataList = new ArrayList<>();
            myCountry.setCountry(id);
            myCountry.setName(Name.getAsString());
            //Ελέγχουμε για null εγγραφές στα πεδία
            if (Lat.getAsString().isEmpty()) {
                myCountry.setLat((Double) null);
            } else {
                myCountry.setLat(Lat.getAsDouble());
            }
            if (Long.getAsString().isEmpty()) {
                myCountry.setLong1((Double) null);
            } else {
                myCountry.setLong1(Long.getAsDouble());
            }

            myCountry.setCoviddataList(mydataList);

            myCountries.add(myCountry);

            id++;
        }
        //Σύμπτυξη των χωρών που έχουν ίδιο Name
        //αλλά διαφορετικό State
        for (int i = myCountries.size() - 1; i > 0; i--) {
            while (myCountries.get(i).getName().equals(myCountries.get(i - 1).getName())) {
                myCountries.remove(i - 1);
                i--;
            }
        }
        //Εγγραφή στον πίνακα
        //Έλεγχος εάν η τρέχουσα χώρα υπάρχει ήδη στον πίνακά μας
        em.getTransaction().begin();
        for (int i = 0; i < myCountries.size(); i++) {
            TypedQuery<String> query = (TypedQuery<String>) em.createQuery("select c.name from Country c where c.name =:currentCountryName").setMaxResults(1);
            query.setParameter("currentCountryName", myCountries.get(i).getName());
            if(query.getResultList().isEmpty()) {
                em.persist(myCountries.get(i));
            }
        }
        em.getTransaction().commit();
    }

    public static void populateCovidDataTable(String requestType, Short dataKind) throws FileNotFoundException, ParseException {
        //Η μέθοδος που γράφει τα δεδομένα στον πίνακα CovidData
        //Θέλουμε η εγγραφή του πίνακα CovidData να γίνεται ανεξάρτητα
        //από την εγγραφή του πίνακα Country
        //γι αυτό η μέθοδος αυτή είναι ανεξάρτητα από την προηγούμενη
        //παρόλο που υπάρχουν αρκετά κοινά κομμάτια κώδικα
        JsonParser jp = new JsonParser();
        JsonObject jsonObject = (JsonObject) jp.parse(new FileReader("src/JSON/" + requestType + ".json"));
        JsonArray jsonArray = (JsonArray) jsonObject.get(requestType);

        EntityManager em;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("3hGE_lib_testPU");
        em = emf.createEntityManager();

        TypedQuery<Country> query = em.createQuery("select c from Country c", Country.class);
        List<Country> results = query.getResultList();

        List<Country> myCountries = new ArrayList<>();
        Integer id = 1;

        for (Object obj : jsonArray) {

            JsonObject record = (JsonObject) obj;

            JsonElement Name = record.get("Country/Region");

            Country myCountry = new Country();
            List<Coviddata> mydataList = new ArrayList<>();
            myCountry.setCountry(id);
            myCountry.setName(Name.getAsString());
            //Για κάθε χώρα γεμίζουμε το CoviddataList
            fillCovidDataList(record, myCountry, dataKind, mydataList);
            myCountry.setCoviddataList(mydataList);
            myCountries.add(myCountry);
            id++;
        }
        //Σύμπτυξη των χωρών με ίδιο Name
        //αλλά διαφορετικό State
        //Σύμπτυξη των αντίστοιχων CoviddataLists
        //και άθροισμα των δεδομένων
        for (int i = myCountries.size() - 1; i > 0; i--) {
            while (myCountries.get(i).getName().equals(myCountries.get(i - 1).getName())) {
                for (int j = 0; j < myCountries.get(i).getCoviddataList().size(); j++) {
                    myCountries.get(i).getCoviddataList().get(j).setProodqty(myCountries.get(i - 1).getCoviddataList().get(j).getProodqty() + myCountries.get(i).getCoviddataList().get(j).getProodqty());
                    myCountries.get(i).getCoviddataList().get(j).setQty(myCountries.get(i - 1).getCoviddataList().get(j).getQty() + myCountries.get(i).getCoviddataList().get(j).getQty());
                }
                myCountries.remove(i - 1);
                i--;
            }
        }
        //Εγγραφή στον πίνακα αφού
        //προηγηθεί έλεγχος ύπαρξης
        em.getTransaction().begin();
        for (int i = 0; i < myCountries.size(); i++) {
            for (int j = 0; j < results.size(); j++) {
                TypedQuery<Short> query1 = (TypedQuery<Short>) em.createQuery("select cd.datakind from Coviddata cd where cd.datakind =:currentData and cd.country.name =:currentName").setMaxResults(1);
                query1.setParameter("currentData", dataKind);
                query1.setParameter("currentName", results.get(j).getName());
                if (myCountries.get(i).getName().equals(results.get(j).getName()) && query1.getResultList().isEmpty()) {
                    results.get(j).setCoviddataList(myCountries.get(i).getCoviddataList());
                    for (Coviddata cd : results.get(j).getCoviddataList()) {
                        cd.setCountry(results.get(j));
                        em.persist(cd);
                    }
                }
            }
        }
        em.getTransaction().commit();
    }

    public static void fillCovidDataList(JsonObject jsonObject, Country myCountry, Short dataKind, List<Coviddata> mydataList) throws ParseException, ParseException {
        //η μέθοδος που γεμίζει την λίστα CoviddataList.
        //για κάθε χώρα ψάχνουμε τα πεδία που είναι τύπου ημερομηνίες
        //με την κατάλληλη λογική έκφραση
        //και τραβάμε τα αντίστοιχα values.
        String regex = "^\\d{1,2}/\\d{1,2}/\\d{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher;
        int tempQty = 0;
        for (Object key : jsonObject.keySet()) {
            String keyToString = (String) key;
            matcher = pattern.matcher(keyToString);
            if (matcher.matches()) {
                Coviddata myData = new Coviddata();
                Date date = new SimpleDateFormat("MM/dd/yy").parse(keyToString);
                myData.setCountry(myCountry);
                myData.setDatakind(dataKind);
                myData.setTrndate(date);
                myData.setProodqty(jsonObject.get(keyToString).getAsInt());
                myData.setQty(myData.getProodqty() - tempQty);
                mydataList.add(myData);
                tempQty = myData.getProodqty();
            }
        }
    }

    public static String num2string (Short num){
        //βοηθητική μέθοδος που χρησιμοποιείται
        //από άλλες κλάσεις
        String name = null;
        if (num == (short) 1) {
            name = "confirmed";
        }
        if (num == (short) 2) {
            name = "deaths";
        }
        if (num == (short) 3) {
            name = "recovered";
        }
        return name;
    }

    public static Short numType (String stringType){
        //βοηθητική μέθοδος, αντίστροφη της προηγούμενης
        Short num = 0;
        if (stringType == "confirmed") {
            num = 1;
        } else if (stringType == "deaths") {
            num = 2;
        } else if (stringType == "recovered") {
            num = 3;
        }
        return num;
    }

}
