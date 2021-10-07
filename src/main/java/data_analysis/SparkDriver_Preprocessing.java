package data_analysis;

import org.apache.spark.sql.*;

public class SparkDriver_Preprocessing {

    public static void main(String[] args) {

        // specify input directory for the 52 rental files and input path for historical weather
        String inputDir = "input_data/rental_stats";
        String weatherPath = "input_data/london_history_weather.csv";


        // Create a Spark Session object and set the name of the application
        SparkSession ss = SparkSession.builder().appName("London Bike Rentals: Preprocessing").getOrCreate();

        // create an instance of the class that is responsible for preprocessing the datasets
        DataPreprocess dp = new DataPreprocess(inputDir, weatherPath, ss);

        // rental and weather joined
        Dataset<Row> rental_weather = dp.rentalWeatherJoined();

        // write dataset in csv format
        rental_weather.repartition(1).write().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).save("output/preprocess");

        // Close the Spark context
        ss.stop();
    }
}

