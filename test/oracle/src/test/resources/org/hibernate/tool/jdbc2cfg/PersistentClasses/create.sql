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
CREATE TABLE ORDERS (ID NUMBER(8) NOT NULL, NAME VARCHAR(20), PRIMARY KEY (ID))
CREATE TABLE ITEM  (CHILD_ID NUMBER(8) NOT NULL, NAME VARCHAR(50), ORDER_ID NUMBER, RELATED_ORDER_ID NUMBER, PRIMARY KEY (CHILD_ID), FOREIGN KEY (ORDER_ID) REFERENCES ORDERS(ID), FOREIGN KEY (RELATED_ORDER_ID) REFERENCES ORDERS(ID) )
