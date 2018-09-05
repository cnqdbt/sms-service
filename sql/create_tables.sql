 CREATE TABLE `sms_template` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `business_code` varchar(45) NOT NULL,
  `template_id` varchar(45) NOT NULL,
  `create_date` datetime DEFAULT NULL,
  `update_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

INSERT INTO `sms_template` (business_code, template_id, create_date, update_date)
  VALUES ('verification_code', 'SMS_142950342', curtime(), curtime());

