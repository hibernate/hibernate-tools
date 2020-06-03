/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2017-2020 Red Hat, Inc.
 *
 * Licensed under the GNU Lesser General Public License (LGPL), 
 * version 2.1 or later (the "License").
 * You may not use this file except in compliance with the License.
 * You may read the licence in the 'lgpl.txt' file in the root folder of 
 * project or obtain a copy at
 *
 *     http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
CREATE TABLE BASIC ( A INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY (A)  )
CREATE TABLE SOMECOLUMNSNOPK ( PK VARCHAR(25) NOT NULL, B CHAR, C INT NOT NULL )
CREATE TABLE MULTIKEYED ( ORDERID VARCHAR(10), CUSTOMERID VARCHAR(10), NAME VARCHAR(10), PRIMARY KEY(ORDERID, CUSTOMERID) )
CREATE USER OTHERSCHEMA IDENTIFIED BY OTHERSCHEMA 
GRANT ALL PRIVILEGES TO OTHERSCHEMA WITH ADMIN OPTION
CREATE TABLE OTHERSCHEMA.BASIC ( A INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY (A)  )