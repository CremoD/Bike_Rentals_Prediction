package rentals_weather_gui;
import machine_learning.Regression;
import machine_learning.WeatherForecast;
import org.apache.spark.sql.SparkSession;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class AppManager {

    public static void main(String[] args) {
        String inputPath = args[0];
        // Create a Spark Session object and set the name of the application
        SparkSession ss = SparkSession.builder().appName("London Bike Rentals: Prediction").getOrCreate();

        ArrayList<WeatherForecast> forecasts_list = Regression.trainAndPredictFuture(ss, inputPath);

        // create the frame
        JFrame frame = new JFrame("Bike Shares Oracle");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(1135, 850));

        //DayRentalsPanel prova = new DayRentalsPanel(forecasts_list, 13);
        IntroPanel intro = new IntroPanel(forecasts_list);
        MainPanel.getMainPanel().setIntro(intro);
        frame.getContentPane().add(MainPanel.getMainPanel());

        // create menu bar and menu item
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem exit = new JMenuItem("Exit");
        JMenuItem about = new JMenuItem("About");

        // create and add the action listener for exit and about
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        String message = "Bike shares oracle";
        about.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(MainPanel.getMainPanel(), message, "Welcome!", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // add menuItem to menu and the menu to the menuBar
        mb.add(menu);
        menu.add(exit);
        menu.add(about);

        frame.setJMenuBar(mb);

        frame.pack();
        frame.setVisible(true);
        frame.setResizable(false);

        ss.close();
    }


}
