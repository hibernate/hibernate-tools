create table `us-ers` ( userid INTEGER NOT NULL, department VARCHAR(3), PRIMARY KEY (userid) )
create table typ ( indexid INTEGER NOT NULL, text varchar(10) NOT NULL, korr INTEGER NOT NULL, PRIMARY KEY (indexid) ) 
create table worklogs ( indexid INTEGER NOT NULL, loggedid INTEGER NOT NULL, userid INTEGER NOT NULL, typ INTEGER NOT NULL, PRIMARY KEY (indexid, userid), FOREIGN KEY (userid) REFERENCES `us-ers` (userid), FOREIGN KEY (typ) REFERENCES typ(indexid) )
