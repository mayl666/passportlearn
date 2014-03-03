package com.sogou.upd.passport.dao.datatransfertest.shplustransfer;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.sogou.upd.passport.BaseTest;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.FileIOUtil;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.SnamePassportMappingDAO;
import com.sogou.upd.passport.dao.datatransfertest.shplustransfer.DO.SohuPassportSidMapping;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.SnamePassportMapping;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-4
 * Time: 上午12:29
 * To change this template use File | Settings | File Templates.
 */
public class RepairDataTest extends BaseTest {

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private SnamePassportMappingDAO snamePassportMappingDAO;

    /**
     * 修复头像为空的情况
     */
    @Test
    public void repairNullAvatar() {
        try {
            List<String> passportIdList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\数据迁移的错误数据\\SG与SH+不一样的\\local-no-avator");
            BufferedWriter bw = FileIOUtil.newWriter("d:/no_obtain_avatar-avator.txt");
            for (String passportId : passportIdList) {
                AccountBaseInfo accountBaseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
                SohuPassportSidMapping sohuPassportSidMapping = SohuPlusUtil.sendSohuPlusHttp(passportId);
                String avatar = sohuPassportSidMapping.getLarge_avator();
                if (!Strings.isNullOrEmpty(avatar) && !avatar.matches("%s/app/[a-z]+/%s/[a-zA-Z0-9]+_\\d+")) {
                    avatar = photoUtils.uploadWebImg(avatar);
                }
                if (Strings.isNullOrEmpty(avatar)) {
                    bw.write(passportId + "\n");
                    bw.flush();
                } else {
                    if (accountBaseInfo == null) {
                        AccountBaseInfo accountBaseInfo1 = new AccountBaseInfo();
                        accountBaseInfo1.setPassportId(passportId);
                        accountBaseInfo1.setUniqname("");
                        accountBaseInfo1.setAvatar(avatar);
                        accountBaseInfoDAO.insertAccountBaseInfo(passportId, accountBaseInfo1);
                    } else {
                        accountBaseInfoDAO.updateAvatarByPassportId(avatar, passportId);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 修复昵称为空的情况
     */
    @Test
    public void repairNullUniqname() {
        try {
            BufferedWriter bw = FileIOUtil.newWriter("d:/no_update_uniqname-avator.txt");
            List<String> passportIdList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\数据迁移的错误数据\\SG与SH+不一样的\\local-update-nick");
            for (String passportId : passportIdList) {
                AccountBaseInfo accountBaseInfo = accountBaseInfoService.queryAccountBaseInfo(passportId);
                SohuPassportSidMapping sohuPassportSidMapping = SohuPlusUtil.sendSohuPlusHttp(passportId);
                String uniqname = sohuPassportSidMapping.getNick();
                boolean success = true;
                if (accountBaseInfo == null) {
                    accountBaseInfo = accountBaseInfoService.insertAccountBaseInfo(passportId, uniqname, "");
                } else {
                    success = accountBaseInfoService.updateUniqname(accountBaseInfo, uniqname);

                }
                if (!success) {
                    bw.write("passportid: " + passportId + "\n");
                    bw.flush();
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * 检查不相同的sid或sname在sohuplus修复的2000个里嘛
     */
    @Test
    public void checkDiffSid() {
        try {
            BufferedWriter bw = FileIOUtil.newWriter("d:/no_in_sohuplusfix.txt");
            List<String> sogou_diffsid_passportIdList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\数据迁移的错误数据\\SG与SH+不一样的\\diff_sidsname-passportid");
            List<String> sohuplus_fixList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\数据迁移的错误数据\\SG与SH+不一样的\\sogou4.2fix-修复的2000个记录.txt");
            List<String> sohuplus_fix_passportIdList = Lists.newArrayList();
//            for (String sohuplus : sohuplus_fixList) {
//                String[] strArray = sohuplus.split("||");
//                String sohuplus_passportId = strArray[2];
//                sohuplus_fix_passportIdList.add(sohuplus_passportId);
//            }
            for (String sogoupassportid : sogou_diffsid_passportIdList) {
                if (!sohuplus_fixList.contains(sogoupassportid)) {
                    bw.write("passportid: " + sogoupassportid + "\n");
                    bw.flush();
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * 检查不相同的sid或sname在sohuplus修复的2000个里嘛
     */
    @Test
    public void repairDiffSidOrSname() {
        try {
            BufferedWriter bw = FileIOUtil.newWriter("d:/no_update_diffSidSname.txt");
            List<String> sogou_diffsid_passportIdList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\数据迁移的错误数据\\SG与SH+不一样的\\diff_sidSname-passportid");
            for (String sogou_diffsid_passportId : sogou_diffsid_passportIdList) {
                SohuPassportSidMapping sohuPassportSidMapping = SohuPlusUtil.sendSohuPlusHttp(sogou_diffsid_passportId);
                String sid = sohuPassportSidMapping.getSid();
                String sname = sohuPassportSidMapping.getSname();
                SnamePassportMapping mapping = snamePassportMappingDAO.getSnamePassportMappingByPassportid(sogou_diffsid_passportId);
                if (mapping != null) {
                    String oldSid = mapping.getSid();
                    String oldSname = mapping.getSname();
                    if (!oldSid.equals(sid) || !oldSname.equals(sname)) {
                        bw.write("error passportid: " + sogou_diffsid_passportId + "\n");
                        bw.flush();
                    }
                }
//                int row = 0;
//                try {
//                    row = snamePassportMappingDAO.updateSidSnamePassportMapping(sogou_diffsid_passportId, sname, sid);
//                } catch (Exception e) {
//                }
//                if (row <= 0) {
//                    bw.write("passportid: " + sogou_diffsid_passportId + "\n");
//                    bw.flush();
//                }
            }
        } catch (IOException e) {
        }
    }



}
