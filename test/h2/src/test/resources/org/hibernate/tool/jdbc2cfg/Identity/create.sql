CREATE TABLE `autoinc` (`id` int(11) NOT NULL identity,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))
CREATE TABLE `noautoinc` (`id` int(11) NOT NULL,  `data` varchar(100) default NULL,  PRIMARY KEY  (`id`))
