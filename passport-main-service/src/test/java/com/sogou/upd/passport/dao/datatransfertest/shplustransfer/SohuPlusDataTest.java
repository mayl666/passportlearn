package com.sogou.upd.passport.dao.datatransfertest.shplustransfer;


import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.SnamePassportMappingDAO;
import com.sogou.upd.passport.dao.datatransfertest.shplustransfer.DO.SohuPassportSidMapping;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.SnamePassportMapping;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hujunfei
 * Date: 13-12-2
 * Time: 下午12:37
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class SohuPlusDataTest extends BaseDAOTest {
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private SnamePassportMappingDAO snamePassportMappingDAO;

    private PrintWriter printWriter;

    private ThreadLocal<ObjectMapper> objectMapper = new ThreadLocal<>();
    private ThreadLocal<Integer> success = new ThreadLocal<>();

    private final int STEP = 1000;
    private int THREADS = 10;
    String secret = "59be99d1f5e957ba5a20e8d9b4d76df6";
    String appkey = "30000004";
    String ip = "127.0.0.1";

    @Test
    public void testSnamePassportidMapping() {
        try {
            try {
                ip = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                ip = "127.0.0.1";
            }

            // dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("/search/result.log")));
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream("/search/result.log")), true);
            objectMapper.set(new ObjectMapper());
            success.set(new Integer(0));

            List<SnamePassportMapping> list = snamePassportMappingDAO.listPassportIdMapping();


            int allLocal = list.size();
            //int allLocal = 100;
            int begin = 0;
            int area = allLocal / THREADS;

            List<Thread> threadList = new LinkedList<>();

            for (int i = begin, j = 0; i < allLocal; i += area, j++) {
                int end = i + area;
                if (end > allLocal) {
                    end = allLocal;
                }
                Thread thread = new Thread(new CompareSpassportWithSohu(j, i, end, list));
                thread.start();
                threadList.add(thread);
            }

            for (int i = 0; i < threadList.size(); i++) {
                try {
                    threadList.get(i).join();
                } catch (InterruptedException e) {
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("data file error");
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    /**
     * 获取sohu的数据，并比较结果
     */
    class CompareSpassportWithSohu implements Runnable {
        private int flag;
        private int begin;
        private int end;
        private List<SnamePassportMapping> list;

        void setFlag(int flag) {
            this.flag = flag;
        }

        void setList(List list) {
            this.list = list;
        }

        void setEnd(int end) {
            this.end = end;
        }

        void setBegin(int begin) {

            this.begin = begin;
        }

        public CompareSpassportWithSohu(int flag, int begin, int end, List list) {
            setFlag(flag);
            setBegin(begin);
            setEnd(end);
            setList(list);
        }


        @Override
        public void run() {
            try {
                Thread.sleep(flag * 1000);  // 睡眠一定时间防止一起调用某个接口
            } catch (InterruptedException e) {
                //
            }
            int successNum = 0;
            String sids = null;

            for (int j = begin; j < end && j < list.size(); j += STEP) {
                for (int i = j; i < j + STEP && i < end && i < list.size(); i++) {
                    // dataOutputStream.writeInt(i);
                    //dataOutputStream.writeByte('\t');
                    // dataOutputStream.flush();

                    SnamePassportMapping snamePassportMapping = list.get(i);
                    String passportId = snamePassportMapping.getPassportId();
                    SohuPassportSidMapping sohuPassportSidMapping = SohuPlusUtil.sendSohuPlusHttp(passportId);

                    AccountBaseInfo accountBaseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);
                    printWriter.println(i + "\t" + snamePassportMapping.getPassportId() + "\t" + compareResult(snamePassportMapping, sohuPassportSidMapping) + "\t" + compareNickAndAvator(accountBaseInfo, sohuPassportSidMapping));

                }
                printWriter.flush();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    //
                }
            }
            success.set(new Integer(successNum));
            System.out.println(success.get() + ":" + successNum);
        }

        private String compareResult(SnamePassportMapping loc, SohuPassportSidMapping rem) {
            if (rem == null) {
                return "sohu-no";
            }
            boolean result = loc.getSid().equals(rem.getSid()) && loc.getSname().equals(rem.getSname());
            if (result == true) {
                if (StringUtil.isBlank(loc.getMobile()) && !StringUtil.isBlank(rem.getMobile())) {
                    return "local-no-mobile:" + loc.getMobile() + "|" + rem.getMobile();
                }
                if (!StringUtil.isBlank(loc.getMobile()) && !loc.getMobile().equals(rem.getMobile())) {
                    return "diff-mobile:" + loc.getMobile() + "|" + rem.getMobile();
                }
            }
            if (result == false) {
                return "diff-sidOrname:" + loc.getSid() + "|" + rem.getSid() + "|" + loc.getSname() + "|" + rem.getSname();
            } else {
                return "ok";
            }
        }


        private String compareNickAndAvator(AccountBaseInfo accountBaseInfo, SohuPassportSidMapping sohuPassportSidMapping) {
            String flag = ":" + (accountBaseInfo == null ? null : accountBaseInfo.getUniqname()) + "|"
                    + (sohuPassportSidMapping == null ? null : sohuPassportSidMapping.getNick());
            if (accountBaseInfo == null) {
                if (sohuPassportSidMapping != null && (sohuPassportSidMapping.getLarge_avator() != null || sohuPassportSidMapping.getNick() != null)) {
                    if (!StringUtil.isBlank(sohuPassportSidMapping.getLarge_avator())) {
                        return "local-no-avator" + flag + "|null|" + sohuPassportSidMapping.getLarge_avator();
                    }
                    String nick = sohuPassportSidMapping.getNick();
                    if (!StringUtil.isBlank(nick) && nick.indexOf("搜狐网友") == -1
                            && nick.indexOf("在搜狐") == -1 && nick.indexOf("的blog") == -1) {
                        return "local-no-nick" + flag;
                    }
                }
                return "ok";
            }
            if (sohuPassportSidMapping == null) {
                return "sohu-no" + flag;
            }
            String nickSohu = sohuPassportSidMapping.getNick();
            String avatarSohu = sohuPassportSidMapping.getLarge_avator();
            String avatar = accountBaseInfo.getAvatar();
            String nick = accountBaseInfo.getUniqname();

            if (nick == null) {
                if (nickSohu != null) {
                    return "local-no-nick" + flag;
                }

            } else {
                /*if (!nick.equals(nickSohu)) {
                    return "diff-nick";
                }*/
                if (StringUtil.isEmpty(nickSohu)) {
                    return "sohu-no-nick" + flag;
                }
            }
            if (StringUtil.isBlank(avatar)) {
                if (!StringUtil.isEmpty(avatarSohu)) {
                    return "local-no-avatar" + flag + "|null|" + sohuPassportSidMapping.getLarge_avator();
                }
            } else {
                /*if (!avatar.equals(avatarSohu)) {
                    return "diff-avatar";
                }*/
                if (StringUtil.isEmpty(avatarSohu)) {
                    return "sohu-no-avatar" + flag + "|" + avatar + "|" + sohuPassportSidMapping.getLarge_avator();
                }
            }

            return "ok";
        }
    }

    class SohuPassportMapping {
        private String sid;
        private String sname;
        private String email;
        private String status;

        String getSid() {
            return sid;
        }

        void setSid(String sid) {
            this.sid = sid;
        }

        String getSname() {
            return sname;
        }

        void setSname(String sname) {
            this.sname = sname;
        }

        String getEmail() {
            return email;
        }

        void setEmail(String email) {
            this.email = email;
        }

        String getStatus() {
            return status;
        }

        void setStatus(String status) {
            this.status = status;
        }
    }
}
