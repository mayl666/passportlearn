package com.sogou.upd.passport.dao.shplustransfer;

import com.google.common.base.Strings;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.account.SohuplusTmpDAO;
import com.sogou.upd.passport.model.account.SohuplusTmp;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import com.sogou.upd.passport.service.account.UniqNamePassportMappingService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-11-28
 * Time: 下午5:27
 * To change this template use File | Settings | File Templates.
 */
public class SohuPlusTransferTest extends BaseDAOTest {

    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private SohuplusTmpDAO sohuplusTmpDAO;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private UniqNamePassportMappingService uniqNamePassportMappingService;

    @Test
    public void testSohuPlusToSGDB() {

        List<SohuplusTmp> sohuplusTmpList = sohuplusTmpDAO.listSohuplusTmpDO();
        System.out.println("orgin number:" + sohuplusTmpList.size());
        int i = 0;
        for (SohuplusTmp sohuplusTmp : sohuplusTmpList) {
            String passportId = sohuplusTmp.getPassportId();
            String uniqname = sohuplusTmp.getUniqname();
            String avatar = sohuplusTmp.getAvatar();
            if (!Strings.isNullOrEmpty(avatar)) {
                try {
                    avatar = photoUtils.uploadWebImg(avatar);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("uploadWebImg fail, passportId:" + passportId);
                }
            }
            try {
                accountBaseInfoService.initAccountUniqNameAndAvatar(passportId, uniqname, avatar);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("uploadWebImg fail, passportId:" + passportId);
            }
            i++;
        }
        System.out.println("actual number:" + i);
    }
}
