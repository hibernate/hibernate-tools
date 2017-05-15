alter table htt.LineItem drop constraint toCustomerOrder
alter table htt.LineItem drop constraint toProduct
alter table htt.CustomerOrder drop constraint toCustomer
alter table htt.SimpleLineItem drop constraint toSimpleCustomerOrder
alter table htt.SimpleLineItem drop constraint fromSimpletoProduct
alter table htt.SimpleCustomerOrder drop constraint fromSimpletoCustomer
drop table htt.SimpleLineItem
drop table htt.Product
drop table htt.Customer
drop table htt.SimpleCustomerOrder
drop table htt.CustomerOrder              
drop table htt.LineItem
drop schema htt
