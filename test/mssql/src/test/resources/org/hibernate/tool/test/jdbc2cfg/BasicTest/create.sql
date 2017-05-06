create schema htt
create table htt.basic ( a int not null, name varchar(20), primary key (a)  )
create table htt.somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean bit )
create table htt.multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) ) 
