# London bike rentals prediction model

## Overview
University project that aims at exploiting Data Analysis tools and methods, performing an analysis of past bike sharing services in the city of London and realizing a regression model for the prediction of future bike rentals. The project uses Spark as primary technology, IntelliJ as IDE and Java as programming language.

## Motivation
Nowadays, as urbanization reaches unprecedented levels, road congestions and air pollution have become serious issues. Among the many solutions that have emerged in recent years, perhaps the most promising is represented by bike sharing. To be clear, bike sharing refers to rental schemes, whereby civilians can pick up, ride and drop off bicycles at numerous points across the city.

Bike sharing not only represents one of the most interesting and promising method of reducing city traffic, but it can also benefit the economy (impact on businesses and neighbourhood, reduced expenditure on healthcare, as stated in this [article](https://medium.com/urbansharing/the-economic-benefits-of-bike-sharing-f69c230e5a9d) and the personal health of the customers (as stated in this research [paper](https://www.sciencedirect.com/science/article/pii/S0160412017321566).

Starting from this good points, London is one of the cities most willing to use the bike rental system as a solution for air pollution and road congestions. Therefore, for this project we imagine a scenario in which public authorities and bike rentals service managers would like to monitor past data in order to obtain useful information to improve the system, involving more and more citizens and tourists and improve the general living conditions of the city. In addition, by identifying possible patterns in the use of the service, a program that can give an estimate of the number of bikes that will be rented in the short term, based on information about the day and weather forecast, could be developed.

For this purpose, this project aims at realizating such analysis and software for the forecasting of future rentals. the data regarding the number of bikes that were rented in London (from January 2012 to January 2015) are analyzed. Moreover, the work will be based on the hypothesis that the number of bike rentals are related to some variables like the hour of the day, period of the year and weather condition, and I will try to train a Machine Learning model to try to predict the future bike shares.

The analysis of past data, the discovery of some pattern and the predictions of the future use could help the bike sharing general management (advertising, incentives or discounts for improve low bike shares, take full advantage of predicted high shares in a specific period of the year, improve service and bike availability, and many other useful management choices). Some questions that the city administration may want to have answered, and that will be answered by statistical analysis of past data are:
- Are bike shares influenced by the type of day and period?
- How are bike shares distributed over the hours?
- How is the request for bike rentals during the periods of the year?
- How are bike shares distributed over weather condition?
- How do temperature and wind affect the number of bike rentals?

The detailed report can be consulted [here](Project_Report.pdf).

## Data
- Data concerning the bike rentals usage in the city of London is retrieved from the government [data source](https://cycling.data.tfl.gov.uk)
- Data concerning the historical weather conditions regarding the city of London is retrieved from [OpenWeatherMap.org](https://openweathermap.org/history-bulk)



