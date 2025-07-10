<!--
  ~ Copyright 2004 - 2025 Red Hat, Inc.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~     http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" basis,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

[![Hibernate](https://static.jboss.org/hibernate/images/hibernate_200x150.png)](https://tools.hibernate.org)

# Hibernate Tools Gradle : 5 Minute Tutorial

The best way to get to know the Hibernate Tools Gradle plugin is to start to use it. 
Hence we will provide a quick tutorial that gives you the first taste of it.
Before tackling this tutorial, make sure you have the [Gradle](https://gradle.org) build tool 
[installed](https://gradle.org/install/) and available on your machine.

## Create a Simple Initial Gradle Java Project

Let’s assume in this case that we start off with a very simple Gradle Java application
that we create from a command-line window with the instructions below. 

```shell
mkdir 5-minute-tutorial
cd 5-minute-tutorial
echo "" > settings.gradle
```

The last line above created an empty `settings.gradle` file. To use Gradle, we also need
a `build.gradle` file.

## Create the  `build.gradle` file

Add a `build.gradle` file to the `5-minute-tutorial` folder.

```shell
echo "" > build.gradle
```

Now edit the created emtpy file and add the following contents:

```groovy
plugins {
    id('application')
    id('org.hibernate.tool.hibernate-tools-gradle') version '7.0.3.Final'
}

repositories {
    mavenCentral()
}

dependencies {
    implementation('com.h2database:h2:2.3.232')
}
```

With this in place, we need to make sure that the Hibernate Tools Gradle plugin knows where
to find the database from which to generate the artefacts. This is done by spefifying the 
Hibernate properties in the file `hibernate.properties`.

## Specify the Hibernate Properties

For the purpose of this tutorial introduction, let's assume that you have a database running, e.g.
[H2 Sakila database](https://github.com/hibernate/sakila-h2) reacheable at the following JDBC URL:
`jdbc:h2:tcp://localhost/./sakila`.

With this set up, we create a `hibernate.properties` file in the `src/main/resources` folder
as this is the default location where the Hibernate Gradle plugin will look for this file.

```shell
mkdir -p src/main/resources
echo "" > src/main/resources/hibernate.properties
```

Now edit the `hibernate.properties` file so that it contains the properties as specified below.

```properties
hibernate.connection.driver_class=org.h2.Driver
hibernate.connection.url=jdbc:h2:tcp://localhost/./sakila
hibernate.connection.username=sa
hibernate.default_catalog=SAKILA
hibernate.default_schema=PUBLIC
```

Now we are ready to generate the entities.

## Run the Reverse Engineering

With all the previous elements in place, generating the Java classes from the Sakila database
becomes as simple as issuing `gradle generateJava` in your command line window.

```shell
> Task :generateJava
Starting Task 'generateJava'
Creating Java exporter
Loading the properties file : /Users/koen/temp/5-minute-tutorial/src/main/resources/hibernate.properties
Properties file is loaded
Starting Java export to directory: /Users/koen/temp/5-minute-tutorial/generated-sources...
...
Java export finished
Ending Task 'generateJava'
```


Congratulations! You have succesfully created Java classes for the Sakila database... Now it's
probably time to dive somewhat deeper in the available functionality.
