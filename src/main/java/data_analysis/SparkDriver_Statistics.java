package data_analysis;

import org.apache.spark.sql.*;

public class SparkDriver_Statistics {

    public static void main(String[] args) {

        String inputPath = args[0];


        // Create a Spark Session object and set the name of the application
        SparkSession ss = SparkSession.builder().appName("London Bike Rentals: Statistics").getOrCreate();

        Dataset<Row> rental_weather_dataset = ss.read().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).option("inferSchema", true)
                .load(inputPath).drop("start_date");

        DataAnalyser dm = new DataAnalyser();

        //////////////////////////////////////////////////////////////////
        //                         TIME ANALYSIS                        //
        //////////////////////////////////////////////////////////////////

        // analyse timestamp and extract additional features from timestamp
        Dataset<Row> timestamp_extracted = dm.timestampCleaner(rental_weather_dataset);

        // ANALYSIS: get number of bike used by year
        Dataset<Row> rentalsByYear = dm.rentalsByYear(timestamp_extracted);
        rentalsByYear.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/year");

        // ANALYSIS: get number of bike used by day of the month
        Dataset<Row> rentalsByMonth = dm.rentalsByMonth(timestamp_extracted);
        rentalsByMonth.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/month");

        // ANALYSIS: get number of bike used by day of the week
        Dataset<Row> rentalsByDay = dm.rentalsByDay(timestamp_extracted);
        rentalsByDay.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/dayWeek");

        // get number of bike used by season
        Dataset<Row> rentalsBySeason = dm.rentalsBySeason(timestamp_extracted);
        rentalsBySeason.show();

        // get number of bike used by period of the day
        Dataset<Row> rentalsByDayPeriod = dm.rentalsByDayPeriod(timestamp_extracted);
        rentalsByDayPeriod.show();

        // get number of bike used by hour
        Dataset<Row> rentalsByHour = dm.rentalsByHour(timestamp_extracted);
        rentalsByHour.show();

        // get number of bike used by weekend
        Dataset<Row> rentalsByWeekend = dm.rentalsByWeekend(timestamp_extracted);
        rentalsByWeekend.show();

        // get number of bike used by hour and weekend
        Dataset<Row> rentalsByHourWeekend = dm.rentalsByHourWeekend(timestamp_extracted);
        rentalsByHourWeekend.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/weekend_hour");

        // get number of bike used by period and weekend
        Dataset<Row> rentalsByPeriodWeekend = dm.rentalsByPeriodWeekend(timestamp_extracted);
        rentalsByPeriodWeekend.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/weekend_period");

        // get number of bike used by season and hour
        Dataset<Row> rentalsBySeasonHour = dm.rentalsBySeasonHour(timestamp_extracted);
        rentalsBySeasonHour.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/season_hour");

        // get number of bike used by season and period
        Dataset<Row> rentalsBySeasonPeriod = dm.rentalsBySeasonPeriod(timestamp_extracted);
        rentalsBySeasonPeriod.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/season_period");


        //////////////////////////////////////////////////////////////////
        //                         WEATHER ANALYSIS                     //
        //////////////////////////////////////////////////////////////////
        Dataset<Row> weather_processed = dm.weatherCleaner(timestamp_extracted);
        weather_processed.show();

        // get weather by day
        Dataset<Row> weatherByDay = dm.weatherByDay(weather_processed);
        weatherByDay.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/weather_day");

        // get number of bike used by weather
        Dataset<Row> rentalsByWeather = dm.rentalsByWeather(weather_processed);
        rentalsByWeather.show();

        // get number of bike used by temperature range
        Dataset<Row> rentalsByTemp = dm.rentalsByTempRange(weather_processed);
        rentalsByTemp.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/temp");


        // get number of bike used by wind_speed
        Dataset<Row> rentalsByWind = dm.rentalsByWindSpeed(weather_processed);
        rentalsByWind.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/wind");

        // get number of bike used by season and weather
        Dataset<Row> rentalsBySeasonWeather = dm.rentalsBySeasonWeather(weather_processed);
        rentalsBySeasonWeather.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/season_weather");



        // Close the Spark context
        ss.stop();
    }
}

