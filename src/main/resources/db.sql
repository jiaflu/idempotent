CREATE DATABASE `idempotentTest` ;
USE `idempotentTest`;

CREATE TABLE `order` (
                       `order_id` int(11) NOT NULL AUTO_INCREMENT,
                       `user_id` int(11) NOT NULL,
                       `money` bigint(20) NOT NULL,
                       `create_time` datetime NOT NULL,
                       PRIMARY KEY (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8;

CREATE TABLE `wallet` (
                        `user_id` int(11) NOT NULL,
                        `total_amount` bigint(20) NOT NULL,
                        `freeze_amount` bigint(20) NOT NULL,
                        PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `idempotent` (
                            `src_app_id` smallint(5) unsigned NOT NULL COMMENT '来源AppID',
                            `src_bus_code` smallint(5) unsigned NOT NULL COMMENT '来源业务类型',
                            `src_trx_id` bigint(20) unsigned NOT NULL COMMENT '来源交易ID',
                            `app_id` smallint(5) NOT NULL COMMENT '调用APPID',
                            `bus_code` smallint(5) NOT NULL COMMENT '调用的业务代码',
                            `call_seq` smallint(5) NOT NULL COMMENT '同一事务同一方法内调用的次数',
                            `handler` smallint(5) NOT NULL COMMENT '处理者appid',
                            `called_methods` varchar(64) NOT NULL COMMENT '被调用过的方法名',
                            `md5` binary(16) NOT NULL COMMENT '参数摘要',
                            `sync_method_result` blob COMMENT '同步方法的返回结果',
                            `create_time` datetime NOT NULL COMMENT '执行时间',
                            `update_time` datetime NOT NULL,
                            `lock_version` smallint(32) NOT NULL COMMENT '乐观锁版本号',
                            PRIMARY KEY (`src_app_id`,`src_bus_code`,`src_trx_id`,`app_id`,`bus_code`,`call_seq`,`handler`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

