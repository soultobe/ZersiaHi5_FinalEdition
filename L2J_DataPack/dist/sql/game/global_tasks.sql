CREATE TABLE IF NOT EXISTS `global_tasks` (
  `id` int(11) NOT NULL auto_increment,
  `task` varchar(50) NOT NULL DEFAULT '',
  `type` varchar(50) NOT NULL DEFAULT '',
  `last_activation` bigint(13) unsigned NOT NULL DEFAULT '0',
  `param1` varchar(100) NOT NULL DEFAULT '',
  `param2` varchar(100) NOT NULL DEFAULT '',
  `param3` varchar(255) NOT NULL DEFAULT '',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `global_tasks` VALUES
(9,'Restart','TYPE_GLOBAL_TASK',1432331701179,'1','06:55:00','300'); -- Server Restart
