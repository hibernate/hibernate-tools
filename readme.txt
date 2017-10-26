Development
-----------
The project is developed under the form of a hierarchical multimodule Maven project. 
After cloning the git repository to your local machine you can import it as such into 
your favorite IDE. 
However in order to compile and run the tests one additional step is required: you need
to download the Oracle JDBC driver (currently we are using ojdbc8.jar) and use the Maven 
command to install it into your local repository:

    mvn install:install-file 
        -Dfile=/path/to/local/ojdbc8.jar 
        -DgroupId=com.oracle.jdbc 
        -DartifactId=ojdbc8 
        -Dversion=12.2.0.1 
        -Dpackaging=jar
        