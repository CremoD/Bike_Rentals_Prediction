package machine_learning;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.Metadata;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.ArrayList;
import java.util.List;

public class ForecastAnalyser {
    private static final String api_key = "8b68074189987f8b317ecb78546e0171";
    private static final String location = "London";
    private static final String url_string = "http://api.openweathermap.org/data/2.5/forecast?q="+location+"&appid=" + api_key;

    // make request to website and return the response as JSON array
    public static JSONArray getForecast() {
        URL url = null;

        try {
            url = new URL(url_string);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));

            StringBuilder response = new StringBuilder();
            String responseLine = null;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine);
            }

            //System.out.println(response.toString());

            JSONObject obj = new JSONObject(response.toString());
            JSONArray arr = obj.getJSONArray("list");

            return arr;



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JSONArray();

    }

    // return the list of WeatherForecast objects extracted from JSON response of OpenWeatherMap.org API
    public static ArrayList<WeatherForecast> getForecastList() {
        ArrayList<WeatherForecast> forecast_list = new ArrayList<WeatherForecast>();

        JSONArray results = ForecastAnalyser.getForecast();

        for(int i =0; i<results.length();i++) {
            WeatherForecast hourly_forecast = new WeatherForecast();
            hourly_forecast.setDt_iso(results.getJSONObject(i).getString("dt_txt"));
            hourly_forecast.setTemp(results.getJSONObject(i).getJSONObject("main").getDouble("temp"));
            hourly_forecast.setFeels_like(results.getJSONObject(i).getJSONObject("main").getDouble("feels_like"));
            hourly_forecast.setTemp_min(results.getJSONObject(i).getJSONObject("main").getDouble("temp_min"));
            hourly_forecast.setTemp_max(results.getJSONObject(i).getJSONObject("main").getDouble("temp_max"));
            hourly_forecast.setHumidity(results.getJSONObject(i).getJSONObject("main").getDouble("humidity"));
            hourly_forecast.setPressure(results.getJSONObject(i).getJSONObject("main").getDouble("pressure"));
            hourly_forecast.getDateDetails();
            hourly_forecast.setWeather_main(results.getJSONObject(i).getJSONArray("weather").getJSONObject(0).getString("main"));
            hourly_forecast.setWind_speed(results.getJSONObject(i).getJSONObject("wind").getDouble("speed"));
            hourly_forecast.setCount(-1);

            forecast_list.add(hourly_forecast);
        }

        return forecast_list;
    }

    // from the list of weather forecasts objects, create manually a dataframe
    public static Dataset<Row> getForecastDataset(ArrayList<WeatherForecast> forecast_list, SparkSession ss) {

        List<Row> data = new ArrayList<Row>();

        for (WeatherForecast wf : forecast_list) {
            data.add(RowFactory.create(-1, wf.getDay_of_week(), wf.getFeels_like(), wf.getTemp(),
                    wf.getTemp_max(), wf.getTemp_min(), wf.getHumidity(), wf.getPressure(), wf.getWind_speed(),
                    wf.getWeather_main(), wf.getMonth(), wf.getHour(), wf.getSeason(), wf.getDay_period(), wf.getType_day(), wf.getDt_iso()));
        }

        StructType schema = new StructType(new StructField[]{
                new StructField("count", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("day_of_week", DataTypes.StringType, false, Metadata.empty()),
                new StructField("feels_like", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("temp", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("temp_max", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("temp_min", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("humidity", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("pressure", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("wind_speed", DataTypes.DoubleType, false, Metadata.empty()),
                new StructField("weather_main", DataTypes.StringType, false, Metadata.empty()),
                new StructField("month", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("hour", DataTypes.IntegerType, false, Metadata.empty()),
                new StructField("season", DataTypes.StringType, false, Metadata.empty()),
                new StructField("day_period", DataTypes.StringType, false, Metadata.empty()),
                new StructField("type_day", DataTypes.StringType, false, Metadata.empty()),
                new StructField("dt_iso", DataTypes.StringType, false, Metadata.empty())
        });

        Dataset<Row> forecasts = ss.createDataFrame(data, schema);

        return forecasts;

    }

}
