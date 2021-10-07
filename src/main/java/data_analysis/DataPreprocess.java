package data_analysis;
import org.apache.spark.sql.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

// class responsible for preprocessing the rentals datasets and historical weather
public class DataPreprocess {
    private String inputDir, weatherPath;
    private SparkSession ss;

    // constructor of the class: setting the input directory, path of weather file and Spark session
    public DataPreprocess(String inputDir, String weatherPath, SparkSession ss) {
        this.inputDir = inputDir;
        this.weatherPath = weatherPath;
        this.ss = ss;
    }

    // getter and setter methods

    public String getInputDir() {
        return inputDir;
    }

    public void setInputDir(String inputDir) {
        this.inputDir = inputDir;
    }

    public String getWeatherPath() {
        return weatherPath;
    }

    public void setWeatherPath(String weatherPath) {
        this.weatherPath = weatherPath;
    }

    public SparkSession getSs() {
        return ss;
    }

    public void setSs(SparkSession ss) {
        this.ss = ss;
    }

    // read each file from directory, preprocess it and merge them to obtain a single dataset
    public Dataset<HourlyRental> mergeRentalStats() {
        File dir = new File(inputDir);
        String[] list_files = dir.list();

        // preprocess first dataset, use as base for the union of all datasets
        Dataset<Row> rental_stats = ss.read().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).option("inferSchema", true)
                .load("input_data/rental_stats/"+list_files[0]);
        Dataset<Row> rental_stats_adv = rental_stats.withColumn("Start Date",regexp_replace(col("Start Date"), ":\\d\\d", ":00")).groupBy("Start Date").count();

        // preprocess each file and merge it with the final one
        for (int i = 1; i < list_files.length; i++) {
            Dataset<Row> curr_rental_stats = ss.read().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).option("inferSchema", true)
                    .load("input_data/rental_stats/"+list_files[i]);
            Dataset<Row> curr_rental_stats_adv = curr_rental_stats.withColumn("Start Date",regexp_replace(col("Start Date"), ":\\d\\d", ":00")).groupBy("Start Date").count();
            rental_stats_adv = rental_stats_adv.union(curr_rental_stats_adv);
        }

        // rename column
        Dataset<Row> final_rental_stats = rental_stats_adv.withColumnRenamed("Start Date","start_date");

        // encode the dataset
        Encoder<HourlyRental> hourlyRentalEncoder = Encoders.bean(HourlyRental.class);
        Dataset<HourlyRental> rentalDataset= final_rental_stats.as(hourlyRentalEncoder);

        return rentalDataset;
    }

    // load and preprocess weather dataset
    public  Dataset<HourlyWeatherAdvanced> weatherPreprocessing() {
        Dataset<Row> weather_historical_data = ss.read().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).option("inferSchema", true)
                .load(weatherPath);

        Dataset<Row> weather_filtered = weather_historical_data.select("dt_iso", "temp", "feels_like", "temp_min", "temp_max", "humidity", "wind_speed", "weather_main", "pressure");
        Encoder<HourlyWeather> hourlyWeatherEncoder = Encoders.bean(HourlyWeather.class);
        Dataset<HourlyWeather> weatherDataset= weather_filtered.as(hourlyWeatherEncoder);

        // transform the dataset of hourlyWeather using another encoder, including a transformed date and adding way of the week and if it is weekend or not
        Dataset<HourlyWeatherAdvanced> advancedWeatherDataset = weatherDataset.map(p ->{
            HourlyWeatherAdvanced advancedHourlyWeather = new HourlyWeatherAdvanced();
            advancedHourlyWeather.setDt_iso(p.getDt_iso().substring(0, p.getDt_iso().lastIndexOf(":")).replaceAll("-", "/"));
            advancedHourlyWeather.setFeels_like(p.getFeels_like());
            advancedHourlyWeather.setHumidity(p.getHumidity());
            advancedHourlyWeather.setPressure(p.getPressure());
            advancedHourlyWeather.setTemp(p.getTemp());
            advancedHourlyWeather.setTemp_min(p.getTemp_min());
            advancedHourlyWeather.setTemp_max(p.getTemp_max());
            advancedHourlyWeather.setWind_speed(p.getWind_speed());
            advancedHourlyWeather.setWeather_main(p.getWeather_main());

            // get day of the week
            // first, get the date from the string
            Date date =new SimpleDateFormat("dd/MM/yyyy").parse(advancedHourlyWeather.getDt_iso().substring(0, advancedHourlyWeather.getDt_iso().indexOf(" ")));
            // then, get day of the week
            SimpleDateFormat simpleDF = new SimpleDateFormat("E", Locale.ENGLISH);
            String dayOfWeek = simpleDF.format(date);
            advancedHourlyWeather.setDay_of_week(dayOfWeek);

            // set 1 if day of the week is saturday or sunday, 0 otherwise
            if (dayOfWeek.equals("Sun") || dayOfWeek.equals("Sat"))
                advancedHourlyWeather.setWeekend(1);
            else
                advancedHourlyWeather.setWeekend(0);

            return advancedHourlyWeather;

        }, Encoders.bean(HourlyWeatherAdvanced.class));

        return advancedWeatherDataset;
    }


    // join rental dataset preprocessed with weather dataset preprocessed
    public Dataset<Row> rentalWeatherJoined() {
        Dataset<HourlyRental> rentalDataset = this.mergeRentalStats();
        Dataset<HourlyWeatherAdvanced> advancedWeatherDataset = this.weatherPreprocessing();
        return advancedWeatherDataset.join(rentalDataset, advancedWeatherDataset.col("dt_iso").equalTo(rentalDataset.col("start_date")));
    }
}
