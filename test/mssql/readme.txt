The tests in this project will run when the 'all' profile is used and when the database is online.

The project was developed against the Microsoft SQL Server for Linux database running on localhost in a Docker container.

To do this on your own machine execute the following steps:

1. Install Docker.

2. Pull the Docker image of SQL Server: 'docker pull microsoft/mssql-server-linux'

3. Start the database: 'docker run -d --name MySQL -e 'ACCEPT_EULA=Y' -e 'SA_PASSWORD=P@55w0rd' -p 1433:1433 microsoft/mssql-server-linux'. 

4. Connect to the database using your favorite client (e.g. DBeaver: http://dbeaver.jkiss.org)
hostname: localhost
port: 1433
username: sa
password: P@55w0rd
The complete JDBC URL is: jdbc:sqlserver://localhost:1433

5. You are now ready to run the tests. The database creation scripts will create a dedicated 'htt' schema that is dropped by the database drop scripts.
In the future this schema, as well as a dedicated user, could be created in the database startup.  