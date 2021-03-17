/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import GUI.Gui;
import org.xml.sax.SAXException;
import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.text.ParseException;

/**
 *
 * @author panos
 *         
 */

public class Main {

    /**
     * @param args the command line arguments
     */

    public static void main(String[] args) throws IOException, ParseException, ParserConfigurationException, SAXException, BadLocationException {

        JFrame myFrame = new JFrame();
        myFrame.setSize(400, 600);
	myFrame.setTitle("Covid19-Stats App");

        Gui gui = new Gui(myFrame);
        gui.mainMenu();
        gui.clearAllCoviddata();
        gui.clearCountries();
	
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.setLocationRelativeTo(null);
        myFrame.setVisible(true);

    }

}
