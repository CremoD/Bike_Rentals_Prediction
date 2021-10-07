package data_analysis;
import org.apache.spark.sql.*;
import static org.apache.spark.sql.functions.*;
import static org.apache.spark.sql.functions.col;

// class responsible for extract new features from datasets (clean timestamp and weather)
public class DataAnalyser {

    public DataAnalyser() {

    }

    public Dataset<Row> timestampCleaner(Dataset<Row> dataset) {
        // remove null values
        return dataset.na().drop().withColumn(
                // We cast variable as timestamp
                "dt_iso",to_timestamp(col("dt_iso"), "dd/MM/yyyy HH:mm")
        ).withColumn(
                // extract year
                "year", year(col("dt_iso"))
        ).withColumn(
                // extract month
                "month", month(col("dt_iso"))
        ).withColumn(
                // extract day
                "day", dayofmonth(col("dt_iso"))
        ).withColumn(
                // extract hour
                "hour", hour(col("dt_iso"))
        ).withColumn(
                // get season
                "season", when(col("month").isin("12", "1", "2"), "Winter")
                        .when(col("month").isin("3", "4", "5"), "Spring")
                        .when(col("month").isin("6", "7", "8"), "Summer")
                        .when(col("month").isin("9", "10", "11"), "Fall")
        ).withColumn(
                // get period of the day
                "day_period", when(col("hour").isin("0", "1", "2", "3", "4", "5"), "Night")
                .when(col("hour").isin("6", "7", "8", "9", "10", "11"), "Morning")
                .when(col("hour").isin("12", "13", "14", "15", "16", "17"), "Afternoon")
                .when(col("hour").isin("18", "19", "20", "21", "22", "23"), "Evening")
        ).withColumn(
                "type_day", when(col("weekend").equalTo(0), "workday").otherwise("weekend")
        );
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// FUNCTIONS FOR STATISTICS SUMMARY ///////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////


    // get number of bike used by day of the year
    public Dataset<Row> rentalsByYear(Dataset<Row> dataset) {
        // filter out 2015 since data is not complete
        return dataset.filter(col("year").isin("2012", "2013", "2014")).groupBy(col("year")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by day of the month
    public Dataset<Row> rentalsByMonth(Dataset<Row> dataset) {
        return dataset.groupBy(col("month")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by day of the week
    public Dataset<Row> rentalsByDay(Dataset<Row> dataset) {
        return dataset.groupBy(col("day_of_week")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by season
    public Dataset<Row> rentalsBySeason(Dataset<Row> dataset) {
        return dataset.groupBy(col("season")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by period of the day
    public Dataset<Row> rentalsByDayPeriod(Dataset<Row> dataset) {
        return dataset.groupBy(col("day_period")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by hour
    public Dataset<Row> rentalsByHour(Dataset<Row> dataset) {
        return dataset.groupBy(col("hour")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by weekend
    public Dataset<Row> rentalsByWeekend(Dataset<Row> dataset) {
        return dataset.groupBy(col("type_day")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by hour and weekend
    public Dataset<Row> rentalsByHourWeekend(Dataset<Row> dataset) {
        return dataset.groupBy(col("type_day"), col("hour")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by period and weekend
    public Dataset<Row> rentalsByPeriodWeekend(Dataset<Row> dataset) {
        return dataset.groupBy(col("type_day"), col("day_period")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by season and hour
    public Dataset<Row> rentalsBySeasonHour(Dataset<Row> dataset) {
        return dataset.groupBy(col("season"), col("hour")).agg(sum("count").as("bikes_rented"));
    }

    // get number of bike used by season and day period
    public Dataset<Row> rentalsBySeasonPeriod(Dataset<Row> dataset) {
        return dataset.groupBy(col("season"), col("day_period")).agg(sum("count").as("bikes_rented"));
    }


    // clean the weather columns and extract temperature range and wind range
    public Dataset<Row> weatherCleaner(Dataset<Row> dataset) {
        return dataset.withColumn(
                "temp_range", when(col("temp").lt(-10.0), "< -10°C")
                .when(col("temp").between(-10.0, 0.0), "-10°C - 0°C")
                .when(col("temp").between(0.0, 10.0), "0°C - 10°C")
                .when(col("temp").between(10.0, 20.0), "10°C - 20°C")
                .when(col("temp").between(20.0, 30.0), "20°C - 30°C")
                .when(col("temp").between(30.0, 40.0), "30°C - 40°C")
                .otherwise("> 40°C")
        ).withColumn(
                "wind_range", when(col("wind_speed").between(0.0,5.0), "0-5 m/s")
                .when(col("wind_speed").between(5.0,10.0), "5-10 m/s")
                .when(col("wind_speed").between(10.0,15.0), "10-15 m/s")
                .when(col("wind_speed").between(15.0,20.0), "15-20 m/s")
                .when(col("wind_speed").between(20.0,25.0), "20-25 m/s")
                .otherwise("> 25 m/s")
        );
    }

    // get number of bike used by period and weekend
    public Dataset<Row> rentalsByWeather(Dataset<Row> dataset) {
        return dataset.groupBy(col("weather_main")).agg(bround(avg("count"),2).as("bikes_rented"));
    }

    // get weather by day
    public Dataset<Row> weatherByDay(Dataset<Row> dataset) {
        return dataset.groupBy(col("weather_main"), col("day_of_week")).count();
    }

    // get number of bike used by season and hour
    public Dataset<Row> rentalsByTempRange(Dataset<Row> dataset) {
        return dataset.groupBy(col("temp_range")).agg(bround(avg("count"),2).as("bikes_rented"));
    }

    // get number of bike used by season and day period
    public Dataset<Row> rentalsByWindSpeed(Dataset<Row> dataset) {
        return dataset.groupBy(col("wind_range")).agg(bround(avg("count"),2).as("bikes_rented"));
    }

    // get number of bike used by season and weather
    public Dataset<Row> rentalsBySeasonWeather(Dataset<Row> dataset) {
        return dataset.groupBy(col("season"), col("weather_main")).agg(bround(avg("count"),2).as("bikes_rented"));
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////// FUNCTIONS FOR ML dataset preparation ///////////////////////////////////
    //////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Dataset<Row> handleCyclicFeatures(Dataset<Row> dataset){
        return dataset.withColumn(
                "hour_sin", sin(col("hour").multiply(2*Math.PI).divide(23))
        ).withColumn(
                "hour_cos", cos(col("hour").multiply(2*Math.PI).divide(23))
        ).withColumn(
                "month_sin", sin(col("month").multiply(2*Math.PI).divide(12))
        ).withColumn(
                "month_cos", cos(col("month").multiply(2*Math.PI).divide(12))
        );
    }
}
