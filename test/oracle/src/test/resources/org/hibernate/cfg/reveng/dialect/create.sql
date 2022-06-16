/*
 * Hibernate Tools, Tooling for your Hibernate Projects
 * 
 * Copyright 2018-2020 Red Hat, Inc.
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
CREATE TABLE person (  id              NUMBER(2) PRIMARY KEY,  a_varchar2_char VARCHAR2(10 CHAR),  a_varchar2_byte VARCHAR2(10 BYTE),  a_varchar_char  VARCHAR(10 CHAR),  a_varchar_byte  VARCHAR(10 BYTE),  a_nvarchar      NVARCHAR2(10),  a_char_char     CHAR(10 CHAR),  a_char_byte     CHAR(10 BYTE),  a_nchar_char    NCHAR(10),  a_nchar_byte    NCHAR(10),  a_number_int    NUMBER(10),  a_number_dec    NUMBER(10, 2),  a_float         FLOAT(10))