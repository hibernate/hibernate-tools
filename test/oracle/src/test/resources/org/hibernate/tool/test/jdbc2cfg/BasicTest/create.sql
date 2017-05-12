create table basic ( a int not null, name varchar(20), primary key (a)  )
create table somecolumnsnopk ( pk varchar(25) not null, b char, c int not null, aBoolean number(1) not null )
create table multikeyed ( orderid varchar(10), customerid varchar(10), name varchar(10), primary key(orderid, customerid) ) 
