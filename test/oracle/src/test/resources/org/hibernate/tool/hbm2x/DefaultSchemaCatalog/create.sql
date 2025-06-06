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
CREATE USER OVRTEST IDENTIFIED BY OVRTEST 
GRANT ALL PRIVILEGES TO OVRTEST WITH ADMIN OPTION
CREATE TABLE OVRTEST.CATMASTER ( ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID) )
CREATE TABLE OVRTEST.CATCHILD  ( CHILDID CHAR NOT NULL, MASTERREF CHAR, PRIMARY KEY (CHILDID), FOREIGN KEY (MASTERREF) REFERENCES OVRTEST.CATMASTER(ID) )
CREATE TABLE MASTER ( ID CHAR NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID) )
CREATE TABLE CHILD  ( CHILDID CHAR NOT NULL, MASTERREF CHAR, PRIMARY KEY (CHILDID), FOREIGN KEY (MASTERREF) REFERENCES MASTER(ID) )			
