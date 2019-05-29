
CREATE SCHEMA IF NOT EXISTS `manage2` DEFAULT CHARACTER SET utf8 ;
USE `manage2` ;

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '类别ID',
  `category_name` varchar(64) NOT NULL COMMENT '类别名称',
  `parent_id` int(11) NOT NULL DEFAULT '-1' COMMENT '上级ID',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '产品类别表';

--
-- Table structure for table `output`
--

DROP TABLE IF EXISTS `output`;
CREATE TABLE `output` (
  `output_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '产值ID',
  `month` int(11) NOT NULL COMMENT '月份',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `output_name` varchar(64) NOT NULL COMMENT '产值产品名称',
  `suk_id` int(11) NOT NULL DEFAULT '0' COMMENT 'skuId',
  `suk_price` float NOT NULL DEFAULT '0' COMMENT '产品价格',
  `xia_dan` int(11) NOT NULL DEFAULT '0' COMMENT '下单',
  `mu_gong` int(11) NOT NULL DEFAULT '0' COMMENT '木工',
  `you_fang` int(11) NOT NULL DEFAULT '0' COMMENT '油房',
  `bao_zhuang` int(11) NOT NULL DEFAULT '0' COMMENT '包装',
  `te_ding` int(11) NOT NULL DEFAULT '0' COMMENT '特定',
  `beijing_input` int(11) NOT NULL DEFAULT '0' COMMENT '北京入库',
  `beijing_teding_input` int(11) NOT NULL DEFAULT '0' COMMENT '北京特定',
  `factory_output` int(11) NOT NULL DEFAULT '0' COMMENT '工厂出货',
  `teding_factory_output` int(11) NOT NULL DEFAULT '0' COMMENT '特定工厂出货',
  `beijing_stock` int(11) NOT NULL DEFAULT '0' COMMENT '北京库存',
  `beijing_teding_stock` int(11) NOT NULL DEFAULT '0' COMMENT '北京特定库存',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`output_id`),
  KEY `index_productid_sukid`(`suk_id`,`product_id`),
  KEY `index_month` (`month`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '产值表';

--
-- Table structure for table `output_record`
--

DROP TABLE IF EXISTS `output_record`;
CREATE TABLE `output_record` (
  `record_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `suk_id` int(11) NOT NULL DEFAULT '0' COMMENT 'sukId',
  `col_name` varchar(64) NOT NULL COMMENT '变化项',
  `value` int(11) NOT NULL DEFAULT '0' COMMENT '变化值',
  `comments` varchar(256) NOT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `index_productid_sukid` (`suk_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '产值记录表';

--
-- Table structure for table `produce`
--

DROP TABLE IF EXISTS `produce`;
CREATE TABLE `produce` (
  `produce_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '进度ID',
  `date` int(11) NOT NULL COMMENT '日期',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `produce_name` varchar(64) NOT NULL COMMENT '进度产品名称',
  `suk_id` int(11) NOT NULL DEFAULT '0' COMMENT 'sukId',
  `suk_price` float NOT NULL DEFAULT '0' COMMENT 'suk价格',
  `xia_dan` int(11) NOT NULL DEFAULT '0' COMMENT '下单',
  `mu_gong` int(11) NOT NULL DEFAULT '0' COMMENT '木工',
  `you_fang` int(11) NOT NULL DEFAULT '0' COMMENT '油房',
  `bao_zhuang` int(11) NOT NULL DEFAULT '0' COMMENT '包装',
  `te_ding` int(11) NOT NULL DEFAULT '0' COMMENT '特定',
  `beijing` int(11) NOT NULL DEFAULT '0' COMMENT '北京',
  `beijing_teding` int(11) NOT NULL DEFAULT '0' COMMENT '北京特定',
  `bendi_hetong` int(11) NOT NULL DEFAULT '0' COMMENT '本地合同',
  `waidi_hetong` int(11) NOT NULL DEFAULT '0' COMMENT '外地合同',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`produce_id`),
  KEY `index_productid_sukid` (`suk_id`,`product_id`),
  KEY `index_date` (`date`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '生产进度表';

--
-- Table structure for table `produce_record`
--

DROP TABLE IF EXISTS `produce_record`;
CREATE TABLE `produce_record` (
  `record_id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `user_id` int(11) NOT NULL DEFAULT '0' COMMENT '用户ID',
  `product_id` int(11) NOT NULL COMMENT '产品ID',
  `suk_id` int(11) NOT NULL DEFAULT '0' COMMENT 'sukId',
  `col_name1` varchar(64) NOT NULL COMMENT '变化项1',
  `value1` int(11) NOT NULL DEFAULT '0' COMMENT '变化值1',
  `col_name2` varchar(64) NOT NULL COMMENT '变化项2',
  `value2` int(11) NOT NULL DEFAULT '0' COMMENT '变化值2',
  `col_name3` varchar(64) NOT NULL COMMENT '变化项3',
  `value3` int(11) NOT NULL DEFAULT '0' COMMENT '变化值3',
  `comments` varchar(256) NOT NULL COMMENT '备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `index_productid_sukid` (`suk_id`,`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '生产进度记录表';

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '产品Id',
  `product_name` varchar(64) NOT NULL COMMENT '产品名称',
  `category_id` int(11) DEFAULT NULL COMMENT '产品类别',
  `head_img` varchar(100) DEFAULT NULL COMMENT '头图',
  `img` varchar(1000) DEFAULT NULL COMMENT '产品图片',
  `comments` varchar(256) DEFAULT NULL COMMENT '产品备注',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '产品信息表';

--
-- Table structure for table `product_suk`
--

DROP TABLE IF EXISTS `product_suk`;
CREATE TABLE `product_suk` (
  `suk_id` int(11) NOT NULL AUTO_INCREMENT COMMENT 'sukId',
  `product_id` int(11) NOT NULL COMMENT '产品Id',
  `suk_name` varchar(64) NOT NULL COMMENT 'suk名称',
  `price` float NOT NULL COMMENT 'suk价格',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`suk_id`),
  KEY `index_productid` (`product_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '产品suk表';

--
-- Table structure for table `user_info`
--

DROP TABLE IF EXISTS `user_info`;
CREATE TABLE `user_info` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(64) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码',
  `name` varchar(64) NOT NULL COMMENT '姓名',
  `roles` varchar(64) NOT NULL DEFAULT 'USER' COMMENT '角色',
  `status` int(1) NOT NULL DEFAULT '0' COMMENT '账号状态：0-禁用，1-可用',
  `wx_open_id` varchar(64) NULL COMMENT '微信openId',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8 COMMENT '用户信息表';

-- ----------------------------
-- Records of user_info
-- ----------------------------
BEGIN;
INSERT INTO `user_info` (username, password, name, roles, status) VALUES ('admin', '$2a$10$sw34isaLbnsQjJAkGmB03uDNrWFad/Hy5N2Z4kTnIpx9MdVnq9yEi', 'test', 'SYS_ADMIN', 1);
COMMIT;