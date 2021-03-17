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
Upon the execution the necessary tables are created and you are ready to go.\
Mind that the time needed to download the data depends on each user's hardware,\
but a data fetch will take approximately 10 seconds in modern systems.
# GUI
The user can toggle between projecting the feched data in a\
line chart, XML grid or in Google Maps.\
The graphical interface is created with the notion to be\
rather simple and straightforward and here are some screenshots of it :
<img width="909" alt="Screenshot 2021-03-14 at 5 42 46 PM" src="https://user-images.githubusercontent.com/63146477/111530889-860abb00-876c-11eb-8626-d46492d68061.png">
# User manual
Currently the existing [User manual](User_guide.pdf) is written in greek,\
but an english version of it will be soon provided.
