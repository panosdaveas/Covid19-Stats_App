package GUI;

import CHART.LineChart;
import GRID.Grid;
import MAP.Map;
import model.Country;
import model.Coviddata;
import org.jfree.ui.RefineryUtilities;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static FILL.Fill.*;


public class Gui {
    JFrame myFrame;
    EntityManager em;

    public Gui(JFrame myFrame) {
        this.myFrame = myFrame;
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("3hGE_lib_testPU");
        em = emf.createEntityManager();
    }

    public void mainMenu () throws IOException {
        JMenu menu1 = new JMenu("Options");
        JMenu menu2 = new JMenu("Exit");
        JMenuItem item1 = new JMenuItem("Manage Covid19 Data");
        JMenuItem item2 = new JMenuItem("Display Country Data");
        JMenuItem item3 = new JMenuItem("Display Data Map");
        JMenuItem item4 = new JMenuItem("Quit App");
        JMenuBar menuBar = new JMenuBar();
        menu1.add(item1);
        menu1.addSeparator();
        menu1.add(item2);
        menu1.addSeparator();
        menu1.add(item3);
        menu2.add(item4);
        menuBar.add(menu1);
        menuBar.add(menu2);
        myFrame.setJMenuBar(menuBar);

        item4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.dispose();
                System.exit(0);
            }
        });
        item1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.setContentPane(panelInsertData());
                myFrame.repaint();
                myFrame.revalidate();
            }
        });
        item2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContentPane().removeAll();
                myFrame.invalidate();
                myFrame.setContentPane(panelInsertDataPerCountry());
                myFrame.repaint();
                myFrame.revalidate();
            }
        });
        item3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                myFrame.getContentPane().removeAll();
                myFrame.invalidate();
                myFrame.setContentPane(panelMap());
                myFrame.repaint();
                myFrame.revalidate();
            }
        });
    }

    public void clearCountries () {
        em.getTransaction().begin();
        TypedQuery<Country> query = em.createQuery("delete from Country", Country.class);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public void clearAllCoviddata () {
        em.getTransaction().begin();
        TypedQuery<Coviddata> query = em.createQuery("delete from Coviddata", Coviddata.class);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public void clearSelectedCoviddata (Short datakind) {
        em.getTransaction().begin();
        TypedQuery<Coviddata> query = em.createQuery("delete from Coviddata where datakind =:datakind", Coviddata.class);
        query.setParameter("datakind", datakind);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public void clearSelectedCountry (String countryName){
        em.getTransaction().begin();
        TypedQuery<Coviddata> query = em.createQuery("delete from Coviddata cd where cd.country.name = :selectedCountry", Coviddata.class);
        query.setParameter("selectedCountry", countryName);
        query.executeUpdate();
        em.getTransaction().commit();
    }

    public JPanel panelInsertData (){
        JPanel myPanel = new JPanel();
        myPanel.setSize(myFrame.getSize());
        String[] dataType = {"<empty>", "confirmed", "deaths", "recovered"};
        JComboBox myBox = new JComboBox(dataType);
        java.util.List<JButton> buttons = new ArrayList<>();
        buttons.add(new JButton( "Insert Countries"));
        buttons.add(new JButton("Insert Data"));
        buttons.add(new JButton("Clear Countries"));
        buttons.add(new JButton("Clear Data"));
        buttons.add(new JButton("Reset"));
        JLabel label = new JLabel();
        label.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC, 12));
        label.setHorizontalAlignment(JLabel.CENTER);
        List<String> labelNames = new ArrayList<>();
        for(Short i = 1; i < 4; i++){
            TypedQuery<Short> query = (TypedQuery<Short>) em.createQuery("select cd.datakind from Coviddata cd where cd.datakind =:i").setMaxResults(1);
            query.setParameter("i", i);
            if(!query.getResultList().isEmpty()){
                labelNames.add(num2string(query.getSingleResult()));
            }
        }
        if(!labelNames.isEmpty()){
            label.setText("Data inserted : " + labelNames.toString());
        }
        final boolean[] flag = {false};

        myPanel.setLayout(new GridLayout(4, 1));
        JPanel panel1 = new JPanel(new BorderLayout(4, 4));
        panel1.setBorder(new TitledBorder(null, "Select Data", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel1inside = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.insets = new Insets(4, 4, 4, 4);
        panel1inside.add(myBox, constraints);
        panel1.add(panel1inside);
        myPanel.add(panel1);

        JPanel panel2 = new JPanel(new BorderLayout(4, 4));
        panel2.setBorder(new TitledBorder(null, null, TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel2inside = new JPanel(new GridBagLayout());
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        panel2inside.add(buttons.get(0), constraints);
        constraints.gridx++;
        panel2inside.add(buttons.get(1), constraints);
        constraints.anchor = GridBagConstraints.SOUTH;
        constraints.gridwidth = 2;
        constraints.gridx = 0;
        panel2inside.add(label, constraints);
        panel2.add(panel2inside);
        myPanel.add(panel2);

        JPanel panel3 = new JPanel(new BorderLayout(4,4));
        panel3.setBorder(new TitledBorder(null, null, TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel3inside = new JPanel(new GridBagLayout());
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        panel3inside.add(buttons.get(2), constraints);
        constraints.gridx++;
        panel3inside.add(buttons.get(3), constraints);
        panel3.add(panel3inside);
        myPanel.add(panel3);

        JPanel panel4 = new JPanel(new BorderLayout(4,4));
        panel4.setBorder(new TitledBorder(null, null, TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel4inside = new JPanel(new GridBagLayout());
        panel4inside.add(buttons.get(4));
        panel4.add(panel4inside);
        myPanel.add(panel4);

        for (JButton b : buttons) {
            if (b != buttons.get(4)) {
                b.setEnabled(false);
            }
        }
        myBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myBox.getSelectedIndex() != 0) {
                    TypedQuery<Short> query = (TypedQuery<Short>) em.createQuery("select cd.datakind from Coviddata cd where cd.datakind =:selectedData").setMaxResults(1);
                    query.setParameter("selectedData", numType((String) myBox.getItemAt(myBox.getSelectedIndex())));
                    if(!query.getResultList().isEmpty()){
                        buttons.get(3).setEnabled(true);
                    }else {
                        buttons.get(3).setEnabled(false);
                    }
                    buttons.get(0).setEnabled(true);
                    buttons.get(1).setEnabled(true);
                    buttons.get(2).setEnabled(true);
                    buttons.get(4).setEnabled(true);
                } else if (myBox.getSelectedIndex() == 0) {
                    buttons.get(1).setEnabled(false);
                    buttons.get(3).setEnabled(false);
                }
            }
        });
        buttons.get(4).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int input = JOptionPane.showConfirmDialog(null, "Clear All ?");
                if (input == 0) {
                    myFrame.getContentPane().removeAll();
                    myFrame.getContentPane().invalidate();
                    clearAllCoviddata();
                    clearCountries();
                    myFrame.setContentPane(panelInsertData());
                    myFrame.repaint();
                    myFrame.revalidate();
                }
            }
        });
        buttons.get(0).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TypedQuery<Country> query = em.createQuery("select c from Country c", Country.class).setMaxResults(1);
                if (query.getResultList().isEmpty()) {
                    try {
                        selectType(numType((String) myBox.getItemAt(myBox.getSelectedIndex())));
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                    buttons.get(0).setEnabled(false);
                    buttons.get(2).setEnabled(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Countries already inserted");
                }
            }
        });
        buttons.get(1).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TypedQuery<Country> query = em.createQuery("select c from Country c", Country.class).setMaxResults(1);
                TypedQuery<Coviddata> query1 = em.createQuery("select c from Coviddata c", Coviddata.class).setMaxResults(1);
                TypedQuery<Coviddata> query2 = em.createQuery("select c.datakind from Coviddata c where c.datakind =:currentData", Coviddata.class).setMaxResults(1);
                query2.setParameter("currentData", numType((String) myBox.getItemAt(myBox.getSelectedIndex())));

                if (query.getResultList().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please insert Countries first");
                }
                else if (!query1.getResultList().isEmpty() && !query2.getResultList().isEmpty() && flag[0] == true) {
                    JOptionPane.showMessageDialog(null, "This Data Kind is already inserted, you can choose another kind to insert");
                }
                else {
                    try {
                        selectDataType(numType((String) myBox.getItemAt(myBox.getSelectedIndex())));
                    } catch (FileNotFoundException fileNotFoundException) {
                        fileNotFoundException.printStackTrace();
                    } catch (ParseException parseException) {
                        parseException.printStackTrace();
                    }
                    buttons.get(3).setEnabled(true);
                    if(!labelNames.contains((String) myBox.getItemAt(myBox.getSelectedIndex()))){
                        labelNames.add((String) myBox.getItemAt(myBox.getSelectedIndex()));
                    }
                    label.setText("Data inserted : " + labelNames.toString());
                    flag[0] = true;
                }
            }
        });
        buttons.get(3).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int input = JOptionPane.showConfirmDialog(null, "Clear CovidData for " + myBox.getItemAt(myBox.getSelectedIndex()) + " ?");
                if (input == 0) {
                    clearSelectedCoviddata(numType((String) myBox.getItemAt(myBox.getSelectedIndex())));
                    buttons.get(3).setEnabled(false);
                    labelNames.remove((String) myBox.getItemAt(myBox.getSelectedIndex()));
                    if(!labelNames.isEmpty()) {
                        label.setText("Data inserted : " + labelNames.toString());
                    } else {
                        label.setText(null);
                    }
                }
            }
        });
        buttons.get(2).addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TypedQuery<Coviddata> query = em.createQuery("select c from Coviddata c", Coviddata.class).setMaxResults(1);
                if (!query.getResultList().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please clear CovidData First");
                } else {
                    int input = JOptionPane.showConfirmDialog(null, "Clear Countries ?");
                    if (input == 0) {
                        clearCountries();
                        buttons.get(0).setEnabled(true);
                        buttons.get(2).setEnabled(false);
                        myBox.setEnabled(true);
                    }
                }
            }
        });

        return myPanel;
    }

    public JPanel panelInsertDataPerCountry (){

        TypedQuery<Country> query = em.createQuery("select c.name from Country c", Country.class);
        java.util.List<Country> results = query.getResultList();
        JLabel label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setSize(400, 100);
        JComboBox myBox = new JComboBox();
        myBox.addItem("<empty>");
        for (int i = 0; i < results.size(); i++) {
            myBox.addItem(results.get(i));
        }
        myBox.setBounds(150, 150, 150, 40);

        JComboBox boxFrom = new JComboBox();
        JComboBox boxTo = new JComboBox();
        boxFrom.setSize(60, 40);
        boxTo.setSize(60, 40);
        boxFrom.removeAllItems();
        boxTo.removeAllItems();
        Date[] myDates = new Date[2];
        myDates[0] = null;
        myDates[1] = null;
        DefaultListModel<String> answers = new DefaultListModel<String>();

        JButton reset = new JButton("reset");
        JButton bviewGRid = new JButton("GRID");
        JButton bviewMap = new JButton("MAP");
        JButton show = new JButton("CHART");
        JButton clearData = new JButton("CLEAR");
        clearData.setVisible(false);
        show.setVisible(false);
        bviewGRid.setVisible(false);
        bviewMap.setVisible(false);
        JCheckBox[] boxes = new JCheckBox[3];
        boxes[0] = new JCheckBox("confirmed");
        boxes[1] = new JCheckBox("deaths");
        boxes[2] = new JCheckBox("recovered");
        for (int i = 0; i < boxes.length; i++) {
            boxes[i].setEnabled(false);
        }

        JPanel myPanel = new JPanel(new GridBagLayout());
        myPanel.setSize(myFrame.getSize());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 0.33;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 4, 4, 4);

        JPanel panel1 = new JPanel(new BorderLayout(0, 0));
        panel1.setBorder(new TitledBorder(null, "Select Country", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel1inside = new JPanel(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 1;
        c1.weightx = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(4, 4, 4, 4);
        panel1inside.add(myBox, c1);
        c1.gridy++;
        panel1inside.add(label, c1);
        panel1.add(panel1inside);
        myPanel.add(panel1, constraints);

        JPanel panel2 = new JPanel(new BorderLayout(0, 0));
        panel2.setBorder(new TitledBorder(null, "Date Range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel2inside = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 1;
        c2.weightx = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(4, 4, 4, 4);
        panel2inside.add(boxFrom, c2);
        c2.gridy++;
        panel2inside.add(boxTo, c2);
        panel2.add(panel2inside);
        constraints.gridy++;
        myPanel.add(panel2, constraints);

        JPanel panel4 = new JPanel(new BorderLayout(0, 0));
        panel4.setBorder(new TitledBorder(null, "Data Type", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel4inside = new JPanel(new GridBagLayout());
        GridBagConstraints c4 = new GridBagConstraints();
        c4.gridx = 0;
        c4.gridy = 0;
        c4.gridwidth = 1;
        c4.weightx = 1;
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.insets = new Insets(4, 4, 4, 4);
        panel4inside.add(boxes[0], c4);
        c4.gridy++;
        panel4inside.add(boxes[1], c4);
        c4.gridy++;
        panel4inside.add(boxes[2], c4);
        panel4.add(panel4inside);
        constraints.gridy++;
        myPanel.add(panel4, constraints);

        JPanel panel3 = new JPanel(new BorderLayout(0, 0));
        panel3.setBorder(new TitledBorder(null, "Actions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel3inside = new JPanel(new GridBagLayout());
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 0;
        c3.gridwidth = GridBagConstraints.REMAINDER;
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.weightx = 1;
        c3.insets = new Insets(4, 4, 4, 4);
        panel3inside.add(bviewGRid, c3);
        c3.gridy++;
        panel3inside.add(show, c3);
        c3.gridy++;
        panel3inside.add(bviewMap, c3);
        c3.gridy++;
        panel3inside.add(clearData, c3);
        c3.gridy++;
        c3.weighty = 1;
        c3.anchor = GridBagConstraints.SOUTH;
        panel3inside.add(reset, c3);
        constraints.gridy = 0;
        constraints.gridx++;
        constraints.gridheight = GridBagConstraints.REMAINDER;
        constraints.fill = GridBagConstraints.VERTICAL;
        constraints.weighty = 1;
        constraints.weightx = 0;
        panel3.add(panel3inside);
        myPanel.add(panel3, constraints);

        reset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int input = JOptionPane.showConfirmDialog(null, "Clear all selections ?");
                if (input == 0) {
                    myFrame.getContentPane().removeAll();
                    myFrame.getContentPane().invalidate();
                    myFrame.setContentPane(panelInsertDataPerCountry());
                    myFrame.repaint();
                    myFrame.revalidate();
                }
            }
        });

        myBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myBox.getItemAt(myBox.getSelectedIndex()) != "<empty>") {
                    String data = "Country selected : " + myBox.getItemAt(myBox.getSelectedIndex());
                    label.setText(data);
                    for (int i = 0; i < boxes.length; i++) {
                        boxes[i].setEnabled(true);
                    }
                    String name = myBox.getItemAt(myBox.getSelectedIndex()).toString();
                    TypedQuery<Country> query1 = em.createQuery("select c from Country c where c.name = :myName", Country.class);
                    query1.setParameter("myName", name);
                    Country myCountry = query1.getSingleResult();
                    TypedQuery<Coviddata> query2 = em.createQuery("select cd.trndate from Coviddata cd where cd.datakind = :myDataKind and cd.country.name = :myName", Coviddata.class);
                    //TypedQuery<Short> query3 = (TypedQuery<Short>) em.createQuery("select min(cd.datakind) from Coviddata cd");
                    TypedQuery<Short> query3 = (TypedQuery<Short>) em.createQuery("select min(cd.datakind) from Coviddata cd where cd.country.name =:currentName");
                    query3.setParameter("currentName", (String) myBox.getItemAt(myBox.getSelectedIndex()));
                    Short datakind = query3.getSingleResult();
                    query2.setParameter("myDataKind", datakind);
                    query2.setParameter("myName", myCountry.getName());
                    java.util.List<Coviddata> results = query2.getResultList();
                    boxFrom.removeAllItems();
                    boxFrom.addItem("<empty>");
                    boxTo.addItem("<empty>");
                    for (int i = 0; i < results.size(); i++) {
                        boxFrom.addItem(results.get(i));
                    }
                    bviewMap.setVisible(true);
                    bviewGRid.setVisible(true);
                    clearData.setVisible(true);
                    show.setVisible(true);
                }else {
                    boxFrom.removeAllItems();
                    boxFrom.addItem("<empty>");
                }
            }
        });

        boxFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boxFrom.getSelectedIndex() > 0) {
                    myDates[0] = (Date) boxFrom.getSelectedItem();
                    myDates[1] = (Date) boxFrom.getSelectedItem();
                    boxTo.setEnabled(true);
                    boxTo.removeAllItems();
                    for (int i = boxFrom.getSelectedIndex(); i < boxFrom.getItemCount(); i++) {
                        boxTo.addItem(boxFrom.getItemAt(i));
                    }
                    for (int i = 0; i < boxes.length; i++) {
                        boxes[i].setVisible(true);
                    }
                    show.setVisible(true);
                }
                if(boxFrom.getItemAt(boxFrom.getSelectedIndex()) == "<empty>"){
                    boxTo.removeAllItems();
                    boxTo.addItem("<empty>");
                    boxTo.setEnabled(false);
                    myDates[0] = null;
                    myDates[1] = null;
                }
            }
        });

        boxTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boxTo.getSelectedIndex() > 0) {
                    myDates[1] = (Date) boxTo.getSelectedItem();
                }
            }
        });

        bviewGRid.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Grid myGrid = new Grid(myBox.getItemAt(myBox.getSelectedIndex()).toString(), myDates, em);
                JScrollPane[] scrollText = new JScrollPane[3];
                JTextPane[] panelText = new JTextPane[3];
                StyledDocument[] doc = new DefaultStyledDocument[3];
                try {
                    doc = myGrid.readXML();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                for(int i = 0; i < 3; i++){
                    panelText[i] = new JTextPane();
                    panelText[i].setEditable(false);
                    panelText[i].setStyledDocument(doc[i]);
                    panelText[i].setCaretPosition(0);
                    panelText[i].setFont(new Font("Courier", Font.BOLD, 14));
                    scrollText[i] = new JScrollPane(panelText[i]);
                }

                JTabbedPane tabbedPane = new JTabbedPane();
                tabbedPane.setForeground(Color.DARK_GRAY);
                tabbedPane.addTab("confirmed", scrollText[0]);
                tabbedPane.addTab("deaths", scrollText[1]);
                tabbedPane.addTab("recovered", scrollText[2]);

                JFrame frameGrid = new JFrame("XML");
                frameGrid.add(tabbedPane, BorderLayout.CENTER);
                frameGrid.setSize(450, 600);
                frameGrid.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frameGrid.setLocation(myFrame.getLocation().x + myFrame.getSize().height/2, (int) (myFrame.getLocation().y + myFrame.getSize().getHeight()/2));
                frameGrid.setVisible(true);
            }
        });


        for (int i = 0; i < boxes.length; i++) {
            int finalI = i;
            boxes[i].addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (boxes[finalI].isSelected()) {
                        if (!answers.contains(boxes[finalI].getName())) {
                            answers.addElement(boxes[finalI].getText());
                        }
                    } else {
                        answers.removeElement(boxes[finalI].getText());
                    }
                }
            });
        }

        show.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println(answers);
                if (boxFrom.getSelectedIndex() == 0) {
                    JOptionPane.showMessageDialog(null, "Select Date range first");
                    return;
                }
                if (answers.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Select Data Type first");
                    return;
                }
                for (int i = 0; i < answers.size(); i++) {
                    TypedQuery<Coviddata> query = em.createQuery("select cd.datakind from Coviddata cd where cd.datakind = :selectedType and cd.country.name = :myCountryName", Coviddata.class);
                    query.setParameter("selectedType", numType(answers.getElementAt(i)));
                    query.setParameter("myCountryName", myBox.getItemAt(myBox.getSelectedIndex()));
                    java.util.List<Coviddata> results = query.getResultList();
                    if (results.isEmpty()) {
                        List<Integer> indeces = new ArrayList<>();
                        JOptionPane.showMessageDialog(null, "You have not inserted the data for : " + answers.getElementAt(i));
                        for(int j = 0; j < boxes.length; j++){
                            if(boxes[j].isSelected() && boxes[j].getText().equals(answers.getElementAt(i))){
                                indeces.add(j);
                            }
                        }
                        for(int j = 0; j < boxes.length; j++){
                            if(indeces.contains(j)){
                                boxes[j].setSelected(false);
                            }
                        }
                        return;
                    }
                }
                java.util.List<Short> listeners = new ArrayList<>();
                for (int i = 0; i < answers.size(); i++) {
                    listeners.add(numType(answers.get(i)));
                }
                System.out.println(listeners);
                if(!listeners.isEmpty()) {
                    String chartName = myBox.getItemAt(myBox.getSelectedIndex()) + " Line Chart";
                    TypedQuery<Coviddata> queryValues = em.createQuery("select cd from Coviddata cd where  cd.country.name = :myCountryName and cd.trndate between :dateFrom and :dateTo order by cd.trndate", Coviddata.class);
                    queryValues.setParameter("myCountryName", myBox.getItemAt(myBox.getSelectedIndex()));
                    queryValues.setParameter("dateFrom", myDates[0]);
                    queryValues.setParameter("dateTo", myDates[1]);
                    java.util.List<Coviddata> resultValues = queryValues.getResultList();
                    panelChart(chartName, resultValues, listeners);
                }
            }
        });

        clearData.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int input = JOptionPane.showConfirmDialog(null, "Clear data for " + myBox.getItemAt(myBox.getSelectedIndex()) + "?");
                if (input == 0) {
                    clearSelectedCountry((String) myBox.getItemAt(myBox.getSelectedIndex()));
                }
            }
        });

        bviewMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> myCountry = new ArrayList<>();
                myCountry.add((String) myBox.getItemAt(myBox.getSelectedIndex()));
                try {
                    Map myMap = new Map(myCountry, myPanel, myDates);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        return myPanel;
    }

    public void panelChart (String chartName, java.util.List< Coviddata > resultList, java.util.List< Short > selectedDataType){
        LineChart myChart = new LineChart(chartName, resultList, selectedDataType);
        myChart.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        myChart.pack();
        RefineryUtilities.centerFrameOnScreen(myChart);
        myChart.setVisible(true);
    }

    public void sort(DefaultListModel<String> model){
        List<String> list = new ArrayList<>();
        for(int i = 0; i < model.getSize(); i++){
            list.add(model.getElementAt(i));
        }
        Collections.sort(list);
        model.removeAllElements();
        for(String s : list){
            model.addElement(s);
        }
    }

    public JPanel panelMap (){
        TypedQuery<String> query = (TypedQuery<String>) em.createQuery("select c.name from Country c");
        java.util.List<String> results = query.getResultList();

        JLabel label2 = new JLabel();
        label2.setVisible(false);
        JComboBox<String> myBox = new JComboBox<String>();
        myBox.addItem("<empty>");
        for(int i = 0; i < results.size(); i++){
            myBox.addItem(results.get(i));
        }

        DefaultListModel<String> defaultListModel1 = new DefaultListModel<String>();
        DefaultListModel<String> defaultListModel2 = new DefaultListModel<String>();
        JList<String> myList = new JList<String>();
        JList<String> myCopyList = new JList<String>();
        myList.setFixedCellWidth(90);
        myList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myList.setVisibleRowCount(7);
        myCopyList.setFixedCellWidth(90);
        myCopyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myCopyList.setVisibleRowCount(7);

        JButton button1 = new JButton("add>>");
        JButton button2 = new JButton("<<remove");
        JButton buttonReset = new JButton("reset");
        JButton buttonMap = new JButton("MAP");
        button2.setEnabled(false);
        buttonReset.setEnabled(false);
        buttonMap.setEnabled(false);

        JComboBox boxFrom = new JComboBox();
        JComboBox boxTo = new JComboBox();
        boxFrom.setSize(60, 40);
        boxTo.setSize(60, 40);
        boxFrom.removeAllItems();
        boxTo.removeAllItems();
        Date[] myDates = new Date[2];
        myDates[0] = null;
        myDates[1] = null;

        JPanel myPanel = new JPanel(new GridBagLayout());
        myPanel.setSize(myFrame.getSize());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = 0;
        constraints.gridx = 0;
        constraints.weightx = 1;
        constraints.weighty = 0.33;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.insets = new Insets(4, 4, 4, 4);

        JPanel panel1 = new JPanel(new BorderLayout(0, 0));
        panel1.setBorder(new TitledBorder(null, "Select Main Country", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel1inside = new JPanel(new GridBagLayout());
        GridBagConstraints c1 = new GridBagConstraints();
        c1.gridx = 0;
        c1.gridy = 0;
        c1.gridwidth = 1;
        c1.weightx = 1;
        c1.fill = GridBagConstraints.HORIZONTAL;
        c1.insets = new Insets(4, 4, 4, 4);
        panel1inside.add(myBox, c1);
        c1.gridy++;
        label2.setHorizontalAlignment(JLabel.CENTER);
        panel1inside.add(label2, c1);
        panel1.add(panel1inside);
        myPanel.add(panel1, constraints);

        JPanel panelCenter = new JPanel(new FlowLayout());

        JPanel panel2 = new JPanel(new BorderLayout(0, 0));
        panel2.setBorder(new TitledBorder(null, "Available", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel2inside = new JPanel(new GridBagLayout());
        GridBagConstraints c2 = new GridBagConstraints();
        c2.gridx = 0;
        c2.gridy = 0;
        c2.gridwidth = 1;
        c2.gridheight = 1;
        c2.weightx = 1;
        c2.fill = GridBagConstraints.HORIZONTAL;
        c2.insets = new Insets(4, 4, 4, 4);
        JScrollPane scroll1 = new JScrollPane(myList);
        panel2inside.add(scroll1, c2);
        panel2.add(panel2inside);
        panelCenter.add(panel2);

        JPanel panel4 = new JPanel(new GridBagLayout());
        GridBagConstraints c4 = new GridBagConstraints();
        c4.gridx = 0;
        c4.gridy = 0;
        c4.gridwidth = 1;
        c4.gridheight = 1;
        c4.weightx = 1;
        c4.fill = GridBagConstraints.HORIZONTAL;
        c4.insets = new Insets(4, 4, 4, 4);
        panel4.add(button1, c4);
        c4.gridy++;
        panel4.add(button2, c4);
        panelCenter.add(panel4);

        JPanel panel3 = new JPanel(new BorderLayout(0, 0));
        panel3.setBorder(new TitledBorder(null, "Selected", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel3inside = new JPanel(new GridBagLayout());
        GridBagConstraints c3 = new GridBagConstraints();
        c3.gridx = 0;
        c3.gridy = 0;
        c3.gridwidth = 1;
        c3.gridheight = 1;
        c3.weightx = 1;
        c3.fill = GridBagConstraints.HORIZONTAL;
        c3.insets = new Insets(4, 4, 4, 4);
        JScrollPane scroll2 = new JScrollPane(myCopyList);
        panel3inside.add(scroll2, c3);
        panel3.add(panel3inside);
        panelCenter.add(panel3);

        constraints.gridy++;
        myPanel.add(panelCenter, constraints);

        JPanel panel6 = new JPanel(new BorderLayout(0, 0));
        panel6.setBorder(new TitledBorder(null, "Date Range", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel6inside = new JPanel(new GridBagLayout());
        GridBagConstraints c6 = new GridBagConstraints();
        c6.gridx = 0;
        c6.gridy = 0;
        c6.gridwidth = 1;
        c6.weightx = 1;
        c6.fill = GridBagConstraints.HORIZONTAL;
        c6.insets = new Insets(4, 4, 4, 4);
        panel6inside.add(boxFrom, c6);
        c6.gridy++;
        panel6inside.add(boxTo, c6);
        panel6.add(panel6inside);
        constraints.gridy++;
        myPanel.add(panel6, constraints);

        JPanel panel5 = new JPanel(new BorderLayout(0, 0));
        panel5.setBorder(new TitledBorder(null, "Actions", TitledBorder.LEADING, TitledBorder.TOP, null, null));
        JPanel panel5inside = new JPanel(new GridBagLayout());
        GridBagConstraints c5 = new GridBagConstraints();
        c5.gridx = 0;
        c5.gridy = 0;
        c5.fill = GridBagConstraints.HORIZONTAL;
        c5.weightx = 1;
        c5.insets = new Insets(4, 4, 4, 4);
        panel5inside.add(buttonMap, c5);
        c5.gridx++;
        panel5inside.add(buttonReset, c5);
        panel5.add(panel5inside);

        constraints.gridy++;
        myPanel.add(panel5, constraints);

        myBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if((String) myBox.getItemAt(myBox.getSelectedIndex()) != "<empty>") {
                    String data = "Main Country : " + myBox.getItemAt(myBox.getSelectedIndex());
                    label2.setText(data);
                    label2.setVisible(true);
                    defaultListModel1.removeAllElements();
                    defaultListModel2.removeAllElements();
                    for (int i = 0; i < results.size(); i++) {
                        defaultListModel1.addElement(results.get(i));
                    }
                    defaultListModel1.removeElement(myBox.getItemAt(myBox.getSelectedIndex()));
                    myList.setModel(defaultListModel1);
                    myCopyList.setModel(defaultListModel2);
                    buttonMap.setEnabled(true);

                    String name = myBox.getItemAt(myBox.getSelectedIndex()).toString();
                    TypedQuery<Country> query1 = em.createQuery("select c from Country c where c.name = :myName", Country.class);
                    query1.setParameter("myName", name);
                    Country myCountry = query1.getSingleResult();
                    TypedQuery<Coviddata> query2 = em.createQuery("select cd.trndate from Coviddata cd where cd.datakind = :myDataKind and cd.country.name = :myName", Coviddata.class);
                    TypedQuery<Short> query3 = (TypedQuery<Short>) em.createQuery("select min(cd.datakind) from Coviddata cd");
                    Short datakind = query3.getSingleResult();
                    query2.setParameter("myDataKind", datakind);
                    query2.setParameter("myName", myCountry.getName());
                    java.util.List<Coviddata> results = query2.getResultList();
                    boxFrom.addItem("<empty>");
                    boxTo.addItem("<empty>");
                    for (int i = 0; i < results.size(); i++) {
                        boxFrom.addItem(results.get(i));
                    }
                    boxFrom.setEnabled(true);
                    boxTo.setEnabled(false);
                    boxFrom.setSelectedIndex(0);
                    buttonReset.setEnabled(true);

                }else {
                    boxFrom.setEnabled(false);
                    boxTo.setEnabled(false);
                    label2.setVisible(false);
                    defaultListModel1.removeAllElements();
                    defaultListModel2.removeAllElements();
                    myList.setModel(defaultListModel1);
                    myCopyList.setModel(defaultListModel2);
                    buttonMap.setEnabled(false);
                    boxFrom.setSelectedIndex(0);
                }
            }
        });

        boxFrom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boxFrom.getSelectedIndex() > 0) {
                    myDates[0] = (Date) boxFrom.getSelectedItem();
                    myDates[1] = (Date) boxFrom.getSelectedItem();
                    boxTo.setEnabled(true);
                    boxTo.removeAllItems();
                    for (int i = boxFrom.getSelectedIndex(); i < boxFrom.getItemCount(); i++) {
                        boxTo.addItem(boxFrom.getItemAt(i));
                    }
                } else {
                    boxTo.removeAllItems();
                    boxTo.addItem("<empty>");
                    boxTo.setEnabled(false);
                    myDates[0] = null;
                    myDates[1] = null;
                }
            }
        });

        boxTo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (boxTo.getSelectedIndex() > 0) {
                    myDates[1] = (Date) boxTo.getSelectedItem();
                }
            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myList.isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a country to add");
                } else {
                    button1.setEnabled(true);
                    int value = myList.getSelectedIndex();
                    defaultListModel2.addElement(myList.getSelectedValue());
                    myCopyList.setModel(defaultListModel2);

                    if (defaultListModel1.getSize() != 0) {
                        defaultListModel1.removeElementAt(value);
                    }
                    myList.setModel(defaultListModel1);
                    button2.setEnabled(true);
                    buttonMap.setEnabled(true);
                    buttonReset.setEnabled(true);
                }
            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (myCopyList.isSelectionEmpty()) {
                    JOptionPane.showMessageDialog(null, "Please select a country to remove");
                } else {
                    int value = myCopyList.getSelectedIndex();
                    defaultListModel1.addElement(myCopyList.getSelectedValue());
                    sort(defaultListModel1);
                    myList.setModel(defaultListModel1);
                    if (defaultListModel2.getSize() != 0) {
                        defaultListModel2.removeElementAt(value);
                    }
                    myCopyList.setModel(defaultListModel2);
                    if (myCopyList.getModel().getSize() == 0) {
                        button2.setEnabled(false);
                        if(myBox.getItemAt(myBox.getSelectedIndex()) == "<empty>"){
                            buttonMap.setEnabled(false);
                            buttonReset.setEnabled(false);
                        }
                    }
                }
            }
        });

        buttonMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<String> countryNames = new ArrayList<>();
                System.out.println(countryNames);
                countryNames.add(myBox.getItemAt(myBox.getSelectedIndex()));
                for (int i = 0; i < myCopyList.getModel().getSize(); i++) {
                    countryNames.add(myCopyList.getModel().getElementAt(i));
                }
                System.out.println(countryNames);
                try {
                    Map myMap = new Map(countryNames, myPanel, myDates);
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        });

        buttonReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int input = JOptionPane.showConfirmDialog(null, "Clear all selections ?");
                if (input == 0) {
                    myFrame.getContentPane().removeAll();
                    myFrame.getContentPane().invalidate();
                    myFrame.setContentPane(panelMap());
                    myFrame.repaint();
                    myFrame.revalidate();
                }
            }
        });

        return myPanel;
    }

}
