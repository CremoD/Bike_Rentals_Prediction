package machine_learning;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

// class that represents a hourly weather condition, together with information about the time period of the year
public class WeatherForecast {

    private String day_of_week, weather_main, season, day_period, type_day, dt_iso, dayOfWeekExpanded;
    private double feels_like, humidity, pressure, temp, temp_max, temp_min, wind_speed;
    private int hour, month, day, year;
    private int count;
    private DecimalFormat df = new DecimalFormat("####0.00");

    public WeatherForecast() {}


    // process the date string to extract all suitable variables
    public void getDateDetails() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime localDateTime = LocalDateTime.from(formatter.parse(this.getDt_iso()));
        this.month = localDateTime.getMonth().getValue();
        this.hour = localDateTime.getHour();
        this.day = localDateTime.getDayOfMonth();
        this.dayOfWeekExpanded = localDateTime.getDayOfWeek().toString().substring(0,1)
                + localDateTime.getDayOfWeek().toString().substring(1).toLowerCase();
        String dayOfWeek = localDateTime.getDayOfWeek().toString().substring(0, 3);
        this.day_of_week = dayOfWeek.substring(0,1) + dayOfWeek.substring(1,3).toLowerCase();
        this.year = localDateTime.getYear();


        if (this.month == 12 || this.month == 1 || this.month == 2)
            this.season = "Winter";
        else if (this.month >= 3 && this.month <=5)
            this.season = "Spring";
        else if (this.month >= 6 && this.month <=8)
            this.season = "Summer";
        else
            this.season = "Fall";


        if (this.hour >= 0 && this.hour <=5)
            this.day_period = "Night";
        else if (this.hour >= 6 && this.hour <=11)
            this.day_period = "Morning";
        else if (this.hour >= 12 && this.hour <=17)
            this.day_period = "Afternoon";
        else
            this.day_period = "Evening";


        if(this.day_of_week.equals("Sat") || this.day_of_week.equals("Sun"))
            this.type_day = "weekend";
        else
            this.type_day = "workday";
    }


    // getter and setter methods
    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getDayOfWeekExpanded() {
        return dayOfWeekExpanded;
    }

    public void setDayOfWeekExpanded(String dayOfWeekExpanded) {
        this.dayOfWeekExpanded = dayOfWeekExpanded;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }


    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getWeather_main() {
        return weather_main;
    }

    public void setWeather_main(String weather_main) {
        this.weather_main = weather_main;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getDay_period() {
        return day_period;
    }

    public void setDay_period(String day_period) {
        this.day_period = day_period;
    }

    public String getType_day() {
        return type_day;
    }

    public void setType_day(String type_day) {
        this.type_day = type_day;
    }

    public String getDt_iso() {
        return dt_iso;
    }

    public void setDt_iso(String dt_iso) {
        this.dt_iso = dt_iso;
    }

    public double getFeels_like() {
        return feels_like;
    }

    public void setFeels_like(double feels_like) {
        this.feels_like = Math.round((feels_like-273.15) * 100.0) / 100.0;
    }

    public double getHumidity() {
        return humidity;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

    public double getPressure() {
        return pressure;
    }

    public void setPressure(double pressure) {
        this.pressure = pressure;
    }

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = Math.round((temp-273.15) * 100.0) / 100.0;
    }

    public double getTemp_max() {
        return temp_max;
    }

    public void setTemp_max(double temp_max) {
        this.temp_max = Math.round((temp_max-273.15) * 100.0) / 100.0;
    }

    public double getTemp_min() {
        return temp_min;
    }

    public void setTemp_min(double temp_min) {
        this.temp_min = Math.round((temp_min-273.15) * 100.0) / 100.0;
    }

    public double getWind_speed() {
        return wind_speed;
    }

    public void setWind_speed(double wind_speed) {
        this.wind_speed = wind_speed;
    }

    public String toString() {
        return this.dt_iso + ": \n" + "month: " + this.month + " hour: " + this.hour + " dayofweek: " + this.day_of_week+
                " season" + this.season + " dayPeriod: " + this.day_period + " typeDay: " + this.type_day + " day: " + this.day;
    }
}
