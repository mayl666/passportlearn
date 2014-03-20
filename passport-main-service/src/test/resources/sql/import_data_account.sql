DELETE FROM `account_16` WHERE passport_id = '13545210241@sohu.com'
DELETE FROM `account_28` WHERE passport_id = 'tinkame_test@sogou.com'
DELETE FROM `account_04` WHERE passport_id = 'tinkame@126.com'

insert into `account_16` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('2','13545210241@sohu.com','RXF4tXHD$J17yQam0Bkx53ha1AbtDC.','13545210241','2014-03-18 10:42:37','127.0.0.1','1','2','2',NULL,NULL);
insert into `account_28` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('1','tinkame_test@sogou.com','ZBk2BdLA$6E94vfxe4rD3DEB7BnhWq1',NULL,'2014-03-19 19:39:48','10.129.192.193','1','2','1',NULL,NULL);
insert into `account_04` (`id`, `passport_id`, `password`, `mobile`, `reg_time`, `reg_ip`, `flag`, `passwordtype`, `account_type`, `uniqname`, `avatar`) values('1','tinkame@126.com','bBNZcJTt$Z59PJcehNeYt1WVN0Pcs9.',NULL,'2014-03-19 20:26:03','10.129.192.193','1','2','1',NULL,NULL);
