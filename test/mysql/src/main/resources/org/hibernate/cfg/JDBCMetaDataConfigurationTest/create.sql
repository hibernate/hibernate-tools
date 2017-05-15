create table withversion (first int, second int, version int, name varchar(256), primary key (first))
create table noversion (first int, second int, name varchar(256), primary key (second))
create table withrealtimestamp (first int, second int, timestamp timestamp, name varchar(256), primary key (first))
create table withfaketimestamp (first int, second int, timestamp int, name varchar(256), primary key (first))
