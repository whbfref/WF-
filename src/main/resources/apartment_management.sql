/*
SQLyog Ultimate v8.32 
MySQL - 5.6.24 : Database - apartment_management
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`apartment_management` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci */;

USE `apartment_management`;

/*Table structure for table `access_logs` */

DROP TABLE IF EXISTS `access_logs`;

CREATE TABLE `access_logs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户ID',
  `ip_address` varchar(50) NOT NULL COMMENT 'IP地址',
  `user_agent` varchar(255) DEFAULT NULL COMMENT '用户代理',
  `request_url` varchar(255) NOT NULL COMMENT '请求URL',
  `request_method` varchar(10) NOT NULL COMMENT '请求方法',
  `request_params` text COMMENT '请求参数',
  `response_code` int(11) DEFAULT NULL COMMENT '响应代码',
  `execution_time` bigint(20) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_ip_address` (`ip_address`),
  KEY `idx_request_url` (`request_url`(100)),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_access_log_time` (`create_time`),
  KEY `idx_access_log_ip` (`ip_address`),
  KEY `idx_access_log_url` (`request_url`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='访问日志表';

/*Data for the table `access_logs` */

/*Table structure for table `announcements` */

DROP TABLE IF EXISTS `announcements`;

CREATE TABLE `announcements` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `publisher_id` bigint(20) NOT NULL COMMENT '发布人ID',
  `title` varchar(100) NOT NULL COMMENT '公告标题',
  `content` text NOT NULL COMMENT '公告内容',
  `announcement_type` varchar(20) NOT NULL COMMENT '公告类型：系统(SYSTEM)、活动(ACTIVITY)、通知(NOTICE)、其他(OTHER)',
  `target_type` varchar(20) NOT NULL DEFAULT 'ALL' COMMENT '目标类型：全部(ALL)、房东(LANDLORD)、租客(TENANT)',
  `start_time` datetime NOT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `is_top` tinyint(1) DEFAULT '0' COMMENT '是否置顶',
  `status` varchar(20) NOT NULL DEFAULT 'PUBLISHED' COMMENT '状态：已发布(PUBLISHED)、已撤销(REVOKED)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_publisher_id` (`publisher_id`),
  KEY `idx_announcement_type` (`announcement_type`),
  KEY `idx_target_type` (`target_type`),
  KEY `idx_status` (`status`),
  KEY `idx_is_top` (`is_top`),
  KEY `idx_time_range` (`start_time`,`end_time`),
  KEY `idx_announcement_date` (`start_time`,`end_time`),
  KEY `idx_announcement_target` (`target_type`),
  KEY `idx_announcement_status` (`status`),
  CONSTRAINT `fk_announcement_publisher` FOREIGN KEY (`publisher_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

/*Data for the table `announcements` */

insert  into `announcements`(`id`,`publisher_id`,`title`,`content`,`announcement_type`,`target_type`,`start_time`,`end_time`,`is_top`,`status`,`create_time`,`update_time`) values (1,1,'系统维护通知','系统将于本周六晚上22:00-24:00进行维护升级，期间可能无法正常使用，敬请谅解。','SYSTEM','ALL','2024-06-01 00:00:00','2024-06-10 23:59:59',1,'PUBLISHED','2025-06-07 14:30:57','2025-06-07 14:30:57'),(2,1,'端午节放假通知','端午节期间（6月8日-10日）客服值班时间调整为9:00-18:00，紧急事务请拨打24小时热线。','NOTICE','ALL','2024-06-05 00:00:00','2024-06-12 23:59:59',0,'PUBLISHED','2025-06-07 14:30:57','2025-06-07 14:30:57'),(3,1,'夏季用电安全提醒','夏季用电高峰期即将到来，请各位租客注意用电安全，避免同时使用大功率电器。','NOTICE','TENANT','2024-06-01 00:00:00','2024-08-31 23:59:59',0,'PUBLISHED','2025-06-07 14:30:57','2025-06-07 14:30:57'),(4,1,'系统维护通知','系统将于本周六晚上22:00-24:00进行维护升级，期间可能无法正常使用，敬请谅解。','SYSTEM','ALL','2024-06-01 00:00:00','2024-06-10 23:59:59',1,'PUBLISHED','2025-06-07 14:31:04','2025-06-07 14:31:04'),(5,1,'端午节放假通知','端午节期间（6月8日-10日）客服值班时间调整为9:00-18:00，紧急事务请拨打24小时热线。','NOTICE','ALL','2024-06-05 00:00:00','2024-06-12 23:59:59',0,'PUBLISHED','2025-06-07 14:31:04','2025-06-07 14:31:04'),(6,1,'夏季用电安全提醒','夏季用电高峰期即将到来，请各位租客注意用电安全，避免同时使用大功率电器。','NOTICE','TENANT','2024-06-01 00:00:00','2024-08-31 23:59:59',0,'PUBLISHED','2025-06-07 14:31:04','2025-06-07 14:31:04');

/*Table structure for table `approval_records` */

DROP TABLE IF EXISTS `approval_records`;

CREATE TABLE `approval_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `target_id` bigint(20) NOT NULL COMMENT '审核目标ID',
  `target_type` varchar(20) NOT NULL COMMENT '审核类型：房东认证(LANDLORD)、物业认证(PROPERTY)、其他(OTHER)',
  `approver_id` bigint(20) DEFAULT NULL COMMENT '审核人ID',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：待审核(PENDING)、已通过(APPROVED)、已拒绝(REJECTED)',
  `rejection_reason` text COMMENT '拒绝原因',
  `approval_time` datetime DEFAULT NULL COMMENT '审核时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_target_id_type` (`target_id`,`target_type`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_status` (`status`),
  KEY `idx_approval_status` (`status`),
  KEY `idx_approval_target` (`target_id`,`target_type`),
  CONSTRAINT `fk_approval_approver` FOREIGN KEY (`approver_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='审核记录表';

/*Data for the table `approval_records` */

/*Table structure for table `bills` */

DROP TABLE IF EXISTS `bills`;

CREATE TABLE `bills` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contract_id` bigint(20) NOT NULL COMMENT '合同ID',
  `bill_number` varchar(50) NOT NULL COMMENT '账单编号',
  `bill_type` varchar(20) NOT NULL COMMENT '账单类型：租金(RENT)、水费(WATER)、电费(ELECTRICITY)、燃气费(GAS)、其他(OTHER)',
  `amount` decimal(10,2) NOT NULL COMMENT '金额(元)',
  `bill_month` varchar(7) NOT NULL COMMENT '账单月份(格式：YYYY-MM)',
  `due_date` date NOT NULL COMMENT '到期日',
  `paid_date` date DEFAULT NULL COMMENT '支付日期',
  `payment_method` varchar(20) DEFAULT NULL COMMENT '支付方式：支付宝、微信、银行转账等',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '交易ID',
  `status` varchar(20) NOT NULL DEFAULT 'UNPAID' COMMENT '状态：未支付(UNPAID)、已支付(PAID)、逾期(OVERDUE)、已取消(CANCELLED)',
  `remark` text COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_bill_number` (`bill_number`),
  KEY `idx_contract_id` (`contract_id`),
  KEY `idx_bill_type` (`bill_type`),
  KEY `idx_bill_month` (`bill_month`),
  KEY `idx_status` (`status`),
  KEY `idx_due_date` (`due_date`),
  KEY `idx_bill_due_date` (`due_date`),
  KEY `idx_bill_status` (`status`),
  CONSTRAINT `fk_bill_contract` FOREIGN KEY (`contract_id`) REFERENCES `contracts` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=70 DEFAULT CHARSET=utf8mb4 COMMENT='账单表';

/*Data for the table `bills` */

insert  into `bills`(`id`,`contract_id`,`bill_number`,`bill_type`,`amount`,`bill_month`,`due_date`,`paid_date`,`payment_method`,`transaction_id`,`status`,`remark`,`create_time`,`update_time`) values (1,1,'BILL202401001','RENT','2000.00','2024-01','2024-01-15','2024-01-14','支付宝',NULL,'PAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(2,1,'BILL202402001','RENT','2000.00','2024-02','2024-02-15','2024-02-14','支付宝',NULL,'PAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(3,1,'BILL202403001','RENT','2000.00','2024-03','2024-03-15','2024-03-14','支付宝',NULL,'PAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(4,1,'BILL202404001','RENT','2000.00','2024-04','2024-04-15','2024-04-13','支付宝',NULL,'PAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(5,1,'BILL202405001','RENT','2000.00','2024-05','2024-05-15','2024-05-14','支付宝',NULL,'PAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(6,1,'BILL202406001','RENT','2000.00','2024-06','2024-06-15',NULL,NULL,NULL,'UNPAID',NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(9,2,'BILL202402004','RENT','2800.00','2024-02','2024-02-01','2024-01-30','银行转账',NULL,'PAID',' [?????????8400?????800] [?????????8400?????800] [?????????8400?????800]','2025-06-07 14:30:57','2025-06-07 20:24:23'),(10,2,'BILL202405004','RENT','2800.00','2024-05','2024-05-01','2024-04-28','银行转账',NULL,'PAID',' [?????????8400?????800] [?????????8400?????800] [?????????8400?????800]','2025-06-07 14:30:57','2025-06-07 20:24:23'),(47,5,'RENT202412005','RENT','2000.00','2025-12','2025-12-01',NULL,NULL,NULL,'OVERDUE','Auto generated December rent bill','2025-06-07 20:25:35','2025-06-07 20:34:53'),(48,6,'RENT202412006','RENT','2800.00','2025-12','2025-12-05',NULL,NULL,NULL,'OVERDUE','Auto generated December rent bill','2025-06-07 20:25:35','2025-06-07 20:35:01'),(49,7,'RENT202412007','RENT','2500.00','2025-12','2025-12-10',NULL,NULL,NULL,'OVERDUE','Auto generated December rent bill','2025-06-07 20:25:35','2025-06-07 20:35:25'),(50,1,'RENT202412008','RENT','3000.00','2024-12','2024-12-05',NULL,NULL,NULL,'UNPAID',NULL,'2025-06-07 22:07:14','2025-06-07 22:07:14'),(52,3,'RENT202412010','RENT','3200.00','2024-12','2024-12-15',NULL,NULL,NULL,'PAID',NULL,'2025-06-07 22:07:14','2025-06-07 22:07:14'),(54,1,'RENT202412011','RENT','5000.00','2024-12','2025-06-05','2025-06-07',NULL,NULL,'PAID',NULL,'2025-06-07 22:08:24','2025-06-07 22:41:59'),(65,6,'RENT202410009','RENT','2800.00','2024-10','2024-10-05','2025-06-09',NULL,NULL,'PAID',NULL,'2025-06-09 00:28:55','2025-06-09 08:42:03'),(67,6,'RENT202412009','RENT','2800.00','2024-12','2024-12-05',NULL,NULL,NULL,'UNPAID',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55'),(68,6,'RENT202501009','RENT','2800.00','2025-01','2025-01-05',NULL,NULL,NULL,'UNPAID',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55'),(69,6,'RENT202502009','RENT','2800.00','2025-02','2025-02-05',NULL,NULL,NULL,'UNPAID',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55');

/*Table structure for table `cities` */

DROP TABLE IF EXISTS `cities`;

CREATE TABLE `cities` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `province_id` bigint(20) NOT NULL COMMENT '所属省份ID',
  `name` varchar(50) NOT NULL COMMENT '城市名称',
  `code` varchar(20) NOT NULL COMMENT '城市代码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_province_id` (`province_id`),
  CONSTRAINT `fk_city_province` FOREIGN KEY (`province_id`) REFERENCES `provinces` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='城市表';

/*Data for the table `cities` */

insert  into `cities`(`id`,`province_id`,`name`,`code`,`create_time`,`update_time`) values (1,1,'北京市','110100','2025-06-05 10:12:38','2025-06-05 10:12:38'),(2,2,'上海市','310100','2025-06-05 10:12:38','2025-06-05 10:12:38'),(3,3,'广州市','440100','2025-06-05 10:12:38','2025-06-05 10:12:38'),(4,3,'深圳市','440300','2025-06-05 10:12:38','2025-06-05 10:12:38'),(5,4,'南京市','320100','2025-06-05 10:12:38','2025-06-05 10:12:38'),(6,4,'苏州市','320500','2025-06-05 10:12:38','2025-06-05 10:12:38'),(7,5,'杭州市','330100','2025-06-05 10:12:38','2025-06-05 10:12:38'),(8,5,'宁波市','330200','2025-06-05 10:12:38','2025-06-05 10:12:38');

/*Table structure for table `complaints` */

DROP TABLE IF EXISTS `complaints`;

CREATE TABLE `complaints` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '投诉人ID',
  `target_id` bigint(20) DEFAULT NULL COMMENT '投诉目标ID',
  `title` varchar(100) NOT NULL COMMENT '投诉标题',
  `content` text NOT NULL COMMENT '投诉内容',
  `complaint_type` varchar(20) NOT NULL COMMENT '投诉类型：服务(SERVICE)、设施(FACILITY)、噪音(NOISE)、卫生(HYGIENE)、安全(SECURITY)、其他(OTHER)',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：待处理(PENDING)、处理中(PROCESSING)、已解决(RESOLVED)、已关闭(CLOSED)',
  `handler_id` bigint(20) DEFAULT NULL COMMENT '处理人ID',
  `handling_result` text COMMENT '处理结果',
  `handling_time` datetime DEFAULT NULL COMMENT '处理时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_complaint_type` (`complaint_type`),
  KEY `idx_complaint_status` (`status`),
  CONSTRAINT `fk_complaint_handler` FOREIGN KEY (`handler_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_complaint_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COMMENT='投诉建议表';

/*Data for the table `complaints` */

insert  into `complaints`(`id`,`user_id`,`target_id`,`title`,`content`,`complaint_type`,`status`,`handler_id`,`handling_result`,`handling_time`,`create_time`,`update_time`) values (1,8,1,'楼道照明不足','晚上楼道灯光昏暗，存在安全隐患，希望能增加照明设备。','FACILITY','RESOLVED',1,'已联系物业更换了楼道LED灯，照明问题已解决。','2024-05-20 16:30:00','2025-06-07 14:30:57','2025-06-07 14:30:57'),(2,9,2,'邻居噪音扰民','楼上邻居经常深夜播放音乐，影响休息，希望能协调解决。','NOISE','PROCESSING',1,NULL,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(3,10,NULL,'缴费方式建议','希望能增加更多的在线缴费方式，比如信用卡支付。','OTHER','PENDING',NULL,NULL,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(4,8,1,'楼道照明不足','晚上楼道灯光昏暗，存在安全隐患，希望能增加照明设备。','FACILITY','RESOLVED',1,'已联系物业更换了楼道LED灯，照明问题已解决。','2024-05-20 16:30:00','2025-06-07 14:31:04','2025-06-07 14:31:04'),(5,9,2,'邻居噪音扰民','楼上邻居经常深夜播放音乐，影响休息，希望能协调解决。','NOISE','PROCESSING',1,NULL,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(6,10,NULL,'缴费方式建议','希望能增加更多的在线缴费方式，比如信用卡支付。','OTHER','PENDING',NULL,NULL,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04');

/*Table structure for table `contracts` */

DROP TABLE IF EXISTS `contracts`;

CREATE TABLE `contracts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `tenant_id` bigint(20) NOT NULL COMMENT '租客ID',
  `landlord_id` bigint(20) NOT NULL COMMENT '房东ID',
  `contract_number` varchar(50) NOT NULL COMMENT '合同编号',
  `start_date` date NOT NULL COMMENT '开始日期',
  `end_date` date NOT NULL COMMENT '结束日期',
  `rent_price` decimal(10,2) NOT NULL COMMENT '租金(元/月)',
  `deposit` decimal(10,2) NOT NULL COMMENT '押金(元)',
  `payment_method` varchar(20) NOT NULL COMMENT '支付方式：月付、季付、半年付、年付',
  `payment_day` int(11) NOT NULL DEFAULT '1' COMMENT '每月缴费日',
  `electric_fee` decimal(5,2) NOT NULL COMMENT '电费(元/度)',
  `water_fee` decimal(5,2) NOT NULL COMMENT '水费(元/吨)',
  `gas_fee` decimal(5,2) NOT NULL COMMENT '燃气费(元/立方)',
  `signed_date` date NOT NULL COMMENT '签约日期',
  `contract_file_url` varchar(255) DEFAULT NULL COMMENT '合同文件URL',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：执行中(ACTIVE)、已完成(COMPLETED)、已终止(TERMINATED)',
  `termination_reason` text COMMENT '终止原因',
  `termination_date` date DEFAULT NULL COMMENT '终止日期',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_contract_number` (`contract_number`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_landlord_id` (`landlord_id`),
  KEY `idx_status` (`status`),
  KEY `idx_date_range` (`start_date`,`end_date`),
  KEY `idx_contract_date_range` (`start_date`,`end_date`),
  KEY `idx_contract_status` (`status`),
  KEY `idx_contract_signed_date` (`signed_date`),
  KEY `idx_room_status_active` (`room_id`,`status`),
  CONSTRAINT `fk_contract_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `landlords` (`id`),
  CONSTRAINT `fk_contract_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  CONSTRAINT `fk_contract_tenant` FOREIGN KEY (`tenant_id`) REFERENCES `tenants` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COMMENT='合同表';

/*Data for the table `contracts` */

insert  into `contracts`(`id`,`room_id`,`tenant_id`,`landlord_id`,`contract_number`,`start_date`,`end_date`,`rent_price`,`deposit`,`payment_method`,`payment_day`,`electric_fee`,`water_fee`,`gas_fee`,`signed_date`,`contract_file_url`,`status`,`termination_reason`,`termination_date`,`create_time`,`update_time`) values (1,1,1,1,'HT202401001','2024-01-15','2025-01-14','2000.00','2000.00','月付',15,'0.60','4.50','2.80','2024-01-10',NULL,'COMPLETED',NULL,NULL,'2025-06-07 14:30:56','2025-06-07 20:12:27'),(2,3,2,2,'HT202402002','2024-02-01','2025-01-31','2800.00','2800.00','季付',1,'0.55','4.20','2.60','2024-01-25',NULL,'COMPLETED',NULL,NULL,'2025-06-07 14:30:56','2025-06-07 20:12:27'),(3,4,3,2,'HT202403003','2024-03-01','2025-02-28','2500.00','2500.00','月付',1,'0.58','4.30','2.70','2024-02-25',NULL,'COMPLETED',NULL,NULL,'2025-06-07 14:30:56','2025-06-07 20:12:27'),(4,7,4,2,'HT202404004','2024-04-01','2025-03-31','3000.00','3000.00','季付',1,'0.62','4.60','2.90','2024-03-25',NULL,'COMPLETED',NULL,NULL,'2025-06-07 14:30:56','2025-06-07 20:12:27'),(5,1,1,1,'HT202412001','2024-12-01','2025-11-30','2000.00','4000.00','MONTHLY',1,'1.00','5.00','3.00','2024-11-25',NULL,'ACTIVE',NULL,NULL,'2025-06-07 20:13:07','2025-06-07 20:13:07'),(6,3,2,1,'HT202412002','2024-12-01','2025-11-30','2800.00','5600.00','MONTHLY',5,'1.00','5.00','3.00','2024-11-25',NULL,'ACTIVE',NULL,NULL,'2025-06-07 20:13:07','2025-06-07 20:13:07'),(7,4,3,1,'HT202412003','2024-12-01','2025-11-30','2500.00','5000.00','MONTHLY',10,'1.00','5.00','3.00','2024-11-25',NULL,'ACTIVE',NULL,NULL,'2025-06-07 20:13:07','2025-06-07 20:13:07');

/*Table structure for table `deposits` */

DROP TABLE IF EXISTS `deposits`;

CREATE TABLE `deposits` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `property_id` bigint(20) NOT NULL,
  `landlord_id` bigint(20) NOT NULL,
  `lease_id` bigint(20) DEFAULT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `payment_time` datetime DEFAULT NULL,
  `refund_time` datetime DEFAULT NULL,
  `deduction_amount` decimal(10,2) DEFAULT '0.00',
  `deduction_reason` text COLLATE utf8mb4_unicode_ci,
  `remark` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_deposit_user` (`user_id`),
  KEY `fk_deposit_property` (`property_id`),
  KEY `fk_deposit_landlord` (`landlord_id`),
  CONSTRAINT `fk_deposit_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `landlords` (`id`),
  CONSTRAINT `fk_deposit_property` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`),
  CONSTRAINT `fk_deposit_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `deposits` */

/*Table structure for table `districts` */

DROP TABLE IF EXISTS `districts`;

CREATE TABLE `districts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `city_id` bigint(20) NOT NULL COMMENT '所属城市ID',
  `name` varchar(50) NOT NULL COMMENT '区县名称',
  `code` varchar(20) NOT NULL COMMENT '区县代码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`),
  KEY `idx_city_id` (`city_id`),
  CONSTRAINT `fk_district_city` FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COMMENT='区县表';

/*Data for the table `districts` */

insert  into `districts`(`id`,`city_id`,`name`,`code`,`create_time`,`update_time`) values (1,1,'东城区','110101','2025-06-05 10:12:38','2025-06-05 10:12:38'),(2,1,'西城区','110102','2025-06-05 10:12:38','2025-06-05 10:12:38'),(3,1,'朝阳区','110105','2025-06-05 10:12:38','2025-06-05 10:12:38'),(4,1,'海淀区','110108','2025-06-05 10:12:38','2025-06-05 10:12:38'),(5,2,'黄浦区','310101','2025-06-05 10:12:38','2025-06-05 10:12:38'),(6,2,'徐汇区','310104','2025-06-05 10:12:38','2025-06-05 10:12:38'),(7,2,'静安区','310106','2025-06-05 10:12:38','2025-06-05 10:12:38'),(8,2,'浦东新区','310115','2025-06-05 10:12:38','2025-06-05 10:12:38'),(9,3,'越秀区','440104','2025-06-05 10:12:38','2025-06-05 10:12:38'),(10,3,'海珠区','440105','2025-06-05 10:12:38','2025-06-05 10:12:38'),(11,3,'天河区','440106','2025-06-05 10:12:38','2025-06-05 10:12:38'),(12,3,'白云区','440111','2025-06-05 10:12:38','2025-06-05 10:12:38'),(13,4,'福田区','440304','2025-06-05 10:12:38','2025-06-05 10:12:38'),(14,4,'罗湖区','440303','2025-06-05 10:12:38','2025-06-05 10:12:38'),(15,4,'南山区','440305','2025-06-05 10:12:38','2025-06-05 10:12:38'),(16,4,'宝安区','440306','2025-06-05 10:12:38','2025-06-05 10:12:38'),(17,5,'玄武区','320102','2025-06-05 10:12:38','2025-06-05 10:12:38'),(18,5,'秦淮区','320104','2025-06-05 10:12:38','2025-06-05 10:12:38'),(19,5,'建邺区','320105','2025-06-05 10:12:38','2025-06-05 10:12:38'),(20,5,'鼓楼区','320106','2025-06-05 10:12:38','2025-06-05 10:12:38'),(21,6,'姑苏区','320508','2025-06-05 10:12:38','2025-06-05 10:12:38'),(22,6,'吴中区','320506','2025-06-05 10:12:38','2025-06-05 10:12:38'),(23,6,'相城区','320507','2025-06-05 10:12:38','2025-06-05 10:12:38'),(24,6,'工业园区','320571','2025-06-05 10:12:38','2025-06-05 10:12:38'),(25,7,'上城区','330102','2025-06-05 10:12:38','2025-06-05 10:12:38'),(26,7,'下城区','330103','2025-06-05 10:12:38','2025-06-05 10:12:38'),(27,7,'江干区','330104','2025-06-05 10:12:38','2025-06-05 10:12:38'),(28,7,'西湖区','330106','2025-06-05 10:12:38','2025-06-05 10:12:38'),(29,8,'海曙区','330203','2025-06-05 10:12:38','2025-06-05 10:12:38'),(30,8,'江北区','330205','2025-06-05 10:12:38','2025-06-05 10:12:38'),(31,8,'鄞州区','330212','2025-06-05 10:12:38','2025-06-05 10:12:38'),(32,8,'镇海区','330211','2025-06-05 10:12:38','2025-06-05 10:12:38');

/*Table structure for table `landlord_applications` */

DROP TABLE IF EXISTS `landlord_applications`;

CREATE TABLE `landlord_applications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `real_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_card_front_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_card_back_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `remarks` text COLLATE utf8mb4_unicode_ci,
  `review_remarks` text COLLATE utf8mb4_unicode_ci,
  `review_time` datetime DEFAULT NULL,
  `reviewer_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `landlord_applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `landlord_applications` */

/*Table structure for table `landlords` */

DROP TABLE IF EXISTS `landlords`;

CREATE TABLE `landlords` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `bank_card` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `bank_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_card` varchar(18) COLLATE utf8mb4_unicode_ci NOT NULL,
  `id_card_back_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `id_card_front_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `rating` decimal(2,1) DEFAULT NULL,
  `real_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `user_id` bigint(20) NOT NULL,
  `verified` bit(1) NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_o71hs1ger57uxyj46w7jile0n` (`id_card`),
  UNIQUE KEY `UK_mwxg1mtc1u9yhxafv1qxb5mde` (`user_id`),
  KEY `idx_landlord_rating` (`rating`),
  KEY `idx_landlord_verified` (`verified`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `landlords` */

insert  into `landlords`(`id`,`create_time`,`update_time`,`bank_card`,`bank_name`,`id_card`,`id_card_back_url`,`id_card_front_url`,`rating`,`real_name`,`user_id`,`verified`,`created_at`,`updated_at`) values (1,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','6217000010001234567','中国工商银行','110101198501011234','https://example.com/images/id_back_001.jpg','https://example.com/images/id_front_001.jpg','4.8','张伟',6,'','2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(2,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','6217000010002345678','中国建设银行','110101198602012345','https://example.com/images/id_back_002.jpg','https://example.com/images/id_front_002.jpg','4.6','李明',7,'','2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(10,'2025-06-09 00:03:58.000000','2025-06-09 00:03:58.000000',NULL,NULL,'110101199001011234',NULL,NULL,'4.8','Zhang San',10,'','2025-06-09 00:03:58.000000','2025-06-09 00:03:58.000000');

/*Table structure for table `maintenance_images` */

DROP TABLE IF EXISTS `maintenance_images`;

CREATE TABLE `maintenance_images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `maintenance_id` bigint(20) NOT NULL COMMENT '维修记录ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `image_type` varchar(20) NOT NULL DEFAULT 'BEFORE' COMMENT '图片类型：维修前(BEFORE)、维修后(AFTER)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_maintenance_id` (`maintenance_id`),
  KEY `idx_image_type` (`image_type`),
  CONSTRAINT `fk_image_maintenance` FOREIGN KEY (`maintenance_id`) REFERENCES `maintenance_records` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='维修图片表';

/*Data for the table `maintenance_images` */

/*Table structure for table `maintenance_records` */

DROP TABLE IF EXISTS `maintenance_records`;

CREATE TABLE `maintenance_records` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `reporter_id` bigint(20) NOT NULL COMMENT '报修人ID',
  `assignee_id` bigint(20) DEFAULT NULL COMMENT '受理人ID',
  `title` varchar(100) NOT NULL COMMENT '维修标题',
  `description` text NOT NULL COMMENT '问题描述',
  `maintenance_type` varchar(20) NOT NULL COMMENT '维修类型：水电(UTILITY)、家具(FURNITURE)、电器(APPLIANCE)、结构(STRUCTURE)、其他(OTHER)',
  `priority` varchar(20) NOT NULL DEFAULT 'MEDIUM' COMMENT '优先级：低(LOW)、中(MEDIUM)、高(HIGH)、紧急(URGENT)',
  `status` varchar(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态：待处理(PENDING)、处理中(PROCESSING)、已完成(COMPLETED)、已取消(CANCELLED)',
  `report_time` datetime NOT NULL COMMENT '报修时间',
  `scheduled_time` datetime DEFAULT NULL COMMENT '预约时间',
  `completion_time` datetime DEFAULT NULL COMMENT '完成时间',
  `maintenance_cost` decimal(10,2) DEFAULT NULL COMMENT '维修费用',
  `maintenance_result` text COMMENT '维修结果',
  `rating` int(11) DEFAULT NULL COMMENT '评分(1-5)',
  `feedback` text COMMENT '反馈意见',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_reporter_id` (`reporter_id`),
  KEY `idx_assignee_id` (`assignee_id`),
  KEY `idx_status` (`status`),
  KEY `idx_report_time` (`report_time`),
  KEY `idx_maintenance_type` (`maintenance_type`),
  KEY `idx_maintenance_status` (`status`),
  KEY `idx_maintenance_priority` (`priority`),
  KEY `idx_maintenance_report_time` (`report_time`),
  CONSTRAINT `fk_maintenance_assignee` FOREIGN KEY (`assignee_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_maintenance_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_maintenance_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COMMENT='维修记录表';

/*Data for the table `maintenance_records` */

insert  into `maintenance_records`(`id`,`room_id`,`reporter_id`,`assignee_id`,`title`,`description`,`maintenance_type`,`priority`,`status`,`report_time`,`scheduled_time`,`completion_time`,`maintenance_cost`,`maintenance_result`,`rating`,`feedback`,`create_time`,`update_time`) values (1,1,8,1,'空调不制冷','房间空调突然不制冷了，可能需要加氟或维修','APPLIANCE','HIGH','COMPLETED','2024-05-10 14:30:00','2024-05-12 09:00:00','2024-05-12 11:30:00','150.00','空调已加氟，制冷正常',5,'维修师傅很专业，服务很好','2025-06-07 14:30:57','2025-06-07 14:30:57'),(2,3,9,1,'洗衣机漏水','洗衣机底部有漏水现象，需要检查','APPLIANCE','MEDIUM','COMPLETED','2024-04-15 08:20:00','2024-04-16 14:00:00','2024-04-16 15:45:00','80.00','更换了排水管密封圈，问题解决',4,'及时解决了问题','2025-06-07 14:30:57','2025-06-07 14:30:57'),(3,4,10,1,'热水器水温不稳定','洗澡时热水器水温忽冷忽热','UTILITY','MEDIUM','PROCESSING','2024-06-01 19:15:00','2024-06-03 10:00:00',NULL,NULL,NULL,NULL,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(4,7,11,1,'门锁损坏','房门钥匙无法正常开锁，可能需要更换锁芯','STRUCTURE','HIGH','PENDING','2024-06-02 12:00:00',NULL,NULL,NULL,NULL,NULL,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(5,1,8,1,'空调不制冷','房间空调突然不制冷了，可能需要加氟或维修','APPLIANCE','HIGH','COMPLETED','2024-05-10 14:30:00','2024-05-12 09:00:00','2024-05-12 11:30:00','150.00','空调已加氟，制冷正常',5,'维修师傅很专业，服务很好','2025-06-07 14:31:04','2025-06-07 14:31:04'),(6,3,9,1,'洗衣机漏水','洗衣机底部有漏水现象，需要检查','APPLIANCE','MEDIUM','COMPLETED','2024-04-15 08:20:00','2024-04-16 14:00:00','2024-04-16 15:45:00','80.00','更换了排水管密封圈，问题解决',4,'及时解决了问题','2025-06-07 14:31:04','2025-06-07 14:31:04'),(7,4,10,1,'热水器水温不稳定','洗澡时热水器水温忽冷忽热','UTILITY','MEDIUM','PROCESSING','2024-06-01 19:15:00','2024-06-03 10:00:00',NULL,NULL,NULL,NULL,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(8,7,11,1,'门锁损坏','房门钥匙无法正常开锁，可能需要更换锁芯','STRUCTURE','HIGH','PENDING','2024-06-02 12:00:00',NULL,NULL,NULL,NULL,NULL,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04');

/*Table structure for table `meter_readings` */

DROP TABLE IF EXISTS `meter_readings`;

CREATE TABLE `meter_readings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `reading_type` varchar(20) NOT NULL COMMENT '读数类型：水表(WATER)、电表(ELECTRICITY)、燃气表(GAS)',
  `reading_value` decimal(10,2) NOT NULL COMMENT '读数值',
  `reading_date` date NOT NULL COMMENT '读数日期',
  `reader_id` bigint(20) DEFAULT NULL COMMENT '抄表人ID',
  `reading_image_url` varchar(255) DEFAULT NULL COMMENT '读数照片URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_type_date` (`room_id`,`reading_type`,`reading_date`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_reading_type` (`reading_type`),
  KEY `idx_reading_date` (`reading_date`),
  KEY `fk_reading_reader` (`reader_id`),
  CONSTRAINT `fk_reading_reader` FOREIGN KEY (`reader_id`) REFERENCES `users` (`id`),
  CONSTRAINT `fk_reading_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='表读数表';

/*Data for the table `meter_readings` */

/*Table structure for table `notifications` */

DROP TABLE IF EXISTS `notifications`;

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '接收用户ID',
  `title` varchar(100) NOT NULL COMMENT '通知标题',
  `content` text NOT NULL COMMENT '通知内容',
  `notification_type` varchar(20) NOT NULL COMMENT '通知类型：系统(SYSTEM)、账单(BILL)、合同(CONTRACT)、维修(MAINTENANCE)、其他(OTHER)',
  `related_id` bigint(20) DEFAULT NULL COMMENT '关联ID',
  `is_read` tinyint(1) DEFAULT '0' COMMENT '是否已读',
  `read_time` datetime DEFAULT NULL COMMENT '阅读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_is_read` (`is_read`),
  KEY `idx_notification_type` (`notification_type`),
  KEY `idx_create_time` (`create_time`),
  KEY `idx_notification_read` (`is_read`),
  KEY `idx_notification_create_time` (`create_time`),
  CONSTRAINT `fk_notification_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=61 DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

/*Data for the table `notifications` */

insert  into `notifications`(`id`,`user_id`,`title`,`content`,`notification_type`,`related_id`,`is_read`,`read_time`,`create_time`,`update_time`) values (1,1,'新合同已创建','您的租房合同已创建，合同编号: HT202401001，租期: 2024-01-15 至 2025-01-14。','CONTRACT',1,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(2,1,'新合同已创建','租客已签署合同，合同编号: HT202401001，租期: 2024-01-15 至 2025-01-14。','CONTRACT',1,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(3,2,'新合同已创建','您的租房合同已创建，合同编号: HT202402002，租期: 2024-02-01 至 2025-01-31。','CONTRACT',2,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(4,2,'新合同已创建','租客已签署合同，合同编号: HT202402002，租期: 2024-02-01 至 2025-01-31。','CONTRACT',2,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(5,3,'新合同已创建','您的租房合同已创建，合同编号: HT202403003，租期: 2024-03-01 至 2025-02-28。','CONTRACT',3,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(6,2,'新合同已创建','租客已签署合同，合同编号: HT202403003，租期: 2024-03-01 至 2025-02-28。','CONTRACT',3,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(7,4,'新合同已创建','您的租房合同已创建，合同编号: HT202404004，租期: 2024-04-01 至 2025-03-31。','CONTRACT',4,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(8,2,'新合同已创建','租客已签署合同，合同编号: HT202404004，租期: 2024-04-01 至 2025-03-31。','CONTRACT',4,0,NULL,'2025-06-07 14:30:56','2025-06-07 14:30:56'),(9,1,'新的维修任务','您有一个新的维修任务，标题: 空调不制冷，房间号: A801，优先级: 高','MAINTENANCE',1,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(10,1,'新的维修请求','您的房间收到了一个维修请求，标题: 空调不制冷，房间号: A801，优先级: 高','MAINTENANCE',1,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(11,1,'新的维修任务','您有一个新的维修任务，标题: 洗衣机漏水，房间号: B501，优先级: 中','MAINTENANCE',2,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(12,1,'新的维修请求','您的房间收到了一个维修请求，标题: 洗衣机漏水，房间号: B501，优先级: 中','MAINTENANCE',2,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(13,1,'新的维修任务','您有一个新的维修任务，标题: 热水器水温不稳定，房间号: C1201，优先级: 中','MAINTENANCE',3,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(14,2,'新的维修请求','您的房间收到了一个维修请求，标题: 热水器水温不稳定，房间号: C1201，优先级: 中','MAINTENANCE',3,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(15,1,'新的维修任务','您有一个新的维修任务，标题: 门锁损坏，房间号: D101，优先级: 高','MAINTENANCE',4,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(16,2,'新的维修请求','您的房间收到了一个维修请求，标题: 门锁损坏，房间号: D101，优先级: 高','MAINTENANCE',4,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(17,8,'租金缴费提醒','您好！您的6月租金即将到期，请及时缴费。金额：2000元，到期日：2024-06-15','BILL',6,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(18,8,'维修完成通知','您报修的空调不制冷问题已经修复完成，请确认。','MAINTENANCE',1,1,'2024-05-12 15:30:00','2025-06-07 14:30:57','2025-06-07 14:30:57'),(19,9,'合同到期提醒','您的租房合同将于2025年1月31日到期，请提前联系续租事宜。','CONTRACT',2,0,NULL,'2025-06-07 14:30:57','2025-06-07 14:30:57'),(20,10,'维修进度通知','您报修的热水器问题已安排维修人员，预计6月3日上午处理。','MAINTENANCE',3,1,'2024-06-02 09:15:00','2025-06-07 14:30:57','2025-06-07 14:30:57'),(21,11,'欢迎入住','欢迎您入住东城区胡同小院，如有任何问题请及时联系。','SYSTEM',NULL,1,'2024-04-01 10:00:00','2025-06-07 14:30:57','2025-06-07 14:30:57'),(22,1,'新的维修任务','您有一个新的维修任务，标题: 空调不制冷，房间号: A801，优先级: 高','MAINTENANCE',5,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(23,1,'新的维修请求','您的房间收到了一个维修请求，标题: 空调不制冷，房间号: A801，优先级: 高','MAINTENANCE',5,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(24,1,'新的维修任务','您有一个新的维修任务，标题: 洗衣机漏水，房间号: B501，优先级: 中','MAINTENANCE',6,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(25,1,'新的维修请求','您的房间收到了一个维修请求，标题: 洗衣机漏水，房间号: B501，优先级: 中','MAINTENANCE',6,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(26,1,'新的维修任务','您有一个新的维修任务，标题: 热水器水温不稳定，房间号: C1201，优先级: 中','MAINTENANCE',7,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(27,2,'新的维修请求','您的房间收到了一个维修请求，标题: 热水器水温不稳定，房间号: C1201，优先级: 中','MAINTENANCE',7,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(28,1,'新的维修任务','您有一个新的维修任务，标题: 门锁损坏，房间号: D101，优先级: 高','MAINTENANCE',8,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(29,2,'新的维修请求','您的房间收到了一个维修请求，标题: 门锁损坏，房间号: D101，优先级: 高','MAINTENANCE',8,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(30,8,'租金缴费提醒','您好！您的6月租金即将到期，请及时缴费。金额：2000元，到期日：2024-06-15','BILL',6,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(31,8,'维修完成通知','您报修的空调不制冷问题已经修复完成，请确认。','MAINTENANCE',1,1,'2024-05-12 15:30:00','2025-06-07 14:31:04','2025-06-07 14:31:04'),(32,9,'合同到期提醒','您的租房合同将于2025年1月31日到期，请提前联系续租事宜。','CONTRACT',2,0,NULL,'2025-06-07 14:31:04','2025-06-07 14:31:04'),(33,10,'维修进度通知','您报修的热水器问题已安排维修人员，预计6月3日上午处理。','MAINTENANCE',3,1,'2024-06-02 09:15:00','2025-06-07 14:31:04','2025-06-07 14:31:04'),(34,11,'欢迎入住','欢迎您入住东城区胡同小院，如有任何问题请及时联系。','SYSTEM',NULL,1,'2024-04-01 10:00:00','2025-06-07 14:31:04','2025-06-07 14:31:04'),(35,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:31:47','2025-06-07 14:31:47'),(36,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:31:47','2025-06-07 14:31:47'),(37,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:31:52','2025-06-07 14:31:52'),(38,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:31:52','2025-06-07 14:31:52'),(39,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:31:57','2025-06-07 14:31:57'),(40,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:31:57','2025-06-07 14:31:57'),(41,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:13','2025-06-07 14:32:13'),(42,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:13','2025-06-07 14:32:13'),(43,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:14','2025-06-07 14:32:14'),(44,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:14','2025-06-07 14:32:14'),(45,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:14','2025-06-07 14:32:14'),(46,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:14','2025-06-07 14:32:14'),(47,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(48,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(49,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(50,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(51,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(52,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(53,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(54,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(55,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(56,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:15','2025-06-07 14:32:15'),(57,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:16','2025-06-07 14:32:16'),(58,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:16','2025-06-07 14:32:16'),(59,11,'租金缴费提醒','您的6月份租金1800元即将到期，请及时缴费','BILL',NULL,0,NULL,'2025-06-07 14:32:16','2025-06-07 14:32:16'),(60,12,'合同签署成功','恭喜您成功签署租房合同，欢迎入住！','CONTRACT',NULL,0,NULL,'2025-06-07 14:32:16','2025-06-07 14:32:16');

/*Table structure for table `properties` */

DROP TABLE IF EXISTS `properties`;

CREATE TABLE `properties` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `landlord_id` bigint(20) NOT NULL,
  `title` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` text COLLATE utf8mb4_unicode_ci,
  `type` int(11) NOT NULL,
  `monthly_rent` decimal(10,2) NOT NULL,
  `area` decimal(7,2) NOT NULL,
  `floor` int(11) DEFAULT NULL,
  `total_floor` int(11) DEFAULT NULL,
  `bedrooms` int(11) DEFAULT NULL,
  `bathrooms` int(11) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `province` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `city` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `district` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `address` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `latitude` double(10,6) DEFAULT NULL,
  `longitude` double(10,6) DEFAULT NULL,
  `facilities` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `support_one_payment` tinyint(1) NOT NULL DEFAULT '0',
  `support_three_payment` tinyint(1) NOT NULL DEFAULT '1',
  `min_lease_term` int(11) NOT NULL DEFAULT '12',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_property_landlord` (`landlord_id`),
  CONSTRAINT `fk_property_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `landlords` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `properties` */

insert  into `properties`(`id`,`landlord_id`,`title`,`description`,`type`,`monthly_rent`,`area`,`floor`,`total_floor`,`bedrooms`,`bathrooms`,`status`,`province`,`city`,`district`,`address`,`latitude`,`longitude`,`facilities`,`support_one_payment`,`support_three_payment`,`min_lease_term`,`created_at`,`updated_at`,`created_by`,`updated_by`,`create_time`,`update_time`) values (1,1,'朝阳区现代公寓','位于朝阳区CBD核心区域，交通便利，配套齐全',1,'3500.00','65.50',8,20,2,1,1,'北京市','北京市','朝阳区','建国门外大街12号',39.908823,116.397470,'空调,洗衣机,冰箱,热水器,宽带',0,1,12,'2025-06-07 14:30:56','2025-06-07 14:30:56',NULL,NULL,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(2,1,'海淀区学区房','毗邻知名大学，适合学生和年轻白领居住',1,'2800.00','45.20',5,15,1,1,1,'北京市','北京市','海淀区','中关村大街58号',39.983424,116.318407,'空调,洗衣机,冰箱,书桌,宽带',0,1,6,'2025-06-07 14:30:56','2025-06-07 14:30:56',NULL,NULL,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(3,2,'西城区精装修公寓','精装修，家具家电齐全，拎包入住',1,'4200.00','78.30',12,25,3,2,1,'北京市','北京市','西城区','金融街15号',39.916485,116.366381,'空调,洗衣机,冰箱,热水器,微波炉,宽带,停车位',1,1,12,'2025-06-07 14:30:56','2025-06-07 14:30:56',NULL,NULL,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(4,2,'东城区胡同小院','传统胡同改造，独门独院，环境安静',2,'5500.00','120.00',1,2,4,2,1,'北京市','北京市','东城区','南锣鼓巷胡同8号',39.936019,116.402334,'空调,洗衣机,冰箱,热水器,花园,停车位',1,1,12,'2025-06-07 14:30:56','2025-06-07 14:30:56',NULL,NULL,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000'),(5,1,'朝阳区现代公寓','位于朝阳区CBD核心区域，交通便利，配套齐全',1,'3500.00','65.50',8,20,2,1,1,'北京市','北京市','朝阳区','建国门外大街12号',39.908823,116.397470,'空调,洗衣机,冰箱,热水器,宽带',0,1,12,'2025-06-07 14:31:04','2025-06-07 14:31:04',NULL,NULL,'2025-06-07 14:31:04.000000','2025-06-07 14:31:04.000000'),(6,1,'海淀区学区房','毗邻知名大学，适合学生和年轻白领居住',1,'2800.00','45.20',5,15,1,1,1,'北京市','北京市','海淀区','中关村大街58号',39.983424,116.318407,'空调,洗衣机,冰箱,书桌,宽带',0,1,6,'2025-06-07 14:31:04','2025-06-07 14:31:04',NULL,NULL,'2025-06-07 14:31:04.000000','2025-06-07 14:31:04.000000'),(7,2,'西城区精装修公寓','精装修，家具家电齐全，拎包入住',1,'4200.00','78.30',12,25,3,2,1,'北京市','北京市','西城区','金融街15号',39.916485,116.366381,'空调,洗衣机,冰箱,热水器,微波炉,宽带,停车位',1,1,12,'2025-06-07 14:31:04','2025-06-07 14:31:04',NULL,NULL,'2025-06-07 14:31:04.000000','2025-06-07 14:31:04.000000'),(8,2,'东城区胡同小院','传统胡同改造，独门独院，环境安静',2,'5500.00','120.00',1,2,4,2,1,'北京市','北京市','东城区','南锣鼓巷胡同8号',39.936019,116.402334,'空调,洗衣机,冰箱,热水器,花园,停车位',1,1,12,'2025-06-07 14:31:04','2025-06-07 14:31:04',NULL,NULL,'2025-06-07 14:31:04.000000','2025-06-07 14:31:04.000000'),(10,10,'Sunshine Garden',NULL,0,'0.00','0.00',NULL,NULL,NULL,NULL,0,NULL,NULL,NULL,'Beijing Chaoyang District Sunshine Garden Building A',39.904200,116.407400,'AC,Elevator,Parking',0,1,12,'2025-06-09 00:03:58','2025-06-09 00:03:58',NULL,NULL,'2025-06-09 00:03:58.000000','2025-06-09 00:03:58.000000');

/*Table structure for table `property_images` */

DROP TABLE IF EXISTS `property_images`;

CREATE TABLE `property_images` (
  `property_id` bigint(20) NOT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  KEY `FKemw5i1cysiorfaxfba7tgtpiu` (`property_id`),
  CONSTRAINT `FKemw5i1cysiorfaxfba7tgtpiu` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `property_images` */

insert  into `property_images`(`property_id`,`image_url`) values (1,'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(1,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib='),(1,'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(1,'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1620626011761-996317b8d101?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1505142468610-359e7d316be0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(1,'https://images.unsplash.com/photo-1545324418-cc1a3fa10c00?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(1,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib='),(1,'https://images.unsplash.com/photo-1560448204-e02f11c3d0e2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(1,'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(2,'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1512917774080-9991f1c4c750?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(3,'https://images.unsplash.com/photo-1620626011761-996317b8d101?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1505142468610-359e7d316be0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1449824913935-59a10b8d2000?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80'),(4,'https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80');

/*Table structure for table `provinces` */

DROP TABLE IF EXISTS `provinces`;

CREATE TABLE `provinces` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL COMMENT '省份名称',
  `code` varchar(20) NOT NULL COMMENT '省份代码',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_code` (`code`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='省份表';

/*Data for the table `provinces` */

insert  into `provinces`(`id`,`name`,`code`,`create_time`,`update_time`) values (1,'北京市','110000','2025-06-05 10:12:38','2025-06-05 10:12:38'),(2,'上海市','310000','2025-06-05 10:12:38','2025-06-05 10:12:38'),(3,'广东省','440000','2025-06-05 10:12:38','2025-06-05 10:12:38'),(4,'江苏省','320000','2025-06-05 10:12:38','2025-06-05 10:12:38'),(5,'浙江省','330000','2025-06-05 10:12:38','2025-06-05 10:12:38');

/*Table structure for table `rental_contracts` */

DROP TABLE IF EXISTS `rental_contracts`;

CREATE TABLE `rental_contracts` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `tenant_id` bigint(20) NOT NULL,
  `landlord_id` bigint(20) NOT NULL,
  `room_id` bigint(20) NOT NULL,
  `contract_number` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `monthly_rent` decimal(10,2) NOT NULL,
  `deposit` decimal(10,2) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'ACTIVE',
  `contract_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `contract_number` (`contract_number`),
  KEY `idx_tenant_id` (`tenant_id`),
  KEY `idx_landlord_id` (`landlord_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `rental_contracts_ibfk_1` FOREIGN KEY (`tenant_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `rental_contracts_ibfk_2` FOREIGN KEY (`landlord_id`) REFERENCES `landlords` (`id`) ON DELETE CASCADE,
  CONSTRAINT `rental_contracts_ibfk_3` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `rental_contracts` */

/*Table structure for table `room_applications` */

DROP TABLE IF EXISTS `room_applications`;

CREATE TABLE `room_applications` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `room_id` bigint(20) NOT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `expected_move_in_date` date NOT NULL,
  `lease_duration` int(11) NOT NULL,
  `remarks` text COLLATE utf8mb4_unicode_ci,
  `review_remarks` text COLLATE utf8mb4_unicode_ci,
  `review_time` datetime DEFAULT NULL,
  `reviewer_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `room_applications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `room_applications_ibfk_2` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `room_applications` */

insert  into `room_applications`(`id`,`user_id`,`room_id`,`status`,`expected_move_in_date`,`lease_duration`,`remarks`,`review_remarks`,`review_time`,`reviewer_id`,`create_time`,`update_time`) values (1,2,1,'PENDING','2024-02-01',12,'甯屾湜鑳藉?绉熷埌杩欎釜鎴块棿锛屾垜鏄?竴涓?畨闈欑殑绉熷?',NULL,NULL,NULL,'2025-06-08 14:12:17','2025-06-08 14:12:17'),(2,3,2,'PENDING','2024-02-15',6,'鐭?湡绉熸埧闇?眰锛屽伐浣滆皟鍔',NULL,NULL,NULL,'2025-06-08 14:12:17','2025-06-08 14:12:17'),(3,46,2,'CANCELLED','2025-07-02',12,'距离工作地点比较近，比较方便',NULL,NULL,NULL,'2025-06-08 20:02:59','2025-06-08 22:16:49'),(4,2,2,'CANCELLED','2025-07-01',12,'????',NULL,NULL,NULL,'2025-06-08 20:07:06','2025-06-09 08:36:26'),(5,9,2,'PENDING','2025-06-10',12,'1234567891',NULL,NULL,NULL,'2025-06-08 23:44:37','2025-06-08 23:44:37');

/*Table structure for table `room_images` */

DROP TABLE IF EXISTS `room_images`;

CREATE TABLE `room_images` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `room_id` bigint(20) NOT NULL COMMENT '房间ID',
  `image_url` varchar(255) NOT NULL COMMENT '图片URL',
  `sort_order` int(11) NOT NULL DEFAULT '0' COMMENT '排序顺序',
  `is_cover` tinyint(1) DEFAULT '0' COMMENT '是否为封面图',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_is_cover` (`is_cover`),
  CONSTRAINT `fk_image_room` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COMMENT='房间图片表';

/*Data for the table `room_images` */

insert  into `room_images`(`id`,`room_id`,`image_url`,`sort_order`,`is_cover`,`create_time`,`update_time`) values (1,1,'https://images.unsplash.com/photo-1560448204-61dc36dc98c8?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:30:56','2025-06-07 19:50:48'),(2,1,'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:30:56','2025-06-07 19:45:14'),(3,1,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',3,0,'2025-06-07 14:30:56','2025-06-07 19:45:29'),(4,2,'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:30:56','2025-06-07 19:45:58'),(5,2,'https://images.unsplash.com/photo-1571087520281-f69465b65c46?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:30:56','2025-06-07 19:46:15'),(6,3,'https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:30:56','2025-06-07 19:46:33'),(7,3,'https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:30:56','2025-06-07 19:46:46'),(8,3,'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',3,0,'2025-06-07 14:30:56','2025-06-07 19:47:03'),(9,4,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:30:56','2025-06-07 19:47:21'),(10,4,'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:30:56','2025-06-07 19:47:35'),(11,7,'https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:30:56','2025-06-07 19:47:55'),(12,7,'https://images.unsplash.com/photo-1571087520281-f69465b65c46?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:30:56','2025-06-07 19:48:15'),(13,1,'https://images.unsplash.com/photo-1560448204-61dc36dc98c8?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:31:04','2025-06-07 19:50:20'),(14,1,'https://images.unsplash.com/photo-1556228578-0d85b1a4d571?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(15,1,'https://images.unsplash.com/photo-1586023492125-27b2c045efd7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',3,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(16,2,'https://images.unsplash.com/photo-1505693416388-ac5ce068fe85?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(17,2,'https://images.unsplash.com/photo-1571087520281-f69465b65c46?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(18,3,'https://images.unsplash.com/photo-1484154218962-a197022b5858?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(19,3,'https://images.unsplash.com/photo-1567767292278-a4f21aa2d36e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(20,3,'https://images.unsplash.com/photo-1556909114-f6e7ad7d3136?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',3,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(21,4,'https://images.unsplash.com/photo-1616594039964-ae9021a400a0?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(22,4,'https://images.unsplash.com/photo-1502672260266-1c1ef2d93688?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(23,7,'https://images.unsplash.com/photo-1578662996442-48f60103fc96?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',1,1,'2025-06-07 14:31:04','2025-06-07 19:50:48'),(24,7,'https://images.unsplash.com/photo-1571087520281-f69465b65c46?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80',2,0,'2025-06-07 14:31:04','2025-06-07 19:50:48');

/*Table structure for table `rooms` */

DROP TABLE IF EXISTS `rooms`;

CREATE TABLE `rooms` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `property_id` bigint(20) NOT NULL COMMENT '所属物业ID',
  `room_number` varchar(50) NOT NULL COMMENT '房间号',
  `floor` int(11) DEFAULT '1' COMMENT '所在楼层',
  `area` decimal(10,2) DEFAULT '0.00' COMMENT '面积(平方米)',
  `room_type` varchar(20) DEFAULT 'STANDARD' COMMENT '房型',
  `layout` varchar(20) DEFAULT '1室1厅' COMMENT '户型',
  `orientation` varchar(20) DEFAULT 'SOUTH' COMMENT '朝向',
  `rent_price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '租金(元/月)',
  `deposit` decimal(10,2) DEFAULT '0.00' COMMENT '押金(元)',
  `payment_method` varchar(20) DEFAULT 'MONTHLY' COMMENT '支付方式',
  `status` varchar(20) NOT NULL DEFAULT 'VACANT' COMMENT '状态',
  `facilities` text COMMENT '配套设施',
  `description` text COMMENT '房间描述',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_property_room` (`property_id`,`room_number`),
  KEY `idx_property_id` (`property_id`),
  KEY `idx_status` (`status`),
  KEY `idx_rent_price` (`rent_price`),
  KEY `idx_area` (`area`),
  KEY `idx_room_rent_price` (`rent_price`),
  KEY `idx_room_area` (`area`),
  KEY `idx_room_status` (`status`),
  KEY `idx_room_floor` (`floor`),
  KEY `idx_room_type` (`room_type`),
  CONSTRAINT `fk_room_property` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=105 DEFAULT CHARSET=utf8mb4 COMMENT='房间表';

/*Data for the table `rooms` */

insert  into `rooms`(`id`,`property_id`,`room_number`,`floor`,`area`,`room_type`,`layout`,`orientation`,`rent_price`,`deposit`,`payment_method`,`status`,`facilities`,`description`,`create_time`,`update_time`) values (1,1,'A801',8,'32.50','主卧','2室1厅','南','2000.00','2000.00','MONTHLY','RENTED','{\"bed\": \"双人床\", \"wardrobe\": \"大衣柜\", \"desk\": \"学习桌\", \"chair\": \"椅子\"}','主卧室，采光良好，家具齐全','2025-06-07 14:30:56','2025-06-07 20:13:07'),(2,1,'A802',8,'28.00','次卧','2室1厅','北','1500.00','1500.00','MONTHLY','VACANT','{\"bed\": \"单人床\", \"wardrobe\": \"衣柜\", \"desk\": \"学习桌\"}','次卧室，安静舒适','2025-06-07 14:30:56','2025-06-07 14:30:56'),(3,2,'B501',5,'45.20','一居室','1室1厅','南','2800.00','2800.00','MONTHLY','RENTED','{\"bed\": \"双人床\", \"sofa\": \"沙发\", \"tv\": \"电视\", \"desk\": \"书桌\"}','独立一居室，适合单身或情侣','2025-06-07 14:30:56','2025-06-07 20:13:07'),(4,3,'C1201',12,'26.00','主卧','3室2厅','南','2500.00','2500.00','MONTHLY','RENTED','{\"bed\": \"双人床\", \"wardrobe\": \"大衣柜\", \"balcony\": \"阳台\"}','主卧带阳台，视野开阔','2025-06-07 14:30:56','2025-06-07 20:13:07'),(5,3,'C1202',12,'22.00','次卧','3室2厅','北','2000.00','2000.00','MONTHLY','VACANT','{\"bed\": \"单人床\", \"wardrobe\": \"衣柜\", \"desk\": \"书桌\"}','次卧室，采光充足','2025-06-07 14:30:56','2025-06-07 14:30:56'),(6,3,'C1203',12,'18.00','小房间','3室2厅','西','1700.00','1700.00','MONTHLY','VACANT','{\"bed\": \"单人床\", \"desk\": \"书桌\", \"chair\": \"椅子\"}','小房间，适合学生','2025-06-07 14:30:56','2025-06-07 14:30:56'),(7,4,'D101',1,'35.00','主房','4室2厅','南','3000.00','3000.00','MONTHLY','VACANT','{\"bed\": \"古式大床\", \"wardrobe\": \"实木衣柜\", \"tea_table\": \"茶桌\"}','正房主卧，传统装修风格','2025-06-07 14:30:56','2025-06-07 20:12:27'),(8,4,'D102',1,'28.00','东厢房','4室2厅','东','2500.00','2500.00','MONTHLY','VACANT','{\"bed\": \"双人床\", \"wardrobe\": \"衣柜\", \"window\": \"窗台\"}','东厢房，晨光充足','2025-06-07 14:30:56','2025-06-07 14:30:56'),(9,4,'D103',1,'25.00','西厢房','4室2厅','西','2300.00','2300.00','MONTHLY','VACANT','{\"bed\": \"单人床\", \"desk\": \"书桌\", \"bookshelf\": \"书架\"}','西厢房，适合读书','2025-06-07 14:30:56','2025-06-07 14:30:56'),(101,1,'A101',1,'25.50','STANDARD','1室1厅','SOUTH','2500.00','2500.00','MONTHLY','VACANT',NULL,'测试房间A101','2025-06-07 20:03:05','2025-06-07 20:03:05'),(102,1,'A102',1,'30.00','DELUXE','1室1厅','NORTH','2800.00','2800.00','MONTHLY','VACANT',NULL,'测试房间A102','2025-06-07 20:03:05','2025-06-07 20:03:05'),(103,1,'A103',1,'28.00','STANDARD','1室1厅','EAST','2600.00','2600.00','MONTHLY','VACANT',NULL,'测试房间A103','2025-06-07 20:03:05','2025-06-07 20:03:05'),(104,4,'F101',1,'100.00','SUITE',NULL,'SOUTH','5000.00',NULL,'MONTHLY','MAINTENANCE',NULL,'','2025-06-07 20:06:51','2025-06-07 23:42:48');

/*Table structure for table `system_configs` */

DROP TABLE IF EXISTS `system_configs`;

CREATE TABLE `system_configs` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `config_key` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `config_value` text COLLATE utf8mb4_unicode_ci,
  `config_type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime NOT NULL,
  `update_time` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_config_key` (`config_key`),
  KEY `idx_config_type` (`config_type`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `system_configs` */

insert  into `system_configs`(`id`,`config_key`,`config_value`,`config_type`,`description`,`create_time`,`update_time`) values (1,'site.name','房屋租赁管理系统','SYSTEM','系统名称','2025-06-05 10:07:11','2025-06-05 10:07:11'),(2,'site.logo','@/assets/images/logo.png','SYSTEM','系统Logo','2025-06-05 10:07:11','2025-06-05 10:07:11'),(3,'site.description','专业的房屋租赁管理系统','SYSTEM','系统描述','2025-06-05 10:07:11','2025-06-05 10:07:11'),(4,'utility.water.rate','5.00','FEE','水费费率(元/吨)','2025-06-05 10:07:11','2025-06-05 10:07:11'),(5,'utility.electricity.rate','0.50','FEE','电费费率(元/度)','2025-06-05 10:07:11','2025-06-05 10:07:11'),(6,'utility.gas.rate','3.00','FEE','燃气费费率(元/立方)','2025-06-05 10:07:11','2025-06-05 10:07:11'),(7,'deposit.rate','1.0','FEE','押金系数(月租金的倍数)','2025-06-05 10:07:11','2025-06-05 10:07:11'),(8,'payment.methods','ALIPAY,WECHAT,BANK_TRANSFER','PAYMENT','支付方式','2025-06-05 10:07:11','2025-06-05 10:07:11'),(9,'contract.template','标准租赁合同模板','TEMPLATE','合同模板','2025-06-05 10:07:11','2025-06-05 10:07:11'),(10,'notification.enabled','true','NOTIFICATION','是否启用通知','2025-06-05 10:07:11','2025-06-05 10:07:11'),(11,'maintenance.auto.assign','false','MAINTENANCE','是否自动分配维修任务','2025-06-05 10:07:11','2025-06-05 10:07:11'),(12,'bill.auto.generate','true','BILL','是否自动生成账单','2025-06-05 10:07:11','2025-06-05 10:07:11'),(25,'job.generate_bills.cron','0 0 1 1 * ?','JOB','生成月度账单任务Cron表达式(每月1日凌晨执行)','2025-06-05 10:12:38','2025-06-05 10:12:38'),(26,'job.check_overdue.cron','0 0 9 * * ?','JOB','检查逾期账单任务Cron表达式(每天早上9点执行)','2025-06-05 10:12:38','2025-06-05 10:12:38'),(27,'job.contract_reminder.cron','0 0 10 * * ?','JOB','合同到期提醒任务Cron表达式(每天早上10点执行)','2025-06-05 10:12:38','2025-06-05 10:12:38'),(28,'job.update_room_status.cron','0 0 2 * * ?','JOB','更新房间状态任务Cron表达式(每天凌晨2点执行)','2025-06-05 10:12:38','2025-06-05 10:12:38');

/*Table structure for table `tenants` */

DROP TABLE IF EXISTS `tenants`;

CREATE TABLE `tenants` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL COMMENT '关联用户ID',
  `real_name` varchar(50) NOT NULL COMMENT '真实姓名',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `occupation` varchar(100) DEFAULT NULL COMMENT '职业',
  `work_place` varchar(255) DEFAULT NULL COMMENT '工作单位',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_id` (`user_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  CONSTRAINT `fk_tenant_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COMMENT='租客表';

/*Data for the table `tenants` */

insert  into `tenants`(`id`,`user_id`,`real_name`,`id_card`,`emergency_contact`,`emergency_phone`,`occupation`,`work_place`,`create_time`,`update_time`) values (1,8,'王小明','110101199001011234','王大明','13900001001','软件工程师','北京科技有限公司','2025-06-07 14:30:56','2025-06-07 14:30:56'),(2,9,'赵小红','110101199102012345','赵大红','13900002002','会计师','北京会计事务所','2025-06-07 14:30:56','2025-06-07 14:30:56'),(3,10,'刘小刚','110101199203013456','刘大刚','13900003003','销售经理','北京贸易公司','2025-06-07 14:30:56','2025-06-07 14:30:56'),(4,11,'陈小丽','110101199304014567','陈大丽','13900004004','教师','北京第一中学','2025-06-07 14:30:56','2025-06-07 14:30:56');

/*Table structure for table `user_feedback` */

DROP TABLE IF EXISTS `user_feedback`;

CREATE TABLE `user_feedback` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `title` varchar(200) COLLATE utf8mb4_unicode_ci NOT NULL,
  `content` text COLLATE utf8mb4_unicode_ci NOT NULL,
  `contact_info` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `status` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL DEFAULT 'PENDING',
  `reply` text COLLATE utf8mb4_unicode_ci,
  `reply_time` datetime DEFAULT NULL,
  `replier_id` bigint(20) DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_type` (`type`),
  KEY `idx_status` (`status`),
  CONSTRAINT `user_feedback_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `user_feedback` */

insert  into `user_feedback`(`id`,`user_id`,`type`,`title`,`content`,`contact_info`,`status`,`reply`,`reply_time`,`replier_id`,`create_time`,`update_time`) values (1,2,'SUGGESTION','寤鸿?澧炲姞鍦ㄧ嚎鏀?粯鍔熻兘','甯屾湜鑳藉?鏀?寔寰?俊鍜屾敮浠樺疂鍦ㄧ嚎鏀?粯鎴跨?','13800138001','PENDING',NULL,NULL,NULL,'2025-06-08 14:12:17','2025-06-08 14:12:17'),(2,3,'COMPLAINT','鎴块棿璁炬柦闂??','鎴块棿鐨勭┖璋冨埗鍐锋晥鏋滀笉濂斤紝甯屾湜鑳藉?缁翠慨','13800138002','PENDING',NULL,NULL,NULL,'2025-06-08 14:12:17','2025-06-08 14:12:17');

/*Table structure for table `users` */

DROP TABLE IF EXISTS `users`;

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  `avatar_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `email` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_login_time` datetime(6) DEFAULT NULL,
  `password` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `phone` varchar(20) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `role` varchar(20) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` int(11) NOT NULL,
  `username` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `created_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` datetime(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_du5v5sr43g5bfnji4vb8hg5s3` (`phone`),
  KEY `idx_user_status` (`status`),
  KEY `idx_user_role` (`role`),
  KEY `idx_user_last_login` (`last_login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=203 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `users` */

insert  into `users`(`id`,`create_time`,`update_time`,`avatar_url`,`email`,`last_login_time`,`password`,`phone`,`role`,`status`,`username`,`created_at`,`updated_at`) values (1,'2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png','admin@example.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13800000000','ADMIN',1,'admin','0000-00-00 00:00:00.000000','2025-06-07 23:53:02.857579'),(2,'2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000','https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png','user1@example.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13800000001','USER',1,'user1','2025-06-05 10:12:38.000000','2025-06-08 13:39:42.612501'),(3,'2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000','https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png','user2@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800000002','USER',1,'user2','2025-06-05 10:12:38.000000','2025-06-07 23:53:02.857579'),(4,'2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000','https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48png.png','landlord1@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800000003','LANDLORD',1,'landlord1','2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000'),(5,'2025-06-05 10:12:38.000000','2025-06-05 10:12:38.000000','https://cube.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg','landlord2@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800000004','LANDLORD',1,'landlord2','2025-06-05 10:12:38.000000','2025-06-07 23:53:02.857579'),(6,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg','zhang@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800001001','LANDLORD',1,'zhang_landlord','2025-06-07 14:30:56.000000','2025-06-07 23:53:02.857579'),(7,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','https://fuss10.elemecdn.com/1/8e/aeffeb4de74e2fde4bd74fc7b4486jpeg.jpeg','li@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800001002','LANDLORD',1,'li_landlord','2025-06-07 14:30:56.000000','2025-06-07 23:53:02.857579'),(8,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','https://fuss10.elemecdn.com/8/27/f01c15bb73e1ef3793e64e6b7bbccjpeg.jpeg','wang@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800002001','USER',1,'wang_tenant','2025-06-07 14:30:56.000000','2025-06-07 23:53:02.857579'),(9,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','https://fuss10.elemecdn.com/9/bb/e27858e973f5d7d3904835f46abbdjpeg.jpeg','zhao@example.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13800002002','USER',1,'zhao_tenant','2025-06-07 14:30:56.000000','2025-06-08 22:48:39.579690'),(10,'2025-06-07 14:30:56.000000','2025-06-09 08:41:05.329000','/files/avatars/20250609/02d2fdb9-fec6-4bfb-b410-1a4ea21d7363.jpeg','liu@example.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13800002003','USER',1,'liu_tenant','2025-06-07 14:30:56.000000','2025-06-09 08:41:05.329000'),(11,'2025-06-07 14:30:56.000000','2025-06-07 14:30:56.000000','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png','chen@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800002004','USER',1,'chen_tenant','2025-06-07 14:30:56.000000','2025-06-07 23:53:02.857579'),(12,'2025-06-07 14:31:47.000000','2025-06-07 14:31:47.000000','https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png','wangqiang@example.com',NULL,'$2a$10$ySG2lkvjFHY5O0./CPIE1OI8VJsuKYEzOYzqIa7AJR6sEgSzUFOAm','13800001012','USER',1,'wang_qiang','2025-06-07 14:31:47.000000','2025-06-07 14:31:47.000000'),(44,'2025-06-07 15:09:04.172000','2025-06-07 15:09:04.172000','https://cube.elemecdn.com/6/94/4d3ea53c084bad6931a56d5158a48png.png','456@qq.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13145678912','ADMIN',1,'wff','2025-06-07 15:09:04.172000','2025-06-07 23:53:02.857579'),(45,'2025-06-07 15:33:43.179000','2025-06-07 15:34:02.652000','https://cube.elemecdn.com/a/3f/3302e58f9a181d2509f3dc0fa68b0jpeg.jpeg','111111@qq.com',NULL,'$2a$10$h5UNFV125HGlB6KKhWI3.eRPlVWIRjtDBRRUV34KK58ZRFIh2FZqe','13212321333','USER',0,'张三儿','2025-06-07 15:33:43.179000','2025-06-07 23:53:02.857579'),(46,'2025-06-07 15:38:57.074000','2025-06-07 15:38:57.074000','https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg','132@qq.com',NULL,'$2a$10$edEaEfKsuN0Ogy0LA17XN.4.kyZ5qSZt.mN2wt7ACd/1N4tyuc2ie','13221344325','USER',1,'小小小','2025-06-07 15:38:57.074000','2025-06-07 23:53:02.857579'),(101,'2025-06-07 15:57:17.000000','2025-06-07 15:57:17.000000','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png','user@example.com',NULL,'$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8ioctKQVkwrjJJJJJJJJJJJJJJJJJJ','13800138001','TENANT',1,'user','2025-06-07 15:57:17.869218','2025-06-07 23:53:02.857579'),(201,'2025-06-07 20:03:05.000000','2025-06-07 20:03:05.000000','https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png','tenant1@example.com',NULL,'$2a$10$kK7cO6a5B3NPMjlxEfC7ZOLgETgKEvKVPWS75k0HQN4zSmPtcsCXu','13800138201','USER',1,'tenant1','2025-06-07 20:03:05.634760','2025-06-09 00:35:53.126105'),(202,'2025-06-07 20:03:05.000000','2025-06-07 20:03:05.000000','https://cube.elemecdn.com/9/c2/f0ee8a3c7c9638a54940382568c9dpng.png','tenant2@example.com',NULL,'$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.','13800138202','TENANT',1,'tenant2','2025-06-07 20:03:05.634760','2025-06-07 23:53:02.857579');

/*Table structure for table `utility_bills` */

DROP TABLE IF EXISTS `utility_bills`;

CREATE TABLE `utility_bills` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bill_no` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT 'NULL',
  `user_id` bigint(20) NOT NULL,
  `property_id` bigint(20) NOT NULL,
  `landlord_id` bigint(20) NOT NULL,
  `type` int(11) NOT NULL,
  `month` varchar(7) COLLATE utf8mb4_unicode_ci NOT NULL,
  `last_reading` decimal(10,2) DEFAULT '0.00',
  `current_reading` decimal(10,2) DEFAULT NULL,
  `usage_amount` decimal(10,2) DEFAULT NULL,
  `price` decimal(5,2) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  `payment_time` datetime DEFAULT NULL,
  `reading_time` datetime DEFAULT NULL,
  `remark` text COLLATE utf8mb4_unicode_ci,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `created_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `create_time` datetime(6) DEFAULT NULL,
  `update_time` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_bill_user` (`user_id`),
  KEY `fk_bill_property` (`property_id`),
  KEY `fk_bill_landlord` (`landlord_id`),
  CONSTRAINT `fk_bill_landlord` FOREIGN KEY (`landlord_id`) REFERENCES `landlords` (`id`),
  CONSTRAINT `fk_bill_property` FOREIGN KEY (`property_id`) REFERENCES `properties` (`id`),
  CONSTRAINT `fk_bill_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

/*Data for the table `utility_bills` */

insert  into `utility_bills`(`id`,`bill_no`,`user_id`,`property_id`,`landlord_id`,`type`,`month`,`last_reading`,`current_reading`,`usage_amount`,`price`,`amount`,`status`,`payment_time`,`reading_time`,`remark`,`created_at`,`updated_at`,`created_by`,`updated_by`,`create_time`,`update_time`) values (1,'UB202401001',3,1,1,1,'2024-01','1200.50','1250.30','49.80','2.50','124.50',1,'2024-01-25 10:30:00','2024-01-01 09:00:00','Jan water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(2,'UB202401002',3,1,1,2,'2024-01','8500.20','8650.80','150.60','0.65','97.89',1,'2024-01-25 10:35:00','2024-01-01 09:15:00','Jan electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(3,'UB202401003',3,1,1,3,'2024-01','450.30','465.80','15.50','3.20','49.60',1,'2024-01-25 10:40:00','2024-01-01 09:30:00','Jan gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(4,'UB202401004',4,2,1,1,'2024-01','980.20','1025.60','45.40','2.50','113.50',1,'2024-01-26 14:20:00','2024-01-01 10:00:00','Jan water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(5,'UB202401005',4,2,1,2,'2024-01','7200.50','7320.80','120.30','0.65','78.20',1,'2024-01-26 14:25:00','2024-01-01 10:15:00','Jan electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(6,'UB202401006',4,2,1,3,'2024-01','380.60','395.20','14.60','3.20','46.72',1,'2024-01-26 14:30:00','2024-01-01 10:30:00','Jan gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(7,'UB202401007',5,3,1,1,'2024-01','1350.80','1405.20','54.40','2.50','136.00',1,'2024-01-27 16:10:00','2024-01-01 11:00:00','Jan water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(8,'UB202401008',5,3,1,2,'2024-01','9200.30','9380.90','180.60','0.65','117.39',1,'2024-01-27 16:15:00','2024-01-01 11:15:00','Jan electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(9,'UB202401009',5,3,1,3,'2024-01','520.40','538.90','18.50','3.20','59.20',1,'2024-01-27 16:20:00','2024-01-01 11:30:00','Jan gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(10,'UB202402001',3,1,1,1,'2024-02','1250.30','1298.70','48.40','2.50','121.00',1,'2024-02-25 11:20:00','2024-02-01 09:00:00','Feb water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(11,'UB202402002',3,1,1,2,'2024-02','8650.80','8795.40','144.60','0.65','93.99',1,'2024-02-25 11:25:00','2024-02-01 09:15:00','Feb electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(12,'UB202402003',3,1,1,3,'2024-02','465.80','480.60','14.80','3.20','47.36',1,'2024-02-25 11:30:00','2024-02-01 09:30:00','Feb gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(13,'UB202402004',4,2,1,1,'2024-02','1025.60','1068.90','43.30','2.50','108.25',1,'2024-02-26 15:10:00','2024-02-01 10:00:00','Feb water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(14,'UB202402005',4,2,1,2,'2024-02','7320.80','7435.20','114.40','0.65','74.36',1,'2024-02-26 15:15:00','2024-02-01 10:15:00','Feb electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(15,'UB202402006',4,2,1,3,'2024-02','395.20','408.90','13.70','3.20','43.84',1,'2024-02-26 15:20:00','2024-02-01 10:30:00','Feb gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(16,'UB202402007',5,3,1,1,'2024-02','1405.20','1456.80','51.60','2.50','129.00',1,'2024-02-27 17:30:00','2024-02-01 11:00:00','Feb water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(17,'UB202402008',5,3,1,2,'2024-02','9380.90','9545.30','164.40','0.65','106.86',1,'2024-02-27 17:35:00','2024-02-01 11:15:00','Feb electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(18,'UB202402009',5,3,1,3,'2024-02','538.90','555.70','16.80','3.20','53.76',1,'2024-02-27 17:40:00','2024-02-01 11:30:00','Feb gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(19,'UB202403001',3,1,1,1,'2024-03','1298.70','1345.20','46.50','2.50','116.25',1,'2024-03-25 12:10:00','2024-03-01 09:00:00','Mar water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(20,'UB202403002',3,1,1,2,'2024-03','8795.40','8932.80','137.40','0.65','89.31',1,'2024-03-25 12:15:00','2024-03-01 09:15:00','Mar electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(21,'UB202403003',3,1,1,3,'2024-03','480.60','494.90','14.30','3.20','45.76',1,'2024-03-25 12:20:00','2024-03-01 09:30:00','Mar gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(22,'UB202403004',4,2,1,1,'2024-03','1068.90','1110.40','41.50','2.50','103.75',1,'2024-03-26 16:00:00','2024-03-01 10:00:00','Mar water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(23,'UB202403005',4,2,1,2,'2024-03','7435.20','7542.60','107.40','0.65','69.81',1,'2024-03-26 16:05:00','2024-03-01 10:15:00','Mar electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(24,'UB202403006',4,2,1,3,'2024-03','408.90','421.80','12.90','3.20','41.28',1,'2024-03-26 16:10:00','2024-03-01 10:30:00','Mar gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(25,'UB202403007',5,3,1,1,'2024-03','1456.80','1506.30','49.50','2.50','123.75',1,'2024-03-27 18:20:00','2024-03-01 11:00:00','Mar water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(26,'UB202403008',5,3,1,2,'2024-03','9545.30','9702.70','157.40','0.65','102.31',1,'2024-03-27 18:25:00','2024-03-01 11:15:00','Mar electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(27,'UB202403009',5,3,1,3,'2024-03','555.70','571.90','16.20','3.20','51.84',1,'2024-03-27 18:30:00','2024-03-01 11:30:00','Mar gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(28,'UB202404001',3,1,1,1,'2024-04','1345.20','1390.80','45.60','2.50','114.00',1,'2025-06-07 22:56:18','2024-04-01 09:00:00','Apr water bill','2025-06-07 22:49:10','2025-06-07 22:56:18',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:56:18.169000'),(29,'UB202404002',3,1,1,2,'2024-04','8932.80','9065.20','132.40','0.65','86.06',0,NULL,'2024-04-01 09:15:00','Apr electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(30,'UB202404003',3,1,1,3,'2024-04','494.90','508.60','13.70','3.20','43.84',1,'2024-04-25 13:30:00','2024-04-01 09:30:00','Apr gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(31,'UB202404004',4,2,1,1,'2024-04','1110.40','1150.90','40.50','2.50','101.25',1,'2024-04-26 17:10:00','2024-04-01 10:00:00','Apr water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(32,'UB202404005',4,2,1,2,'2024-04','7542.60','7645.80','103.20','0.65','67.08',0,NULL,'2024-04-01 10:15:00','Apr electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(33,'UB202404006',4,2,1,3,'2024-04','421.80','434.20','12.40','3.20','39.68',0,NULL,'2024-04-01 10:30:00','Apr gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(34,'UB202404007',5,3,1,1,'2024-04','1506.30','1554.80','48.50','2.50','121.25',0,NULL,'2024-04-01 11:00:00','Apr water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(35,'UB202404008',5,3,1,2,'2024-04','9702.70','9855.30','152.60','0.65','99.19',1,'2024-04-27 19:15:00','2024-04-01 11:15:00','Apr electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(36,'UB202404009',5,3,1,3,'2024-04','571.90','587.40','15.50','3.20','49.60',0,NULL,'2024-04-01 11:30:00','Apr gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(37,'UB202405001',3,1,1,1,'2024-05','1390.80','1435.60','44.80','2.50','112.00',0,NULL,'2024-05-01 09:00:00','May water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(38,'UB202405002',3,1,1,2,'2024-05','9065.20','9192.80','127.60','0.65','82.94',0,NULL,'2024-05-01 09:15:00','May electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(39,'UB202405003',3,1,1,3,'2024-05','508.60','521.90','13.30','3.20','42.56',0,NULL,'2024-05-01 09:30:00','May gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(40,'UB202405004',4,2,1,1,'2024-05','1150.90','1190.20','39.30','2.50','98.25',0,NULL,'2024-05-01 10:00:00','May water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(41,'UB202405005',4,2,1,2,'2024-05','7645.80','7745.60','99.80','0.65','64.87',0,NULL,'2024-05-01 10:15:00','May electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(42,'UB202405006',4,2,1,3,'2024-05','434.20','446.10','11.90','3.20','38.08',0,NULL,'2024-05-01 10:30:00','May gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(43,'UB202405007',5,3,1,1,'2024-05','1554.80','1602.30','47.50','2.50','118.75',0,NULL,'2024-05-01 11:00:00','May water bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(44,'UB202405008',5,3,1,2,'2024-05','9855.30','10003.90','148.60','0.65','96.59',0,NULL,'2024-05-01 11:15:00','May electricity bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(45,'UB202405009',5,3,1,3,'2024-05','587.40','602.20','14.80','3.20','47.36',0,NULL,'2024-05-01 11:30:00','May gas bill','2025-06-07 22:49:10','2025-06-07 22:49:10',NULL,NULL,'2025-06-07 22:49:10.000000','2025-06-07 22:49:10.000000'),(46,'UB202410009',9,2,1,1,'2024-10','1200.50','1250.30','49.80','2.50','124.50',0,NULL,'2024-10-01 09:00:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(47,'UB202410010',9,2,1,2,'2024-10','8500.20','8650.80','150.60','0.65','97.89',0,NULL,'2024-10-01 09:15:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(48,'UB202410011',9,2,1,3,'2024-10','450.30','465.80','15.50','3.20','49.60',0,NULL,'2024-10-01 09:30:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(49,'UB202411009',9,2,1,1,'2024-11','1250.30','1295.60','45.30','2.50','113.25',0,NULL,'2024-11-01 09:00:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(50,'UB202411010',9,2,1,2,'2024-11','8650.80','8780.40','129.60','0.65','84.24',0,NULL,'2024-11-01 09:15:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(51,'UB202411011',9,2,1,3,'2024-11','465.80','478.90','13.10','3.20','41.92',0,NULL,'2024-11-01 09:30:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(52,'UB202412009',9,2,1,1,'2024-12','1295.60','1340.20','44.60','2.50','111.50',0,NULL,'2024-12-01 09:00:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(53,'UB202412010',9,2,1,2,'2024-12','8780.40','8920.80','140.40','0.65','91.26',0,NULL,'2024-12-01 09:15:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(54,'UB202412011',9,2,1,3,'2024-12','478.90','492.30','13.40','3.20','42.88',0,NULL,'2024-12-01 09:30:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(55,'UB202501009',9,2,1,1,'2025-01','1340.20','1385.80','45.60','2.50','114.00',0,NULL,'2025-01-01 09:00:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(56,'UB202501010',9,2,1,2,'2025-01','8920.80','9065.20','144.40','0.65','93.86',0,NULL,'2025-01-01 09:15:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000'),(57,'UB202501011',9,2,1,3,'2025-01','492.30','506.80','14.50','3.20','46.40',0,NULL,'2025-01-01 09:30:00',NULL,'2025-06-09 00:28:55','2025-06-09 00:28:55',NULL,NULL,'2025-06-09 00:28:55.000000','2025-06-09 00:28:55.000000');

/* Trigger structure for table `bills` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_bill_update` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_bill_update` AFTER UPDATE ON `bills` FOR EACH ROW BEGIN
    DECLARE v_tenant_id BIGINT;
    DECLARE v_landlord_id BIGINT;
    
    -- 如果账单状态从未支付变为已支付，则创建通知
    IF OLD.status IN ('UNPAID', 'OVERDUE') AND NEW.status = 'PAID' THEN
        -- 获取合同信息
        SELECT tenant_id, landlord_id INTO v_tenant_id, v_landlord_id
        FROM contracts
        WHERE id = NEW.contract_id;
        
        -- 创建支付成功通知给租客
        -- 这里添加插入通知的代码
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `contracts` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `before_contract_insert` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `before_contract_insert` BEFORE INSERT ON `contracts` FOR EACH ROW BEGIN
    -- 检查是否违反了一个房间只能有一个活跃合同的规则
    IF NEW.status = 'ACTIVE' AND NOT can_create_active_contract(NEW.room_id, NULL) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '该房间已有活跃的租房合同，不能创建新的活跃合同';
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `contracts` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_contract_insert` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_contract_insert` AFTER INSERT ON `contracts` FOR EACH ROW BEGIN
    -- 如果是活跃合同，更新房间状态为已租
    IF NEW.status = 'ACTIVE' AND NEW.start_date <= CURDATE() AND NEW.end_date >= CURDATE() THEN
        UPDATE rooms SET status = 'RENTED', update_time = NOW() WHERE id = NEW.room_id;
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `contracts` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `before_contract_update` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `before_contract_update` BEFORE UPDATE ON `contracts` FOR EACH ROW BEGIN
    -- 检查是否违反了一个房间只能有一个活跃合同的规则
    IF NEW.status = 'ACTIVE' AND OLD.status != 'ACTIVE' AND NOT can_create_active_contract(NEW.room_id, NEW.id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = '该房间已有活跃的租房合同，不能将此合同设为活跃状态';
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `contracts` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_contract_update` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_contract_update` AFTER UPDATE ON `contracts` FOR EACH ROW BEGIN
    DECLARE active_count INT DEFAULT 0;
    
    -- 统计房间的活跃合同数
    SELECT COUNT(*) INTO active_count
    FROM contracts 
    WHERE room_id = NEW.room_id 
      AND status = 'ACTIVE'
      AND start_date <= CURDATE()
      AND end_date >= CURDATE();
    
    -- 根据活跃合同数更新房间状态
    IF active_count > 0 THEN
        UPDATE rooms SET status = 'RENTED', update_time = NOW() WHERE id = NEW.room_id;
    ELSE
        UPDATE rooms SET status = 'VACANT', update_time = NOW() WHERE id = NEW.room_id AND status != 'MAINTENANCE';
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `landlords` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_landlord_verify` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_landlord_verify` AFTER UPDATE ON `landlords` FOR EACH ROW BEGIN
    -- 如果认证状态从未认证变为已认证，则创建通知
    IF OLD.verified = 0 AND NEW.verified = 1 THEN
        INSERT INTO notifications (
            user_id, title, content, notification_type, 
            related_id, is_read, create_time, update_time
        ) VALUES (
            NEW.user_id,
            '房东身份认证成功',
            '恭喜！您的房东身份已通过认证，现在可以发布公寓和房源了。',
            'SYSTEM',
            NULL,
            FALSE,
            NOW(),
            NOW()
        );
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `maintenance_records` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_maintenance_insert` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_maintenance_insert` AFTER INSERT ON `maintenance_records` FOR EACH ROW BEGIN
    -- 获取相关信息
    DECLARE v_room_number VARCHAR(20);
    DECLARE v_landlord_id BIGINT;
    
    SELECT r.room_number, p.landlord_id 
    INTO v_room_number, v_landlord_id
    FROM rooms r
    JOIN properties p ON r.property_id = p.id
    WHERE r.id = NEW.room_id;
    
    -- 创建维修通知给受理人(如果已分配)
    IF NEW.assignee_id IS NOT NULL THEN
        INSERT INTO notifications (
            user_id, title, content, notification_type, 
            related_id, is_read, create_time, update_time
        ) VALUES (
            NEW.assignee_id,
            '新的维修任务',
            CONCAT('您有一个新的维修任务，标题: ', NEW.title, 
                   '，房间号: ', v_room_number,
                   '，优先级: ', CASE NEW.priority
                              WHEN 'LOW' THEN '低'
                              WHEN 'MEDIUM' THEN '中'
                              WHEN 'HIGH' THEN '高'
                              WHEN 'URGENT' THEN '紧急'
                              ELSE '未知'
                           END),
            'MAINTENANCE',
            NEW.id,
            FALSE,
            NOW(),
            NOW()
        );
    END IF;
    
    -- 创建维修通知给房东
    INSERT INTO notifications (
        user_id, title, content, notification_type, 
        related_id, is_read, create_time, update_time
    ) VALUES (
        v_landlord_id,
        '新的维修请求',
        CONCAT('您的房间收到了一个维修请求，标题: ', NEW.title, 
               '，房间号: ', v_room_number,
               '，优先级: ', CASE NEW.priority
                          WHEN 'LOW' THEN '低'
                          WHEN 'MEDIUM' THEN '中'
                          WHEN 'HIGH' THEN '高'
                          WHEN 'URGENT' THEN '紧急'
                          ELSE '未知'
                       END),
        'MAINTENANCE',
        NEW.id,
        FALSE,
        NOW(),
        NOW()
    );
END */$$


DELIMITER ;

/* Trigger structure for table `maintenance_records` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_maintenance_update` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_maintenance_update` AFTER UPDATE ON `maintenance_records` FOR EACH ROW BEGIN
    -- 如果维修状态变为已完成，则创建通知
    IF OLD.status != 'COMPLETED' AND NEW.status = 'COMPLETED' THEN
        -- 创建维修完成通知给报修人
        INSERT INTO notifications (
            user_id, title, content, notification_type, 
            related_id, is_read, create_time, update_time
        ) VALUES (
            NEW.reporter_id,
            '维修已完成',
            CONCAT('您的维修请求已完成，标题: ', NEW.title, 
                   '，完成时间: ', DATE_FORMAT(NEW.completion_time, '%Y-%m-%d %H:%i'),
                   '，维修结果: ', IFNULL(NEW.maintenance_result, '无')),
            'MAINTENANCE',
            NEW.id,
            FALSE,
            NOW(),
            NOW()
        );
        
        -- 如果房间状态是维修中，则更新为空闲或已租
        UPDATE rooms r
        SET r.status = IF(EXISTS(
                SELECT 1 FROM contracts c
                WHERE c.room_id = r.id
                  AND c.status = 'ACTIVE'
                  AND c.start_date <= CURDATE()
                  AND c.end_date >= CURDATE()
            ), 'RENTED', 'VACANT'),
            r.update_time = NOW()
        WHERE r.id = NEW.room_id AND r.status = 'MAINTENANCE';
    END IF;
    
    -- 如果维修状态变为处理中，则更新房间状态为维修中
    IF OLD.status = 'PENDING' AND NEW.status = 'PROCESSING' THEN
        UPDATE rooms 
        SET status = 'MAINTENANCE', update_time = NOW() 
        WHERE id = NEW.room_id;
    END IF;
END */$$


DELIMITER ;

/* Trigger structure for table `meter_readings` */

DELIMITER $$

/*!50003 DROP TRIGGER*//*!50032 IF EXISTS */ /*!50003 `after_meter_reading_insert` */$$

/*!50003 CREATE */ /*!50017 DEFINER = 'root'@'localhost' */ /*!50003 TRIGGER `after_meter_reading_insert` AFTER INSERT ON `meter_readings` FOR EACH ROW BEGIN
    -- 计算账单月份
    DECLARE v_bill_month VARCHAR(7);
    SET v_bill_month = DATE_FORMAT(NEW.reading_date, '%Y-%m');
    
    -- 调用存储过程计算费用
    CALL calculate_utility_fees(NEW.room_id, v_bill_month);
END */$$


DELIMITER ;

/* Function  structure for function  `can_create_active_contract` */

/*!50003 DROP FUNCTION IF EXISTS `can_create_active_contract` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `can_create_active_contract`(p_room_id BIGINT, p_contract_id BIGINT) RETURNS tinyint(1)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE v_count INT DEFAULT 0;
    
    -- 检查是否已经存在其他活跃合同
    SELECT COUNT(*) INTO v_count
    FROM contracts 
    WHERE room_id = p_room_id 
      AND status = 'ACTIVE'
      AND (p_contract_id IS NULL OR id != p_contract_id);
    
    RETURN (v_count = 0);
END */$$
DELIMITER ;

/* Function  structure for function  `get_active_contract_count` */

/*!50003 DROP FUNCTION IF EXISTS `get_active_contract_count` */;
DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` FUNCTION `get_active_contract_count`(room_id BIGINT) RETURNS int(11)
    READS SQL DATA
    DETERMINISTIC
BEGIN
    DECLARE contract_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO contract_count
    FROM contracts 
    WHERE room_id = room_id 
      AND status = 'ACTIVE'
      AND start_date <= CURDATE()
      AND end_date >= CURDATE();
    
    RETURN contract_count;
END */$$
DELIMITER ;

/* Procedure structure for procedure `check_overdue_bills` */

/*!50003 DROP PROCEDURE IF EXISTS  `check_overdue_bills` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `check_overdue_bills`()
BEGIN
    -- 更新所有已过期但未支付的账单状态为"逾期"
    UPDATE bills 
    SET status = 'OVERDUE', update_time = NOW()
    WHERE status = 'UNPAID' AND due_date < CURDATE();
    
    -- 为逾期账单创建通知
    INSERT INTO notifications (user_id, title, content, notification_type, related_id, is_read, create_time, update_time)
    SELECT 
        c.tenant_id AS user_id,
        CONCAT('账单 ', b.bill_number, ' 已逾期') AS title,
        CONCAT('您的', 
            CASE b.bill_type
                WHEN 'RENT' THEN '租金'
                WHEN 'WATER' THEN '水费'
                WHEN 'ELECTRICITY' THEN '电费'
                WHEN 'GAS' THEN '燃气费'
                ELSE '其他费用'
            END,
            '账单已逾期，请尽快支付。金额: ', b.amount, '元，到期日: ', DATE_FORMAT(b.due_date, '%Y-%m-%d')
        ) AS content,
        'BILL' AS notification_type,
        b.id AS related_id,
        FALSE AS is_read,
        NOW() AS create_time,
        NOW() AS update_time
    FROM bills b
    JOIN contracts c ON b.contract_id = c.id
    WHERE b.status = 'OVERDUE' 
      AND b.due_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY);
END */$$
DELIMITER ;

/* Procedure structure for procedure `contract_expiry_reminder` */

/*!50003 DROP PROCEDURE IF EXISTS  `contract_expiry_reminder` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `contract_expiry_reminder`()
BEGIN
    -- 为即将到期的合同(30天内)创建通知
    INSERT INTO notifications (user_id, title, content, notification_type, related_id, is_read, create_time, update_time)
    SELECT 
        c.tenant_id AS user_id,
        '租约即将到期提醒' AS title,
        CONCAT('您的租约将于 ', DATE_FORMAT(c.end_date, '%Y-%m-%d'), ' 到期，请及时续约或办理退租手续。') AS content,
        'CONTRACT' AS notification_type,
        c.id AS related_id,
        FALSE AS is_read,
        NOW() AS create_time,
        NOW() AS update_time
    FROM contracts c
    WHERE c.status = 'ACTIVE' 
      AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
      AND NOT EXISTS (
          SELECT 1 FROM notifications n
          WHERE n.related_id = c.id
            AND n.notification_type = 'CONTRACT'
            AND n.title = '租约即将到期提醒'
            AND n.create_time > DATE_SUB(CURDATE(), INTERVAL 30 DAY)
      );
    
    -- 同时通知房东
    INSERT INTO notifications (user_id, title, content, notification_type, related_id, is_read, create_time, update_time)
    SELECT 
        c.landlord_id AS user_id,
        '租约即将到期提醒' AS title,
        CONCAT('房间 ', r.room_number, ' 的租约将于 ', DATE_FORMAT(c.end_date, '%Y-%m-%d'), ' 到期。') AS content,
        'CONTRACT' AS notification_type,
        c.id AS related_id,
        FALSE AS is_read,
        NOW() AS create_time,
        NOW() AS update_time
    FROM contracts c
    JOIN rooms r ON c.room_id = r.id
    WHERE c.status = 'ACTIVE' 
      AND c.end_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY)
      AND NOT EXISTS (
          SELECT 1 FROM notifications n
          WHERE n.related_id = c.id
            AND n.notification_type = 'CONTRACT'
            AND n.title = '租约即将到期提醒'
            AND n.user_id = c.landlord_id
            AND n.create_time > DATE_SUB(CURDATE(), INTERVAL 30 DAY)
      );
END */$$
DELIMITER ;

/* Procedure structure for procedure `generate_monthly_bills` */

/*!50003 DROP PROCEDURE IF EXISTS  `generate_monthly_bills` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `generate_monthly_bills`(IN bill_month VARCHAR(7))
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_contract_id BIGINT;
    DECLARE v_room_id BIGINT;
    DECLARE v_tenant_id BIGINT;
    DECLARE v_rent_price DECIMAL(10,2);
    DECLARE v_payment_day INT;
    DECLARE v_bill_number VARCHAR(50);
    DECLARE v_due_date DATE;
    
    -- 定义游标查询活动合同
    DECLARE contract_cursor CURSOR FOR 
        SELECT id, room_id, tenant_id, rent_price, payment_day 
        FROM contracts 
        WHERE status = 'ACTIVE' 
          AND DATE_FORMAT(start_date, '%Y-%m') <= bill_month 
          AND (DATE_FORMAT(end_date, '%Y-%m') >= bill_month OR end_date IS NULL);
    
    -- 定义异常处理
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 开始事务
    START TRANSACTION;
    
    -- 打开游标
    OPEN contract_cursor;
    
    -- 循环处理每个合同
    read_loop: LOOP
        FETCH contract_cursor INTO v_contract_id, v_room_id, v_tenant_id, v_rent_price, v_payment_day;
        
        IF done THEN
            LEAVE read_loop;
        END IF;
        
        -- 生成账单编号
        SET v_bill_number = CONCAT('BILL', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD(v_contract_id, 6, '0'));
        
        -- 计算到期日
        SET v_due_date = STR_TO_DATE(CONCAT(bill_month, '-', v_payment_day), '%Y-%m-%d');
        
        -- 检查是否已经存在此月租金账单
        IF NOT EXISTS (
            SELECT 1 FROM bills 
            WHERE contract_id = v_contract_id 
              AND bill_type = 'RENT' 
              AND bill_month = bill_month
        ) THEN
            -- 插入租金账单
            INSERT INTO bills (
                contract_id, bill_number, bill_type, amount, 
                bill_month, due_date, status, create_time, update_time
            ) VALUES (
                v_contract_id, v_bill_number, 'RENT', v_rent_price,
                bill_month, v_due_date, 'UNPAID', NOW(), NOW()
            );
        END IF;
    END LOOP;
    
    -- 关闭游标
    CLOSE contract_cursor;
    
    -- 提交事务
    COMMIT;
END */$$
DELIMITER ;

/* Procedure structure for procedure `landlord_income_statistics` */

/*!50003 DROP PROCEDURE IF EXISTS  `landlord_income_statistics` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `landlord_income_statistics`(IN p_landlord_id BIGINT, IN p_year INT, IN p_month INT)
BEGIN
    DECLARE v_start_date DATE;
    DECLARE v_end_date DATE;
    
    -- 设置统计时间范围
    IF p_month IS NULL THEN
        -- 按年统计
        SET v_start_date = STR_TO_DATE(CONCAT(p_year, '-01-01'), '%Y-%m-%d');
        SET v_end_date = STR_TO_DATE(CONCAT(p_year, '-12-31'), '%Y-%m-%d');
    ELSE
        -- 按月统计
        SET v_start_date = STR_TO_DATE(CONCAT(p_year, '-', LPAD(p_month, 2, '0'), '-01'), '%Y-%m-%d');
        SET v_end_date = LAST_DAY(v_start_date);
    END IF;
    
    -- 返回统计结果
    SELECT 
        b.bill_type,
        SUM(b.amount) AS total_amount,
        COUNT(b.id) AS bill_count,
        COUNT(CASE WHEN b.status = 'PAID' THEN 1 ELSE NULL END) AS paid_count,
        SUM(CASE WHEN b.status = 'PAID' THEN b.amount ELSE 0 END) AS paid_amount,
        COUNT(CASE WHEN b.status = 'UNPAID' THEN 1 ELSE NULL END) AS unpaid_count,
        SUM(CASE WHEN b.status = 'UNPAID' THEN b.amount ELSE 0 END) AS unpaid_amount,
        COUNT(CASE WHEN b.status = 'OVERDUE' THEN 1 ELSE NULL END) AS overdue_count,
        SUM(CASE WHEN b.status = 'OVERDUE' THEN b.amount ELSE 0 END) AS overdue_amount
    FROM bills b
    JOIN contracts c ON b.contract_id = c.id
    WHERE c.landlord_id = p_landlord_id
      AND (b.paid_date BETWEEN v_start_date AND v_end_date OR 
           (b.paid_date IS NULL AND b.due_date BETWEEN v_start_date AND v_end_date))
    GROUP BY b.bill_type
    WITH ROLLUP;
END */$$
DELIMITER ;

/* Procedure structure for procedure `sync_room_contract_status` */

/*!50003 DROP PROCEDURE IF EXISTS  `sync_room_contract_status` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `sync_room_contract_status`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_room_id BIGINT;
    DECLARE v_active_contracts INT;
    DECLARE v_room_status VARCHAR(20);
    
    DECLARE room_cursor CURSOR FOR SELECT id, status FROM rooms;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 创建临时表记录修复结果
    DROP TEMPORARY TABLE IF EXISTS temp_sync_results;
    CREATE TEMPORARY TABLE temp_sync_results (
        room_id BIGINT,
        old_status VARCHAR(20),
        new_status VARCHAR(20),
        active_contracts INT,
        action_taken VARCHAR(100)
    );
    
    OPEN room_cursor;
    sync_loop: LOOP
        FETCH room_cursor INTO v_room_id, v_room_status;
        IF done THEN
            LEAVE sync_loop;
        END IF;
        
        -- 统计当前活跃合同数
        SELECT COUNT(*) INTO v_active_contracts
        FROM contracts 
        WHERE room_id = v_room_id 
          AND status = 'ACTIVE'
          AND start_date <= CURDATE()
          AND end_date >= CURDATE();
        
        -- 根据合同状态更新房间状态
        IF v_active_contracts > 0 AND v_room_status != 'RENTED' AND v_room_status != 'MAINTENANCE' THEN
            UPDATE rooms SET status = 'RENTED', update_time = NOW() WHERE id = v_room_id;
            INSERT INTO temp_sync_results VALUES (v_room_id, v_room_status, 'RENTED', v_active_contracts, '房间状态更新为已租');
        ELSEIF v_active_contracts = 0 AND v_room_status = 'RENTED' THEN
            UPDATE rooms SET status = 'VACANT', update_time = NOW() WHERE id = v_room_id;
            INSERT INTO temp_sync_results VALUES (v_room_id, v_room_status, 'VACANT', v_active_contracts, '房间状态更新为空闲');
        ELSEIF v_active_contracts > 1 THEN
            INSERT INTO temp_sync_results VALUES (v_room_id, v_room_status, v_room_status, v_active_contracts, '警告：房间有多个活跃合同');
        END IF;
        
    END LOOP;
    CLOSE room_cursor;
    
    -- 显示同步结果
    SELECT '房间状态同步结果' as title;
    SELECT * FROM temp_sync_results;
    
    -- 显示统计信息
    SELECT 
        COUNT(*) as total_processed,
        SUM(CASE WHEN action_taken LIKE '%更新%' THEN 1 ELSE 0 END) as updated_rooms,
        SUM(CASE WHEN action_taken LIKE '%警告%' THEN 1 ELSE 0 END) as warning_rooms
    FROM temp_sync_results;
    
    DROP TEMPORARY TABLE temp_sync_results;
END */$$
DELIMITER ;

/* Procedure structure for procedure `update_room_vacancy_status` */

/*!50003 DROP PROCEDURE IF EXISTS  `update_room_vacancy_status` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `update_room_vacancy_status`()
BEGIN
    -- 更新房间状态为"空闲"，当没有活动合同时
    UPDATE rooms r
    SET r.status = 'VACANT', r.update_time = NOW()
    WHERE r.status = 'RENTED'
      AND NOT EXISTS (
          SELECT 1 FROM contracts c
          WHERE c.room_id = r.id
            AND c.status = 'ACTIVE'
            AND c.end_date >= CURDATE()
      );
    
    -- 更新房间状态为"已租"，当有活动合同时
    UPDATE rooms r
    SET r.status = 'RENTED', r.update_time = NOW()
    WHERE r.status = 'VACANT'
      AND EXISTS (
          SELECT 1 FROM contracts c
          WHERE c.room_id = r.id
            AND c.status = 'ACTIVE'
            AND c.start_date <= CURDATE()
            AND c.end_date >= CURDATE()
      );
END */$$
DELIMITER ;

/* Procedure structure for procedure `validate_rental_integrity` */

/*!50003 DROP PROCEDURE IF EXISTS  `validate_rental_integrity` */;

DELIMITER $$

/*!50003 CREATE DEFINER=`root`@`localhost` PROCEDURE `validate_rental_integrity`()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE v_room_id BIGINT;
    DECLARE v_active_count INT;
    DECLARE v_room_number VARCHAR(20);
    DECLARE v_property_title VARCHAR(255);
    
    -- 定义游标查询所有房间
    DECLARE room_cursor CURSOR FOR 
        SELECT r.id, r.room_number, p.title
        FROM rooms r
        JOIN properties p ON r.property_id = p.id;
    
    -- 定义异常处理
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- 临时表存储问题房间
    DROP TEMPORARY TABLE IF EXISTS temp_problem_rooms;
    CREATE TEMPORARY TABLE temp_problem_rooms (
        room_id BIGINT,
        room_number VARCHAR(20),
        property_title VARCHAR(255),
        active_contracts INT,
        issue_description VARCHAR(255)
    );
    
    -- 打开游标
    OPEN room_cursor;
    
    -- 循环检查每个房间
    check_loop: LOOP
        FETCH room_cursor INTO v_room_id, v_room_number, v_property_title;
        
        IF done THEN
            LEAVE check_loop;
        END IF;
        
        -- 检查活跃合同数量
        SELECT COUNT(*) INTO v_active_count
        FROM contracts
        WHERE room_id = v_room_id AND status = 'ACTIVE';
        
        -- 如果有多个活跃合同，记录问题
        IF v_active_count > 1 THEN
            INSERT INTO temp_problem_rooms VALUES (
                v_room_id, v_room_number, v_property_title, v_active_count, 
                CONCAT('房间有 ', v_active_count, ' 个活跃合同，违反了一房一租规则')
            );
        END IF;
        
        -- 检查房间状态与合同状态是否一致
        IF v_active_count > 0 THEN
            -- 有活跃合同但房间状态不是已租
            IF EXISTS (
                SELECT 1 FROM rooms 
                WHERE id = v_room_id AND status != 'RENTED'
            ) THEN
                INSERT INTO temp_problem_rooms VALUES (
                    v_room_id, v_room_number, v_property_title, v_active_count,
                    '房间有活跃合同但状态不是已租'
                );
            END IF;
        ELSE
            -- 没有活跃合同但房间状态是已租
            IF EXISTS (
                SELECT 1 FROM rooms 
                WHERE id = v_room_id AND status = 'RENTED'
            ) THEN
                INSERT INTO temp_problem_rooms VALUES (
                    v_room_id, v_room_number, v_property_title, v_active_count,
                    '房间没有活跃合同但状态是已租'
                );
            END IF;
        END IF;
    END LOOP;
    
    -- 关闭游标
    CLOSE room_cursor;
    
    -- 返回问题房间列表
    SELECT * FROM temp_problem_rooms ORDER BY room_id;
    
    -- 返回统计信息
    SELECT 
        COUNT(DISTINCT room_id) as problem_rooms_count,
        COUNT(*) as total_issues,
        (SELECT COUNT(*) FROM rooms) as total_rooms,
        (SELECT COUNT(*) FROM contracts WHERE status = 'ACTIVE') as total_active_contracts
    FROM temp_problem_rooms;
    
    -- 清理临时表
    DROP TEMPORARY TABLE temp_problem_rooms;
END */$$
DELIMITER ;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
