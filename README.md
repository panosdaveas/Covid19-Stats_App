# Covid19-Stats_App
A client app to the service [covid2019-api](https://covid2019-api.herokuapp.com/)
that pulls the data provided in a local database.\
The data include the following kinds (noted as timeseries) : 
```
confirmed, deaths, recovered
```
Through the app's GUI, the user can manage the downloaded data\
and choose between a number of statistical projections.
# Database
Before inserting the project, the user must create the database.
The database is created using Apache Derby JavaDB using the following driver :
```
org.apache.derby.jdbc.ClientDriver
```
that can be found here [Apache Derby](https://github.com/apache/derby.git).\
Right after, you can copy the contents of the [init-table-script]() file in a\
database console and execute it.
