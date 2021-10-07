package rentals_weather_gui;

import machine_learning.WeatherForecast;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

// panel representing the single day information
public class DayRentalsPanel extends JPanel {

    private JPanel dayP, titleP;
    private JLabel title;

    public DayRentalsPanel(ArrayList<WeatherForecast> introForecasts, int day) {

        // Create panel which contains all the components with a Box Layout
        dayP = new JPanel();
        dayP.setLayout(new BoxLayout(dayP, BoxLayout.Y_AXIS));
        dayP.add(Box.createRigidArea(new Dimension(0, 20)));

        ArrayList<WeatherForecast> currentFC = currentDayForecasts(introForecasts, day);

        // title panel
        titleP = new JPanel();
        titleP.setLayout(new BoxLayout(titleP, BoxLayout.X_AXIS));
        JLabel image1 = new JLabel();
        if (currentFC.get(0).getType_day().equals("weekend"))
            image1.setIcon(new ImageIcon("icons/weekend.png"));
        else
            image1.setIcon(new ImageIcon("icons/workday.png"));
        titleP.add(image1);
        titleP.add(Box.createRigidArea(new Dimension(20, 0)));
        title = new JLabel(currentFC.get(0).getDayOfWeekExpanded() +  " " + currentFC.get(0).getDay() + "-"
                + currentFC.get(0).getMonth() + "-" + currentFC.get(0).getYear());
        title.setFont((new Font("Helvetica", Font.BOLD, 45)));
        titleP.add(title);
        dayP.add(titleP);
        dayP.add(Box.createRigidArea(new Dimension(0, 50)));


        //////////////////////////////////////////////////////////////////
        // list of hourly forecast
        for (WeatherForecast current_hour_forecast : currentFC) {

            JPanel hourlyPanel = new JPanel();
            hourlyPanel.setLayout(new BorderLayout());
            JPanel currentHour = new JPanel();
            currentHour.setLayout(new BoxLayout(currentHour, BoxLayout.X_AXIS));
            JLabel weatherIcon = new JLabel();
            // case of night and clear
            if (current_hour_forecast.getWeather_main().equals("Clear") && current_hour_forecast.getDay_period().equals("Night"))
                weatherIcon.setIcon(new ImageIcon("icons/moon.png"));
            else
                weatherIcon.setIcon(new ImageIcon("icons/" + current_hour_forecast.getWeather_main() + ".png"));
            String hour = "";
            if (current_hour_forecast.getHour() <= 9)
                hour = "0"+current_hour_forecast.getHour();
            else
                hour = ""+current_hour_forecast.getHour();
            JLabel left = new JLabel("Hour: " + hour + ":00");
            left.setFont((new Font("Helvetica", Font.BOLD, 17)));
            currentHour.add(weatherIcon);
            currentHour.add(Box.createRigidArea(new Dimension(5, 0)));
            currentHour.add(left);

            JLabel pred = new JLabel("Bike shares prediction: " + current_hour_forecast.getCount());
            pred.setFont((new Font("Helvetica", Font.BOLD, 17)));
            hourlyPanel.add(currentHour, BorderLayout.WEST);
            hourlyPanel.add(pred, BorderLayout.EAST);
            dayP.add(hourlyPanel);

            // get weather information about that hour
            JPanel dataPanel = new JPanel();
            dataPanel.setLayout(new BoxLayout(dataPanel, BoxLayout.X_AXIS));
            JPanel firstColumn = new JPanel();
            firstColumn.setLayout(new BoxLayout(firstColumn, BoxLayout.Y_AXIS));
            JPanel secondColumn = new JPanel();
            secondColumn.setLayout(new BoxLayout(secondColumn, BoxLayout.Y_AXIS));
            JPanel thirdColumn = new JPanel();
            thirdColumn.setLayout(new BoxLayout(thirdColumn, BoxLayout.Y_AXIS));
            JPanel fourthColumn = new JPanel();
            fourthColumn.setLayout(new BoxLayout(fourthColumn, BoxLayout.Y_AXIS));

            // first column (weather, temp, temp min, temp max)
            JPanel sub0 = new JPanel();
            sub0.setLayout(new BoxLayout(sub0, BoxLayout.X_AXIS));
            sub0.setAlignmentX(LEFT_ALIGNMENT);
            sub0.add(new JLabel("Weather: " +  current_hour_forecast.getWeather_main()));
            JPanel sub1 = new JPanel();
            sub1.setLayout(new BoxLayout(sub1, BoxLayout.X_AXIS));
            sub1.setAlignmentX(LEFT_ALIGNMENT);
            sub1.add(new JLabel("Temp: " +  current_hour_forecast.getTemp() + " °C"));
            JPanel sub2 = new JPanel();
            sub2.setLayout(new BoxLayout(sub2, BoxLayout.X_AXIS));
            sub2.setAlignmentX(LEFT_ALIGNMENT);
            sub2.add(new JLabel("Temp min: " + current_hour_forecast.getTemp_min()+ " °C"));
            JPanel sub3 = new JPanel();
            sub3.setLayout(new BoxLayout(sub3, BoxLayout.X_AXIS));
            sub3.setAlignmentX(LEFT_ALIGNMENT);
            sub3.add(new JLabel("Temp max: " + current_hour_forecast.getTemp_max()+ " °C"));




            // second column (period, humidity, pressure, wind_speed)
            JPanel sub5 = new JPanel();
            sub5.setLayout(new BoxLayout(sub5, BoxLayout.X_AXIS));
            sub5.setAlignmentX(LEFT_ALIGNMENT);
            sub5.add(new JLabel("Period: " +  current_hour_forecast.getDay_period()));
            JPanel sub6 = new JPanel();
            sub6.setLayout(new BoxLayout(sub6, BoxLayout.X_AXIS));
            sub6.setAlignmentX(LEFT_ALIGNMENT);
            sub6.add( new JLabel("Humidity: " +  current_hour_forecast.getHumidity() + " %"));
            JPanel sub7 = new JPanel();
            sub7.setLayout(new BoxLayout(sub7, BoxLayout.X_AXIS));
            sub7.setAlignmentX(LEFT_ALIGNMENT);
            sub7.add(new JLabel("Pressure: " +current_hour_forecast.getPressure()+ " hPa"));
            JPanel sub8 = new JPanel();
            sub8.setLayout(new BoxLayout(sub8, BoxLayout.X_AXIS));
            sub8.setAlignmentX(LEFT_ALIGNMENT);
            sub8.add(new JLabel("Wind speed: " + current_hour_forecast.getWind_speed()+ " m/s"));


            firstColumn.add(sub0);
            firstColumn.add(Box.createRigidArea(new Dimension(0, 10)));
            firstColumn.add(sub5);

            secondColumn.add(sub1);
            secondColumn.add(Box.createRigidArea(new Dimension(0, 10)));
            secondColumn.add(sub6);

            thirdColumn.add(sub7);
            thirdColumn.add(Box.createRigidArea(new Dimension(0, 10)));
            thirdColumn.add(sub8);

            fourthColumn.add(sub2);
            fourthColumn.add(Box.createRigidArea(new Dimension(0, 10)));
            fourthColumn.add(sub3);


            dataPanel.add(firstColumn);
            dataPanel.add(Box.createRigidArea(new Dimension(40, 0)));
            dataPanel.add(secondColumn);
            dataPanel.add(Box.createRigidArea(new Dimension(40, 0)));
            dataPanel.add(thirdColumn);
            dataPanel.add(Box.createRigidArea(new Dimension(40, 0)));
            dataPanel.add(fourthColumn);


            dayP.add(dataPanel);
            dayP.add(Box.createRigidArea(new Dimension(0, 40)));
        }


        // display plot
        dayP.add(Box.createRigidArea(new Dimension(0, 40)));
        JPanel plotP = new JPanel();
        plotP.add(getPlot(currentFC));
        dayP.add(plotP);
        dayP.add(Box.createRigidArea(new Dimension(0, 50)));


        // back panel
        JPanel backPanel = new JPanel();
        JButton back = new JButton("Back");
        back.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                IntroPanel introPanelP = new IntroPanel(introForecasts);
                MainPanel.getMainPanel().swapPanel(introPanelP);
            }

        });
        backPanel.add(back);

        dayP.add(backPanel);
        dayP.add(Box.createRigidArea(new Dimension(0, 100)));
        add(dayP);
    }


    public ArrayList<WeatherForecast> currentDayForecasts(ArrayList<WeatherForecast> introForecasts, int day) {
        ArrayList<WeatherForecast> result = new ArrayList<WeatherForecast>();

        // iterate over the available forecasts and get those for the selected day
        for (WeatherForecast curr : introForecasts) {
            if (curr.getDay() == day)
                result.add(curr);
        }
        return result;

    }

    public ChartPanel getPlot(ArrayList<WeatherForecast> currBikeShares) {
        // Create dataset

        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series = new XYSeries("bike shares predicted");
        for (WeatherForecast curr : currBikeShares) {
            series.add(curr.getHour(), curr.getCount());
        }

        //Add series to dataset
        dataset.addSeries(series);
        XYDataset xy_dataset = dataset;

        // Create chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Predicted bike shares during the day",
                "Hour",
                "Bike-shares",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);


        // Create Panel
        ChartPanel panel = new ChartPanel(chart);
        panel.setPreferredSize(new Dimension(600, 400));
        panel.setOpaque(true);
        return panel;
    }
}
