package com.sogou.upd.passport.dao.shplustransfer;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.sogou.upd.passport.common.lang.BuilderUtil;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.SohuplusTmpDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.SohuplusTmp;
import com.sogou.upd.passport.model.account.UniqnamePassportMapping;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.io.*;
import java.util.List;
import java.util.Map;

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
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private TaskExecutor batchOperateExecutor;

    /**
     * 将sohu+昵称头像导入sogou mysql
     */
    @Test
    public void testSohuPlusToSGDB() {
        List<SohuplusTmp> sohuplusTmpList = sohuplusTmpDAO.listSohuplusTmp();
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
                accountBaseInfoService.insertAccountBaseInfo(passportId, uniqname, avatar);
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("uploadWebImg fail, passportId:" + passportId);
            }
            i++;
        }
        System.out.println("actual number:" + i);
    }

    @Test
    public void testSohuPlusIncrDataToSGDB() {
        try {
            FileOutputStream fos = new FileOutputStream("d:/error_sohuplus_incrdata.txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            List<String> lineList = FileIOUtil.readFileByLines("C:\\Users\\shipengzhi\\Desktop\\新Passport\\sohu+\\sogou12-06-全量数据\\sogou12-06_008");
            for (String line : lineList) {
                String[] array = line.split(",");
                if (array.length < 4) {
                    bw.write("data length not 3, sid:" + array[0]);
                    bw.flush();
                    bw.write("\n");
                    bw.flush();
                    continue;
                }
                long sid = Long.parseLong(array[0].replace("\"", ""));
                String sname = array[1].replace("\"", "");
                String passportId = array[2].replace("\"", "");
//                String uniqname = array[3].replace("\"", "");
//                String avatar = array[4].replace("\"", "");
                SohuplusTmp sohuplusTmp = new SohuplusTmp();
                sohuplusTmp.setSid(sid);
                sohuplusTmp.setSname(sname);
                sohuplusTmp.setPassportId(passportId);
                sohuplusTmp.setUniqname("");
                sohuplusTmp.setAvatar("");
                int row = sohuplusTmpDAO.insetOrUpdateSohuplusTmpBySId(sid, sohuplusTmp);
                if (row <= 0) {
                    bw.write("insert or update fail, sid:" + array[0]);
                    bw.flush();
                    bw.write("\n");
                    bw.flush();
                    continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 验证sogou AccountBaseInfo表和UniqnamePassportMapping正确性
     */
    @Test
    public void cacheVaildUniqnameMapping() throws IOException {
        List<AccountBaseInfo> accountBaseInfoList = accountBaseInfoDAO.listAccountBaseInfo();
        List<UniqnamePassportMapping> uniqnameMappingList = uniqNamePassportMappingDAO.lisPassportIdByUniqName();
        Map<String, String> uniqnameMap = Maps.newHashMap();
        for (UniqnamePassportMapping mapping : uniqnameMappingList) {
            uniqnameMap.put(mapping.getPassportId(), mapping.getUniqname());
        }
        FileOutputStream fos = new FileOutputStream("d:/error_mapping.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        for (AccountBaseInfo accountBaseInfo : accountBaseInfoList) {
            String uniqname = accountBaseInfo.getUniqname();
            String passportId = accountBaseInfo.getPassportId();
            if (!Strings.isNullOrEmpty(uniqname)) {
                String otherUniqname = uniqnameMap.get(passportId);
                if (Strings.isNullOrEmpty(otherUniqname) || !otherUniqname.equals(uniqname)) {
                    bw.write("error passportid:" + passportId);
                    bw.flush();
                    bw.write("\n");
                    bw.flush();
                }
            }
        }
    }

    /**
     * 验证sohu+原始文件与AccountBaseInfo昵称
     *
     * @throws IOException
     */
    @Test
    public void cacheVaildOriginUniqname() throws IOException {
        List<AccountBaseInfo> accountBaseInfoList = accountBaseInfoDAO.listAccountBaseInfo();
        List<SohuplusTmp> sohuplusTmpList = sohuplusTmpDAO.listSohuplusTmp();
        Map<String, String> baseInfoMap = Maps.newHashMap();
        for (AccountBaseInfo accountBaseInfo : accountBaseInfoList) {
            baseInfoMap.put(accountBaseInfo.getPassportId(), accountBaseInfo.getUniqname());
        }
        FileOutputStream fos = new FileOutputStream("d:/error_uniqname.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        for (SohuplusTmp sohuplusTmp : sohuplusTmpList) {
            String passportId = sohuplusTmp.getPassportId();
            String uniqname = sohuplusTmp.getUniqname();
            String otherUniqname = baseInfoMap.get(passportId);
            if (!Strings.isNullOrEmpty(uniqname)) {
                if (Strings.isNullOrEmpty(otherUniqname) || !otherUniqname.equals(uniqname)) {
                    String mappingPassportid = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                    if (Strings.isNullOrEmpty(mappingPassportid)) {
                        bw.write(passportId);
                        bw.flush();
                        bw.write("\n");
                        bw.flush();
                    }
                }
            }
        }
    }

    /**
     * 修复为插入的昵称头像
     */
    @Test
    public void repairAccountBaseInfo() {
        File file = new File("d:/error_uniqname.txt");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            //一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                String passportId = tempString;
                SohuplusTmp sohuplusTmp = sohuplusTmpDAO.getSohuplusTmpByPassportId(passportId);
                String uniqname = sohuplusTmp.getUniqname();
                // 去除无效昵称
                if (uniqname.contains("搜狐网友") || uniqname.contains("在搜狐") || uniqname.contains("的blog")) {
                    uniqname = "";
                }
                // 转换头像url
                String avatar = sohuplusTmp.getAvatar();
                if (!Strings.isNullOrEmpty(avatar) && !avatar.matches("%s/app/[a-z]+/%s/[a-zA-Z0-9]+_\\d+")) {
                    avatar = photoUtils.uploadWebImg(avatar);
                }
                accountBaseInfoService.insertAccountBaseInfo(passportId, uniqname, avatar);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
    }

    /**
     * 验证sogou AccountBaseInfo表和UniqnamePassportMapping正确性
     */
    @Test
    public void vaildUniqnameMapping() throws IOException {
        final List<AccountBaseInfo> accountBaseInfoList = accountBaseInfoDAO.listAccountBaseInfo();
        final List<List<AccountBaseInfo>> groupList = BuilderUtil.groupingItemList(accountBaseInfoList, 10);
        FileOutputStream fos = new FileOutputStream("d:/error_mapping.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        final BufferedWriter bw = new BufferedWriter(osw);
        bw.write("group siz:" + groupList.size());
        bw.flush();
        for (final List<AccountBaseInfo> accountBaseInfoGroup : groupList) {
            batchOperateExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < accountBaseInfoGroup.size(); i++) {
                        AccountBaseInfo accountBaseInfo = accountBaseInfoGroup.get(i);
                        String uniqname = accountBaseInfo.getUniqname();
                        String passportId = accountBaseInfo.getPassportId();
                        if (!Strings.isNullOrEmpty(uniqname)) {
                            String otherPassportId = uniqNamePassportMappingDAO.getPassportIdByUniqName(uniqname);
                            if (!passportId.equals(otherPassportId)) {
                                try {
                                    bw.write("error passportid:" + passportId);
                                    bw.flush();
                                    bw.write("\n");
                                    bw.flush();
                                } catch (IOException e) {
                                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                }
                            }
                        }
                        if (i == accountBaseInfoGroup.size()) {
                            try {
                                bw.write("one group over!");
                                bw.flush();
                                bw.write("\n");
                                bw.flush();
                            } catch (IOException e) {
                                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                            }
                        }
                    }
                }
            });
        }
    }

    @Test
    public void outPutFile() {
        try {
            FileOutputStream fos = new FileOutputStream("d:/error_mapping.txt");
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write("passportid: shipengzhi");
            bw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
