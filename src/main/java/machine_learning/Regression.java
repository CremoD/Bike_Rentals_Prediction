package machine_learning;

import data_analysis.DataAnalyser;
import org.apache.spark.ml.Pipeline;
import org.apache.spark.ml.PipelineModel;
import org.apache.spark.ml.PipelineStage;
import org.apache.spark.ml.evaluation.RegressionEvaluator;
import org.apache.spark.ml.feature.*;
import org.apache.spark.ml.feature.OneHotEncoder;

import org.apache.spark.ml.regression.RandomForestRegressor;
import org.apache.spark.sql.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Regression {


    // train and evaluate a Random Forest Regressor to predict future forecasts
    public static ArrayList<WeatherForecast> trainAndPredictFuture(SparkSession ss, String inputPath) {
        Dataset<Row> rental_weather_dataset = ss.read().format("org.apache.spark.sql.execution.datasources.v2.csv.CSVDataSourceV2").option("header", true).option("inferSchema", true)
                .load(inputPath).drop("start_date");

        DataAnalyser dataAnalyser = new DataAnalyser();

        Dataset<Row> rental_weather_cleaned = dataAnalyser.weatherCleaner(dataAnalyser.timestampCleaner(rental_weather_dataset));

        rental_weather_cleaned.show();

        // keep the final columns for ML
        ArrayList<String> columnsForML = new ArrayList<>();


        // HANDLE CYCLIC FEATURES like hour or month (use sin and cos functions, since hour like 00 and 23 should be
        // near each other, the same for December and January
        rental_weather_cleaned = dataAnalyser.handleCyclicFeatures(rental_weather_cleaned);

        columnsForML.add("hour_sin");
        columnsForML.add("hour_cos");
        columnsForML.add("month_sin");
        columnsForML.add("month_cos");


        ////////////////////////////////////////////////////////////////////////////////////
        // create pipeline
        Pipeline pipeline = new Pipeline();
        ArrayList<PipelineStage> stages = new ArrayList<>();


        ////////////////////////////////////////////////////////////////////////////////////
        // string indexer + one hot encoding
        ////////////////////////////////////////////////////////////////////////////////////
        for (String column_name : Arrays.asList("day_of_week", "weather_main", "season", "day_period", "type_day")) {
            StringIndexer indexer = new StringIndexer()
                    .setInputCol(column_name)
                    .setOutputCol(column_name + "_label");

            OneHotEncoder encoder = new OneHotEncoder()
                    .setInputCol(column_name + "_label")
                    .setOutputCol(column_name + "_ohe");

            columnsForML.add(column_name + "_ohe");
            stages.add(indexer);
            stages.add(encoder);
        }

        ////////////////////////////////////////////////////////////////////////////////////
        // standard scaler to normalize numerical data
        ////////////////////////////////////////////////////////////////////////////////////
        VectorAssembler assembler = new VectorAssembler().setInputCols(new String[]{"feels_like", "humidity", "pressure", "temp", "temp_max", "temp_min", "wind_speed"}).setOutputCol("standardize_columns");
        StandardScaler scaler = new StandardScaler()
                .setInputCol("standardize_columns")
                .setOutputCol("numeric_scaled")
                .setWithStd(true)
                .setWithMean(true);

        columnsForML.add("numeric_scaled");
        stages.add(assembler);
        stages.add(scaler);


        ////////////////////////////////////////////////////////////////////////////////////
        // vector assembler to collect features
        ////////////////////////////////////////////////////////////////////////////////////
        VectorAssembler feature_assembler = new VectorAssembler().setInputCols(columnsForML.toArray(new String[columnsForML.size()])).setOutputCol("features");
        stages.add(feature_assembler);

        pipeline.setStages(stages.toArray(new PipelineStage[0]));
        PipelineModel pipmodel = pipeline.fit(rental_weather_cleaned);
        Dataset<Row> extracted_dataset = pipmodel.transform(rental_weather_cleaned);
        extracted_dataset = extracted_dataset.select("count", "features");
        extracted_dataset.show();


        ////////////////////////////////////////////////////////////////////////////////////
        // Start Machine Learning task!!
        ////////////////////////////////////////////////////////////////////////////////////

        // split in train and test for evaluation
        Dataset<Row>[] splits = extracted_dataset.randomSplit(new double[]{0.8, 0.2});
        Dataset<Row> trainingData = splits[0];
        Dataset<Row> testData = splits[1];


        // train a random forest regressior with 50 trees and max depth set to 10
        RandomForestRegressor rft = new RandomForestRegressor()
                .setLabelCol("count")
                .setFeaturesCol("features")
                .setNumTrees(50).setMaxDepth(10);


        // Chain random forest regressor in a Pipeline.
        Pipeline pipeline2 = new Pipeline()
                .setStages(new PipelineStage[]{rft});

        // train model
        PipelineModel model = pipeline2.fit(trainingData);

        // Make predictions on test set
        Dataset<Row> predictions = model.transform(testData);

        predictions.show();


        // Select (prediction, true label) and compute test error.
        RegressionEvaluator evaluator = new RegressionEvaluator()
                .setLabelCol("count")
                .setPredictionCol("prediction")
                .setMetricName("rmse");
        double rmse = evaluator.evaluate(predictions);
        System.out.println("Root Mean Squared Error (RMSE) on test data = " + rmse);



        /////////////////////////////////////////////////////////////////////////////////
        //////////////////////    PREDICT 5 days FORECAST     ///////////////////////////
        /////////////////////////////////////////////////////////////////////////////////

        // get weather forecast for 5 days from now
        ArrayList<WeatherForecast> forecasts_list = ForecastAnalyser.getForecastList();
        Dataset<Row> forecasts = ForecastAnalyser.getForecastDataset(forecasts_list, ss);

        // feature engineering on dataset
        forecasts = dataAnalyser.handleCyclicFeatures(forecasts);
        Dataset<Row> forecasts_ml = pipmodel.transform(forecasts);
        forecasts_ml = forecasts_ml.select("count", "features", "dt_iso");

        // Make predictions.
        Dataset<Row> forecasts_predictions = model.transform(forecasts_ml);

        forecasts_predictions.show();

        List<Row> results = forecasts_predictions.collectAsList();

        // update bike shares in the list of forecasts
        for (Row a : results) {
            for (WeatherForecast wf : forecasts_list) {
                if (wf.getDt_iso().equals(a.getString(a.fieldIndex("dt_iso"))))
                    wf.setCount((int)Math.round(a.getDouble(a.fieldIndex("prediction"))));

            }
        }

        // print results, for testing
        for (WeatherForecast b : forecasts_list) {
            System.out.println(b + "       ---> prediction:" + b.getCount());
        }


        return forecasts_list;
    }


}
