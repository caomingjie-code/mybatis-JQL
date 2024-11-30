CREATE TABLE `user` (
                        `id` int(11) NOT NULL AUTO_INCREMENT,
                        `name` varchar(1024) COLLATE utf8_bin DEFAULT NULL,
                        `birthday` datetime DEFAULT NULL,
                        `create_time` datetime DEFAULT NULL,
                        `money` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                        `sex` int(255) DEFAULT NULL,
                        `month` tinyint(10) DEFAULT NULL,
                        `work` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                        `is_del` int(2) DEFAULT NULL,
                        `version` int(12) DEFAULT NULL,
                        PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;
INSERT INTO `user` ( `id`, `name`, `birthday`, `create_time`, `money`, `sex`,`month`,`work` , `is_del`, `version`)
VALUES (1, 'ling', '2022-07-05 00:30:31', '2022-09-09 03:51:23','1000', '1','8', 'JAVA','1','1');

INSERT INTO `user` ( `id`, `name`, `birthday`, `create_time`, `money`, `sex`,`month`,`work` , `is_del`, `version`)
VALUES (null, 'ling', '2022-07-05 00:30:31', '2022-09-09 03:51:23','1000', '1','8', 'JAVA', '1', '1');


CREATE TABLE `user_order` (
                              `id` int(11) NOT NULL AUTO_INCREMENT,
                              `order_name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                              `order_money` int(255) DEFAULT NULL,
                              `user_id` int(11) DEFAULT NULL,
                              `is_del` int(3) DEFAULT '1',
                              `order_mark` varchar(255) COLLATE utf8_bin DEFAULT NULL COMMENT '订单标识唯一',
                              `create_time` datetime DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `base`.`user_order`(`id`, `order_name`, `order_money`, `user_id`, `is_del`, `create_time`) VALUES (1, 'computer', 101, 1, 1, '2022-09-12 03:51:32');
INSERT INTO `base`.`user_order`(`id`, `order_name`, `order_money`, `user_id`, `is_del`, `create_time`) VALUES (2, 'phone', 120, 1, 1, '2022-09-11 03:51:36');

CREATE TABLE `teacher` (
                           `id` int(11) NOT NULL AUTO_INCREMENT,
                           `name` varchar(255) COLLATE utf8_bin DEFAULT NULL,
                           `status` int(2) DEFAULT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `base`.`teacher`(`id`, `name`) VALUES (1, 'mathTeacher:Amop');
INSERT INTO `base`.`teacher`(`id`, `name`) VALUES (2, 'englishTeacher:Alen');

CREATE TABLE `user_teacher` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `user_id` int(255) DEFAULT NULL,
                                `teacher_id` int(255) DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `base`.`user_teacher`(`id`, `user_id`, `teacher_id`) VALUES (1, 1, 1);
INSERT INTO `base`.`user_teacher`(`id`, `user_id`, `teacher_id`) VALUES (2, 1, 2);

CREATE TABLE `encrypt_data` (
                                `id` int(11) NOT NULL AUTO_INCREMENT,
                                `encrypt_num` varchar(512) COLLATE utf8_bin DEFAULT NULL,
                                PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


