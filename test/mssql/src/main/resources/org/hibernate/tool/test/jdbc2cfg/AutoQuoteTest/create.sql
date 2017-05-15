create schema htt
create table htt.[us-ers] ( userid INTEGER NOT NULL, department VARCHAR(3), PRIMARY KEY (userid) )
create table htt.typ ( indexid INTEGER NOT NULL, text varchar(10) NOT NULL, korr INTEGER NOT NULL, PRIMARY KEY (indexid) ) 
create table htt.worklogs ( indexid INTEGER NOT NULL, loggedid INTEGER NOT NULL, userid INTEGER NOT NULL, typ INTEGER NOT NULL, PRIMARY KEY (indexid, userid), FOREIGN KEY (userid) REFERENCES htt.[us-ers](userid), FOREIGN KEY (typ) REFERENCES htt.typ(indexid) )
