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

## Create a Gradle Java Project

Let’s assume in this case that we start off with a very simple default Gradle Java application
that we create from a command-line window with the instruction below. 

```
gradle init --type java-application --dsl groovy
```

Gradle will ask you some details about your application. The conversation is shown below
for completenes but of course you can make your own choices.

```
Enter target Java version (min: 7, default: 21): 

Project name (default: 5-minute-tutorial): 

Select application structure:
  1: Single application project
  2: Application and library project
Enter selection (default: Single application project) [1..2] 1

Select test framework:
  1: JUnit 4
  2: TestNG
  3: Spock
  4: JUnit Jupiter
Enter selection (default: JUnit Jupiter) [1..4] 4

Generate build using new APIs and behavior (some features may change in the next minor release)? (default: no) [yes, no] 


> Task :init
Learn more about Gradle by exploring our Samples at https://docs.gradle.org/8.13/samples/sample_building_java_applications.html

BUILD SUCCESSFUL in 19s
1 actionable task: 1 executed
```
