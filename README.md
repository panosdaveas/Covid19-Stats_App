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
Before inserting the project, the user must create the database.\
The database is created using Apache Derby JavaDB using the following driver :
```
org.apache.derby.jdbc.ClientDriver
```
Most probably your IDE already includes the driver but in any case it can be found here [Apache Derby](https://github.com/apache/derby.git).\
In order for the code to compile you need to name the DB like this :
```
"3hGE_lib_testPU"
```
or else you need to find the occurences in the source files\
and replace the name above with the one you chose.\
Right after, you can copy the contents of the [init-table-script](init-table-script.sql) file in a
database console and execute it.\
Upon the execution the necessary tables are created and you are ready to go!
# User manual
Currently the existing [User manual](User_guide.pdf) is written in greek,\
but an english version of it will be soon provided.
