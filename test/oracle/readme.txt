The tests in this project will run when the 'all' profile is used and when the database is online.

The project was developed against the Oracle 12c database running on localhost in a Docker container.

To do this on your own machine execute the following steps:

1. Install Docker.

2. Pull the Docker image of the Oracle DB: 'docker pull sath89/oracle-12c'

3. Start the database: 'docker run -d -p 8080:8080 -p 1521:1521 sath89/oracle-12c'. 
This step takes a while the first time. Use 'docker logs' and specify the id of your docker container
to monitor the progress. When the logs say 'Database is ready to use' you can move to 
step 4.

4. Connect to the database using your favorite client (e.g. DBeaver: http://dbeaver.jkiss.org)
hostname: localhost
port: 1521
service name: xe.oracle.docker
username: system
password: oracle
The complete JDBC URL is: jdbc:oracle:thin:@//localhost:1521/xe.oracle.docker

5. Create the Hibernate Tools test ('HTT') database. You can do this by executing the following SQL: 

create user HTT
       identified by HTT
       default tablespace USERS
       temporary tablespace TEMP
       quota unlimited on USERS;
grant all privileges to HTT with admin option;

6. You can now connect with username 'HTT' and password 'HTT' and verify the existence of the 'HTT' schema.
If that is the case you are ready to run the tests. 