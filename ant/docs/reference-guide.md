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

# Hibernate Tools Ant : Reference Guide

## 1. The `<hibernatetool>` Task

### 1.1 `<hibernatetool>` task definition
To use the Ant tasks you need to have the `<hibernatetool>` task defined. 
That is done in your `build.xml` file by inserting the following XML.

```xml
<ivy:cachepath organisation="org.hibernate.tool" module="hibernate-tools-ant" revision="${hibernate-tools.version}"
               pathid="hibernate-tools.path" inline="true"/>
<ivy:cachepath organisation="${jdbc-driver.org}" module="${jdbc-driver.module}" revision="${jdbc-driver.version}"
               pathid="jdbc-driver.path" inline="true"/>

<path id="classpath">
    <path refid="hibernate-tools.path"/>
    <path refid="jdbc-driver.path"/>
</path>


<taskdef name="hibernatetool"
         classname="org.hibernate.tool.ant.HibernateToolTask"
         classpathref="classpath" />
```
The `<taskdef>`in the snippet above defines an Ant task called `hibernatetool` 
which now can be used anywhere in your `build.xml` file.

The snippet above also uses [Apache Ivy](https://ant.apache.org/ivy/) to handle the library dependencies. 
Of course you could also explicitly handle these dependencies yourself in the `build.xml` 
file. In addition, you will need to define properties (or replace the variables) for
the jdbc driver and for the version information. See an example in the snippet below:

```xml
<property name="hibernate-tools.version" value="7.0.0.Final"/>
<property name="jdbc-driver.org" value="com.h2database"/>
<property name="jdbc-driver.module" value="h2"/>
<property name="jdbc-driver.version" value="2.3.232"/>
```


