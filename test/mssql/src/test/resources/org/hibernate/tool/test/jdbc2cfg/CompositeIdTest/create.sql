create schema htt
create table htt.SimpleLineItem (lineItemId varchar(256) not null, customerOrderIdRef varchar(256), productId varchar(256) not null, extraId varchar(256) not null, quantity float, primary key (lineItemId))
create table htt.Product (productId varchar(256) not null, extraId varchar(256) not null, description varchar(256) not null, price float, numberAvailable float, primary key (productId, extraId))
create table htt.Customer (customerId varchar(256) not null, name varchar(256) not null, address varchar(256) not null, primary key (customerId))
create table htt.SimpleCustomerOrder (customerOrderId varchar(256) not null, customerId varchar(256) not null, orderNumber float not null, orderDate date not null, primary key (customerOrderId))
alter table htt.SimpleLineItem add constraint toSimpleCustomerOrder foreign key (customerOrderIdRef) references htt.SimpleCustomerOrder
alter table htt.SimpleLineItem add constraint fromSimpletoProduct foreign key (productId, extraId) references htt.Product
alter table htt.SimpleCustomerOrder add constraint fromSimpletoCustomer foreign key (customerId) references htt.Customer
create table htt.LineItem (customerIdRef varchar(256) not null, orderNumber float not null, productId varchar(256) not null, extraProdId varchar(256) not null, quantity float, primary key (customerIdRef, orderNumber, productId, extraProdId))
create table htt.CustomerOrder (customerId varchar(256) not null, orderNumber float not null, orderDate date not null, primary key (customerId, orderNumber))
alter table htt.LineItem add constraint toCustomerOrder foreign key (customerIdRef, orderNumber) references htt.CustomerOrder
alter table htt.LineItem add constraint toProduct foreign key (productId,extraProdId) references htt.Product
alter table htt.CustomerOrder add constraint toCustomer foreign key (customerId) references htt.Customer 
