<!--
  ~ Hibernate Tools, Tooling for your Hibernate Projects
  ~
  ~ Copyright 2020 Red Hat, Inc.
  ~
  ~ Licensed under the GNU Lesser General Public License (LGPL), 
  ~ version 2.1 or later (the "License").
  ~ You may not use this file except in compliance with the License.
  ~ You may read the licence in the 'lgpl.txt' file in the root folder of 
  ~ project or obtain a copy at
  ~
  ~     http://www.gnu.org/licenses/lgpl-2.1.html
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" basis,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  -->

[![Hibernate](https://static.jboss.org/hibernate/images/hibernate_200x150.png)](https://tools.hibernate.org)

# Hibernate Tools - Tooling for your Hibernate Projects

Working with Hibernate is very easy and developers enjoy using the APIs and the query language. Even creating mapping metadata is not an overly complex task once you've mastered the basics. Hibernate Tools makes working with Hibernate or JPA even more pleasant.

## Project Contents

Hibernate Tools is developed under the form of a hierarchical multi module Maven project. This parent module contains the following child modules:

* [**hibernate-tools-orm:**](./orm) 
This module contains among others the base tools to reverse engineer Hibernate artifacts from an existing database. There is a default implementation for the most current Hibernate and JPA artifacts but an API allows you to extend it to other artifacts and develop your own templates should this be needed.  
* [**hibernate-tools-ant**:](./ant)
The hibernate-tools-ant module makes use of the API defined in the hibernate-tools-orm module to wrap these tools in a number of Ant tasks to be used in your Ant build scripts.
* [**hibernate-tools-maven**:](./maven)
Just like the hibernate-tools-ant module this module uses the tools defined in hibernate-tools-orm to create some Maven mojos that bring the reverse engineering power to your Maven build.
* [**hibernate-tools-utils**:](./utils)
This module contains a few general purpose utilities that are used in the other modules.
* [**hibernate-tools-test**:](./test)
The test module is a multi module in itself that contains tests targeting different databases as well as no database tests.

## Contributing

If you run into errors, have ideas on how to improve the project or if you just want to collaborate, checkout the [contribution guide](./contribute.md).