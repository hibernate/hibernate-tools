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
CREATE SCHEMA HTT
CREATE SCHEMA OTHERSCHEMA 
CREATE SCHEMA THIRDSCHEMA 
CREATE TABLE HTT.USERS ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
CREATE TABLE OTHERSCHEMA.ROLE ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
CREATE TABLE THIRDSCHEMA.USERROLES ( USERID INT NOT NULL, ROLEID INT NOT NULL, PRIMARY KEY(USERID, ROLEID))
ALTER TABLE THIRDSCHEMA.USERROLES ADD CONSTRAINT TOROLES FOREIGN KEY (ROLEID) REFERENCES OTHERSCHEMA.ROLE(ID)
ALTER TABLE THIRDSCHEMA.USERROLES ADD CONSTRAINT TOUSERS FOREIGN KEY (USERID) REFERENCES HTT.USERS(ID)
CREATE TABLE HTT.PLAINROLE ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
CREATE TABLE HTT.PLAINUSERROLES ( USERID INT NOT NULL, ROLEID INT NOT NULL, PRIMARY KEY(USERID, ROLEID))
ALTER TABLE HTT.PLAINUSERROLES ADD CONSTRAINT PLAINTOROLES FOREIGN KEY (ROLEID) REFERENCES HTT.PLAINROLE(ID)
ALTER TABLE HTT.PLAINUSERROLES ADD CONSTRAINT PLAINTOUSERS FOREIGN KEY (USERID) REFERENCES HTT.USERS(ID)