<!--
  ~ Copyright 2016 - 2020 Red Hat, Inc.
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
Hibernate Tools Maven Plugin
============================
Version: 6.0.0, January 2016

What is it
----------
A Maven Plugin to generate JPA Entities from an existing database using hibernate-tools.

Development status
------------------
Currently the plugin allows database reverse engineering configurations (JDBCMetaDataConfiguration)
to export JPA Entities (hbm2java) and Schema Files (hbm2ddl).

History
-------
The hibernate-tools-maven-plugin was originally developed by Jacques Stadler
in the following repository and was based on version 1.1.0:
https://github.com/stadler/hibernate-tools-maven-plugin

Licensing
---------
This software is distributed under the terms of the FSF Lesser Gnu Public
License (see lgpl.txt).

Maven Plugin Site
-----------------
TODO: To be migrated...
https://stadler.github.io/hibernate-tools-maven-plugin/

Usage
-----
TODO: To be migrated...
An example project using this plugin can be found here:
https://github.com/stadler/hibernate-tools-maven-plugin-sample

The plugin declaration may look as follows:
```
    <plugin>
        <groupId>com.github.stadler</groupId>
        <artifactId>hibernate-tools-maven-plugin</artifactId>
        <version>${hibernate-tools-maven-plugin.version}</version>
        <executions>
            <execution>
                <id>Display Help</id>
                <phase>validate</phase>
                <goals>
                    <goal>help</goal>
                </goals>
            </execution>
            <execution>
                <id>Entity generation</id>
                <phase>generate-sources</phase>
                <goals>
                    <goal>hbm2java</goal>
                </goals>
                <configuration>
                    <templatePath>${project.basedir}/src/main/resources/templates/</templatePath>
                    <!-- Defaults: -->
                    <outputDirectory>${project.build.directory}/generated-sources/</outputDirectory>
                    <ejb3>false</ejb3>
                    <jdk5>false</jdk5>
                </configuration>
            </execution>
            <execution>
                <id>Schema generation</id>
                <phase>generate-resources</phase>
                <goals>
                    <goal>hbm2ddl</goal>
                </goals>
                <configuration>
                    <!--Possible targetType: SCRIPT (default), STDOUT, DATABASE-->
                    <targetTypes>
                        <param>SCRIPT</param>
                        <param>STDOUT</param>
                        <param>DATABASE</param>
                    </targetTypes>
                    <!-- Defaults:-->
                    <outputDirectory>${project.build.directory}/generated-resources/</outputDirectory>
                     <!--Possible schemaExportAction: CREATE (default), DROP, BOTH-->
                    <schemaExportAction>CREATE</schemaExportAction>
                    <outputFileName>schema.ddl</outputFileName>
                    <delimiter>;</delimiter>
                    <haltOnError>true</haltOnError>
                    <format>true</format>
                </configuration>
            </execution>
        </executions>
        <configuration>
            <revengFile>${project.basedir}/src/main/hibernate/hibernate.reveng.xml</revengFile>
            <!-- Defaults:-->
            <packageName></packageName>
            <configFile>${project.basedir}/src/main/hibernate/hibernate.cfg.xml</configFile>
            <detectManyToMany>true</detectManyToMany>
            <detectOneToOne>true</detectOneToOne>
            <detectOptimisticLock>true</detectOptimisticLock>
            <createCollectionForForeignKey>true</createCollectionForForeignKey>
            <createManyToOneForForeignKey>true</createManyToOneForForeignKey>
        </configuration>
        <dependencies>
            <dependency>
                <!-- DB Driver of your choice -->
                <groupId>com.h2database</groupId>
                <artifactId>h2</artifactId>
                <version>${h2.version}</version>
            </dependency>
        </dependencies>
    </plugin>
</plugins>
```
