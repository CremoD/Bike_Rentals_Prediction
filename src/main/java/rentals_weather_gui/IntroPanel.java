package rentals_weather_gui;

import machine_learning.WeatherForecast;

import org.jdesktop.swingx.JXDatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

// class representing the intro panel
public class IntroPanel extends JPanel {
    private JPanel introP, titleP;
    private JLabel title;
    private JXDatePicker picker;

    public IntroPanel(ArrayList<WeatherForecast> introForecasts) {

        // Create panel which contains all the components with a Box Layout
        introP = new JPanel();
        introP.setLayout(new BoxLayout(introP, BoxLayout.Y_AXIS));
        introP.add(Box.createRigidArea(new Dimension(0, 80)));

        // title panel
        titleP = new JPanel();
        titleP.setLayout(new BoxLayout(titleP, BoxLayout.X_AXIS));
        title = new JLabel("London Bike Shares Oracle");
        title.setFont((new Font("Helvetica", Font.BOLD, 45)));
        JLabel image1 = new JLabel();
        image1.setIcon(new ImageIcon("icons/london-2.png"));
        titleP.add(image1);
        titleP.add(title);
        titleP.add(Box.createRigidArea(new Dimension(30, 0)));
        JLabel image2 = new JLabel();
        image2.setIcon(new ImageIcon("icons/bicycle-parking.png"));
        titleP.add(image2);


        introP.add(titleP);
        introP.add(Box.createRigidArea(new Dimension(0, 90)));



        // label panel with explanation
        JPanel labelP = new JPanel();
        JLabel descr = new JLabel("Select a date in the next 5 days to predict the bike shares");
        descr.setFont((new Font("Helvetica", Font.PLAIN, 20)));
        labelP.add(descr);
        introP.add(labelP);
        introP.add(Box.createRigidArea(new Dimension(0, 20)));


        // calendar
        picker = new JXDatePicker();
        picker.setDate(Calendar.getInstance().getTime());
        picker.setFormats(new SimpleDateFormat("dd.MM.yyyy"));

        Date lower = new Date();
        Date upper = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
        String curr_date = sdf.format(lower);
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(curr_date));
            c.add(Calendar.DATE, 5);  // number of days to add
            upper = sdf.parse(sdf.format(c.getTime()));  // dt is now the new date
        } catch (ParseException e) {
            e.printStackTrace();
        }

        picker.getMonthView().setLowerBound(lower);
        picker.getMonthView().setUpperBound(upper);
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.X_AXIS));
        datePanel.add(picker);


        // search panel
        JButton predict = new JButton("Predict");
        predict.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // button to navigate to chosen day
                Date selected = picker.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(selected);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DayRentalsPanel swap = new DayRentalsPanel(introForecasts, day);
                MainPanel.getMainPanel().swapPanel(swap);
            }
        });

        datePanel.add(Box.createRigidArea(new Dimension(30, 0)));
        datePanel.add(predict);
        introP.add(datePanel);


        add(introP);
    }


}
