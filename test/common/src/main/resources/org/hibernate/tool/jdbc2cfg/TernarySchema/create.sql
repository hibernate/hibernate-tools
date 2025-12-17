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
CREATE SCHEMA OTHERSCHEMA
CREATE SCHEMA THIRDSCHEMA
    CREATE TABLE PUBLIC.MEMBER ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
    CREATE TABLE OTHERSCHEMA.ROLE ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
    CREATE TABLE THIRDSCHEMA.MEMBERROLE ( MEMBERID INT NOT NULL, ROLEID INT NOT NULL, PRIMARY KEY(MEMBERID, ROLEID))
ALTER TABLE THIRDSCHEMA.MEMBERROLE ADD CONSTRAINT TOROLES FOREIGN KEY (ROLEID) REFERENCES OTHERSCHEMA.ROLE(ID)
ALTER TABLE THIRDSCHEMA.MEMBERROLE ADD CONSTRAINT TOUSERS FOREIGN KEY (MEMBERID) REFERENCES PUBLIC.MEMBER(ID)
CREATE TABLE PLAINROLE ( ID INT NOT NULL, NAME VARCHAR(20), PRIMARY KEY(ID))
CREATE TABLE PLAINMEMBERROLE ( MEMBERID INT NOT NULL, ROLEID INT NOT NULL, PRIMARY KEY(MEMBERID, ROLEID))
ALTER TABLE PLAINMEMBERROLE ADD CONSTRAINT PLAINTOROLES FOREIGN KEY (ROLEID) REFERENCES PUBLIC.PLAINROLE(ID)
ALTER TABLE PLAINMEMBERROLE ADD CONSTRAINT PLAINTOMEMBERS FOREIGN KEY (MEMBERID) REFERENCES PUBLIC.MEMBER(ID)