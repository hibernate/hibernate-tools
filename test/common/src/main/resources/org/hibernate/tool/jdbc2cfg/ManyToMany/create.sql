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
CREATE TABLE PROJECT (PROJECT_ID INTEGER NOT NULL, NAME VARCHAR(50), PRIMARY KEY (PROJECT_ID) )
CREATE TABLE EMPLOYEE (ID INTEGER NOT NULL, NAME VARCHAR(50), MANAGER_ID INTEGER, PRIMARY KEY (ID), CONSTRAINT EMPLOYEE_MANAGER FOREIGN KEY (MANAGER_ID) REFERENCES EMPLOYEE(ID))
CREATE TABLE WORKS_ON (PROJECT_ID INTEGER NOT NULL, EMPLOYEE_ID INTEGER NOT NULL, PRIMARY KEY (PROJECT_ID, EMPLOYEE_ID), CONSTRAINT WORKSON_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE(ID), FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID) )
CREATE TABLE WORKS_ON_CONTEXT (PROJECT_ID INTEGER NOT NULL, EMPLOYEE_ID INTEGER NOT NULL, CREATED_BY INTEGER, PRIMARY KEY (PROJECT_ID, EMPLOYEE_ID), CONSTRAINT WORKSON_CTX_EMPLOYEE FOREIGN KEY (EMPLOYEE_ID) REFERENCES EMPLOYEE(ID), FOREIGN KEY (PROJECT_ID) REFERENCES PROJECT(PROJECT_ID), FOREIGN KEY (CREATED_BY) REFERENCES EMPLOYEE(ID) )
CREATE TABLE LEFT_TABLE ( id INTEGER NOT NULL, PRIMARY KEY (ID) )
CREATE TABLE RIGHT_TABLE ( id INTEGER NOT NULL, PRIMARY KEY (ID) )
CREATE TABLE NON_MIDDLE ( left_id INTEGER NOT NULL, RIGHT_ID INTEGER NOT NULL, PRIMARY KEY (LEFT_ID), CONSTRAINT FK_MIDDLE_LEFT FOREIGN KEY (LEFT_ID) REFERENCES LEFT_TABLE(ID), CONSTRAINT FK_MIDDLE_RIGHT FOREIGN KEY (RIGHT_ID) REFERENCES RIGHT_TABLE(ID))
