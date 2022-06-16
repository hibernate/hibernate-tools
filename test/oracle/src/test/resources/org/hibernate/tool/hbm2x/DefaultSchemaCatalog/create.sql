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
CREATE USER OVRTEST IDENTIFIED BY OVRTEST 
GRANT ALL PRIVILEGES TO OVRTEST WITH ADMIN OPTION
CREATE TABLE OVRTEST.CATMASTER ( ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID) )
CREATE TABLE OVRTEST.CATCHILD  ( CHILDID CHAR NOT NULL, MASTERREF CHAR, PRIMARY KEY (CHILDID), FOREIGN KEY (MASTERREF) REFERENCES OVRTEST.CATMASTER(ID) )
CREATE TABLE MASTER ( ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID) )
CREATE TABLE CHILD  ( CHILDID CHAR NOT NULL, MASTERREF CHAR, PRIMARY KEY (CHILDID), FOREIGN KEY (MASTERREF) REFERENCES MASTER(ID) )			
