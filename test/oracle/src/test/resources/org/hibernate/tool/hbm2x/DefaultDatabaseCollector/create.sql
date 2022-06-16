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
CREATE USER "cat.cat" IDENTIFIED BY "cat.cat" 
GRANT ALL PRIVILEGES TO "cat.cat" WITH ADMIN OPTION
CREATE TABLE "cat.cat"."cat.master" (ID INT NOT NULL, TT INT, CONSTRAINT MASTER_PK PRIMARY KEY (ID))
CREATE TABLE "cat.cat"."cat.child" (CHILDID INT NOT NULL, MASTERREF INT, CONSTRAINT CHILD_PK PRIMARY KEY (CHILDID), CONSTRAINT MASTERREF_FK FOREIGN KEY (MASTERREF) references "cat.cat"."cat.master"(ID))