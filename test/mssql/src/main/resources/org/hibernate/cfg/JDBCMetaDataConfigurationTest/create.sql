create schema htt
create table htt.withversion (first int, second int, version int, name varchar(256), primary key (first))
create table htt.noversion (first int, second int, name varchar(256), primary key (second))
create table htt.withrealtimestamp (first int, second int, timestamp timestamp, name varchar(256), primary key (first))
create table htt.withfaketimestamp (first int, second int, timestamp int, name varchar(256), primary key (first))
