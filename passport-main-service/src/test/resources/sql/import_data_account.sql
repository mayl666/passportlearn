DELETE FROM `account_16` WHERE passport_id = '13545210241@sohu.com'
DELETE FROM `account_28` WHERE passport_id = 'tinkame_test@sogou.com'
DELETE FROM `account_04` WHERE passport_id = 'tinkame@126.com'

DELETE FROM `account_00` WHERE passport_id = '13581695053@sohu.com'
DELETE FROM `account_27` WHERE passport_id = 'loveerin9460@163.com'
DELETE FROM `account_28` WHERE passport_id = 'liulingtest01@sogou.com'
DELETE FROM `uniqname_passportid_mapping` WHERE uniqname = '阿沐测试01'
DELETE FROM `mobile_passportid_mapping` WHERE mobile = '13581695053'


insert into `account_16` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('2','13545210241@sohu.com','RXF4tXHD$J17yQam0Bkx53ha1AbtDC.','13545210241','2014-03-18 10:42:37','127.0.0.1','1','2','2',NULL,NULL);
insert into `account_28` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('1','tinkame_test@sogou.com','ZBk2BdLA$6E94vfxe4rD3DEB7BnhWq1',NULL,'2014-03-19 19:39:48','10.129.192.193','1','2','1',NULL,NULL);
insert into `account_04` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('1','tinkame@126.com','bBNZcJTt$Z59PJcehNeYt1WVN0Pcs9.',NULL,'2014-03-19 20:26:03','10.129.192.193','1','2','1',NULL,NULL);

INSERT INTO `account_00` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) VALUES (1, '13581695053@sohu.com', 'ybiqf2QI$ygztMKXtqLj8QUlWIUv1x0', '13581695053', '2014-3-24 22:26:49', '10.129.192.121', '1', '2', 2, NULL, NULL);
INSERT INTO `account_27` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) VALUES (3, 'loveerin9460@163.com', 'gi1jReP3$OUzcSnyufvz74laHo0uQj/', NULL, '2014-3-24 22:35:52', '10.129.192.121', '1', '2', 1, NULL, NULL);
INSERT INTO `account_28` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) VALUES (2, 'liulingtest01@sogou.com', 'ol3WcQve$gdwqt9ybb9/IL/v5SA5e0.', NULL, '2014-3-25 11:22:04', '10.129.192.121', '1', '2', 1, '阿沐测试01', '%s/app/a/%s/Ti78RREtsRL63r64_1395717724516');
INSERT INTO `account_29` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) VALUES (1, 'osadnfdf@sogou.com', 'ouxHB7tW$36624ZI/OrpjQmixseGlV1', NULL, '2014-3-24 22:28:11', '10.129.192.121', '1', '2', 1, NULL, NULL);
INSERT INTO `uniqname_passportid_mapping` (`id`, `uniqname`, `passport_id`, `update_time`) VALUES (8, '阿沐测试01', 'liulingtest01@sogou.com', '2014-3-25 11:21:13');
INSERT INTO `mobile_passportid_mapping` (`id`, `mobile`, `passport_id`, `update_time`) VALUES (3, '13581695053', '13581695053@sohu.com', '2014-3-25 11:42:31');



