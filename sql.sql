-- detail: table
CREATE TABLE `detail` (
                          `id` int(11) NOT NULL AUTO_INCREMENT,
                          `content` varchar(50) DEFAULT NULL,
                          `type` varchar(20) NOT NULL,
                          `username` varchar(20) DEFAULT NULL,
                          `task_interval` int(11) DEFAULT NULL,
                          `test` varchar(20) DEFAULT NULL,
                          PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
-- task: table
CREATE TABLE `task` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `type` varchar(10) NOT NULL,
                        `username` varchar(20) DEFAULT NULL,
                        `content` varchar(50) DEFAULT NULL,
                        `collector_interval` int(11) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
