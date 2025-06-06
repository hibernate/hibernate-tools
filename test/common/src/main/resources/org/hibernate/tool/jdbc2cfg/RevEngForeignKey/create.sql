############################################################################
# Hibernate Tools, Tooling for your Hibernate Projects                     #
#                                                                          #
# Copyright 2004-2025 Red Hat, Inc.                                        #
#                                                                          #
# Licensed under the Apache License, Version 2.0 (the "License");          #
# you may not use this file except in compliance with the License.         #
# You may obtain a copy of the License at                                  #
#                                                                          #
#     http://www.apache.org/licenses/LICENSE-2.0                           #
#                                                                          #
# Unless required by applicable law or agreed to in writing, software      #
# distributed under the License is distributed on an "AS IS" basis,        #
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. #
# See the License for the specific language governing permissions and      #
# limitations under the License.                                           #
############################################################################
CREATE TABLE PROJECT ( PROJECT_ID INT NOT NULL, NAME VARCHAR(50), TEAM_LEAD INT, PRIMARY KEY (PROJECT_ID) )
CREATE TABLE EMPLOYEE ( ID INT NOT NULL, NAME VARCHAR(50), MANAGER_ID INT, PRIMARY KEY (ID), CONSTRAINT EMPLOYEE_MANAGER FOREIGN KEY (MANAGER_ID) REFERENCES EMPLOYEE(ID))
CREATE TABLE WORKS_ON ( PROJECT_ID INT NOT NULL, EMPLOYEE_ID INT NOT NULL, START_DATE DATE, END_DATE DATE, PRIMARY KEY (PROJECT_ID, EMPLOYEE_ID), CONSTRAINT WORKSON_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE(ID), FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID) )
CREATE TABLE PERSON ( PERSON_ID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PERSON_ID) )
CREATE TABLE ADDRESS_PERSON ( ADDRESS_ID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (ADDRESS_ID), CONSTRAINT TO_PERSON FOREIGN KEY (ADDRESS_ID) REFERENCES PERSON(PERSON_ID))
CREATE TABLE MULTI_PERSON ( PERSON_ID INT NOT NULL, PERSON_COMPID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PERSON_ID, PERSON_COMPID) )
CREATE TABLE ADDRESS_MULTI_PERSON ( ADDRESS_ID INT NOT NULL, ADDRESS_COMPID INT NOT NULL, NAME VARCHAR(50), PRIMARY KEY (ADDRESS_ID, ADDRESS_COMPID), CONSTRAINT TO_MULTI_PERSON FOREIGN KEY (ADDRESS_ID, ADDRESS_COMPID) REFERENCES MULTI_PERSON(PERSON_ID, PERSON_COMPID))
ALTER TABLE PROJECT ADD CONSTRAINT PROJECT_MANAGER FOREIGN KEY (TEAM_LEAD) REFERENCES EMPLOYEE(ID)
