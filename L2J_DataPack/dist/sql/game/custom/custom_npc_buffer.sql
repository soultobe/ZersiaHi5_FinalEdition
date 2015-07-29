CREATE TABLE IF NOT EXISTS `custom_npc_buffer` (
  `npc_id` mediumint(7) NOT NULL,
  `skill_id` int(6) NOT NULL,
  `skill_level` int(6) NOT NULL DEFAULT 1,
  `skill_fee_id` int(6) NOT NULL DEFAULT 0,
  `skill_fee_amount` int(6) NOT NULL DEFAULT 0,
  `buff_group` int(6) NOT NULL DEFAULT 0,
  PRIMARY KEY (`npc_id`,`skill_id`,`buff_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `custom_npc_buffer` VALUES
(1000003, 264, 1, 57, 1000, 264),
(1000003, 265, 1, 57, 1000, 265),
(1000003, 266, 1, 57, 1000, 266),
(1000003, 267, 1, 57, 1000, 267),
(1000003, 268, 1, 57, 1000, 268),
(1000003, 269, 1, 57, 1000, 269),
(1000003, 270, 1, 57, 1000, 270),
(1000003, 271, 1, 57, 1000, 271),
(1000003, 272, 1, 57, 1000, 272),
(1000003, 273, 1, 57, 1000, 273),
(1000003, 274, 1, 57, 1000, 274),
(1000003, 275, 1, 57, 1000, 275),
(1000003, 276, 1, 57, 1000, 276),
(1000003, 277, 1, 57, 1000, 277),
(1000003, 304, 1, 57, 1000, 304),
(1000003, 305, 1, 57, 1000, 305),
(1000003, 310, 1, 57, 1000, 310),
(1000003, 349, 1, 57, 1000, 349),
(1000003, 363, 1, 57, 1000, 363),
(1000003, 364, 1, 57, 1000, 364),
(1000003, 365, 1, 57, 1000, 365),
(1000003, 530, 1, 57, 1000, 530),
(1000003, 825, 1, 57, 2000, 825),
(1000003, 826, 1, 57, 2000, 826),
(1000003, 827, 1, 57, 2000, 827),
(1000003, 828, 1, 57, 2000, 828),
(1000003, 829, 1, 57, 2000, 829),
(1000003, 830, 1, 57, 2000, 830),
(1000003, 915, 1, 57, 1000, 915),
(1000003, 982, 3, 57, 1000, 982),
(1000003, 1035, 4, 57, 1000, 1035),
(1000003, 1043, 1, 57, 1000, 1043),
(1000003, 1044, 3, 57, 1000, 1044),
(1000003, 1062, 2, 57, 1000, 1062),
(1000003, 1078, 6, 57, 1000, 1078),
(1000003, 1085, 3, 57, 1000, 1085),
(1000003, 1240, 3, 57, 1000, 1240),
(1000003, 1259, 4, 57, 1000, 1259),
(1000003, 1284, 3, 57, 1000, 1284),
(1000003, 1303, 2, 57, 1000, 1303),
(1000003, 1323, 1, 57, 1000, 1323),
(1000003, 1355, 1, 57, 2000, 1355),
(1000003, 1356, 1, 57, 2000, 1356),
(1000003, 1357, 1, 57, 2000, 1357),
(1000003, 1363, 1, 57, 1000, 1363),
(1000003, 1388, 3, 57, 1000, 1388),
(1000003, 1389, 3, 57, 1000, 1389),
(1000003, 1392, 3, 57, 1000, 1392),
(1000003, 1393, 3, 57, 1000, 1393),
(1000003, 1397, 3, 57, 1000, 1397),
(1000003, 1413, 1, 57, 1000, 1413),
(1000003, 1461, 1, 57, 1000, 1461),
(1000003, 1499, 1, 57, 1000, 1499),
(1000003, 1500, 1, 57, 1000, 1500),
(1000003, 1501, 1, 57, 1000, 1501),
(1000003, 1502, 1, 57, 1000, 1502),
(1000003, 1503, 1, 57, 1000, 1503),
(1000003, 1504, 1, 57, 1000, 1504),
(1000003, 1506, 1, 57, 5000, 1506),
(1000003, 1519, 1, 57, 1000, 1519),
(1000003, 1542, 1, 57, 1000, 1542),
(1000003, 4699, 13, 57, 2000, 4699),
(1000003, 4700, 13, 57, 2000, 4700),
(1000003, 4702, 13, 57, 2000, 4702),
(1000003, 4703, 13, 57, 2000, 4703);
