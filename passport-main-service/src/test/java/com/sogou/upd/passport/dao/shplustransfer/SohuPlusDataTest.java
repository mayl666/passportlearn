package com.sogou.upd.passport.dao.shplustransfer;


import com.sogou.upd.passport.common.lang.StringUtil;
import com.sogou.upd.passport.common.math.Coder;
import com.sogou.upd.passport.common.model.httpclient.RequestModel;
import com.sogou.upd.passport.common.utils.PhotoUtils;
import com.sogou.upd.passport.common.utils.SGHttpClient;
import com.sogou.upd.passport.dao.BaseDAOTest;
import com.sogou.upd.passport.dao.account.AccountBaseInfoDAO;
import com.sogou.upd.passport.dao.account.SnamePassportMappingDAO;
import com.sogou.upd.passport.dao.account.SohuplusTmpDAO;
import com.sogou.upd.passport.dao.account.UniqNamePassportMappingDAO;
import com.sogou.upd.passport.model.account.AccountBaseInfo;
import com.sogou.upd.passport.model.account.SnamePassportMapping;
import com.sogou.upd.passport.service.account.AccountBaseInfoService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;

import java.io.*;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: hujunfei
 * Date: 13-12-2
 * Time: 下午12:37
 * To change this template use File | Settings | File Templates.
 */
public class SohuPlusDataTest extends BaseDAOTest {
    @Autowired
    private PhotoUtils photoUtils;
    @Autowired
    private SohuplusTmpDAO sohuplusTmpDAO;
    @Autowired
    private AccountBaseInfoDAO accountBaseInfoDAO;
    @Autowired
    private UniqNamePassportMappingDAO uniqNamePassportMappingDAO;
    @Autowired
    private SnamePassportMappingDAO snamePassportMappingDAO;
    @Autowired
    private AccountBaseInfoService accountBaseInfoService;
    @Autowired
    private TaskExecutor batchOperateExecutor;

    // private DataOutputStream dataOutputStream;
    private PrintWriter printWriter;

    private ThreadLocal<ObjectMapper> objectMapper = new ThreadLocal<>();
    private ThreadLocal<Integer> success = new ThreadLocal<>();

    private final int STEP = 500;
    private int THREADS = 10;
    String secret = "59be99d1f5e957ba5a20e8d9b4d76df6";
    String appkey = "30000004";
    String ip = "10.129.192.245";

    @Test
    public void testSnamePassportidMapping() {
        try {
            // dataOutputStream = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("/search/result.log")));
            printWriter = new PrintWriter(new BufferedOutputStream(new FileOutputStream("/search/result.log")), true);
            objectMapper.set(new ObjectMapper());
            success.set(new Integer(0));

            List<SnamePassportMapping> list = snamePassportMappingDAO.listPassportIdMapping();


            int allLocal = list.size();
            //int allLocal = 100;
            int begin = 0;
            int area = allLocal / THREADS;

            printWriter.println(allLocal);
            printWriter.flush();
            List<Thread> threadList = new LinkedList<>();

            for (int i = begin, j = 0; i < allLocal; i += area, j++) {
                int end = i + area;
                if (end > allLocal) {
                    end = allLocal;
                }
                // System.out.println(i+":"+end);
                Thread thread = new Thread(new CompareSpassportWithSohu(j, i, end, list));
                thread.start();
                threadList.add(thread);
            }


            for (int i = 0; i < threadList.size(); i++) {
                try {
                    threadList.get(i).join();
                } catch (InterruptedException e) {
                    //
                }
            }


            // List<AccountBaseInfo> listAccount = accountBaseInfoDAO.listAccountBaseInfo();

        } catch (FileNotFoundException e) {
            System.err.println("data file error");
        } finally {

                /*if (dataOutputStream != null) {
                    dataOutputStream.close();
                }*/
            if (printWriter != null) {
                printWriter.close();
            }

        }


    }

    /**
     * 发送请求至SOHU+，获取结果
     *
     * @param sids
     * @return
     */
    private List sendSpassportHttpReq(String sids) {
        String url = "http://rest.plus.sohuno.com/spassportrest/batchget/spassports";
        RequestModel requestModel = new RequestModel(url);
        requestModel.addParam("sids", sids);
        requestModel.addParam("appkey", appkey);
        requestModel.addParam("so_sig", computeSig(sids));
        int errors = 0;

        String result = SGHttpClient.executeStr(requestModel);

        Map<String, String> map = null;
        try {
            map = new ObjectMapper().readValue(result, Map.class);
        } catch (IOException e) {
            errors++;
        }

        List list = null;
        //List allList = new LinkedList();
        try {
            list = new ObjectMapper().readValue(map.get("data"), List.class);
        } catch (IOException e) {
            errors++;
        }

        //allList.addAll(list);

        if (errors != 0) {
            System.out.println(errors);
        }

        return list;
        //return allList;
    }

    /**
     * 发送请求至SOHU+，获取结果
     *
     * @param
     * @return
     */

    private String sendSpassportSingleHttpReq(String url, Map<String, String> map) {
        RequestModel requestModel = new RequestModel(url);

        for (Map.Entry<String, String> entry : map.entrySet()) {
            requestModel.addParam(entry.getKey(), entry.getValue());
        }
        requestModel.addParams(map);
        requestModel.addParam("so_sig", computeSigCommon(map));

        System.out.println(requestModel.getUrlWithParam());

        String result = SGHttpClient.executeStr(requestModel);
        System.out.println(result);

        Map<String, Map<String, Object>> mapResult = null;
        String data = null;
        try {
            ObjectMapper om = new ObjectMapper();
            mapResult = om.readValue(result, Map.class);// , Map.class);
            data = om.writeValueAsString(mapResult.get("data"));
        } catch (IOException e) {
            System.err.println("error");
        }

        //List allList = new LinkedList();
        // data = (String) map.get("data");

        return data;
    }

    private List sendSpassportSidHttpReq(String passportId) {
        String url = "http://rest.plus.sohuno.com/spassportrest/passport/autoconvert";
        RequestModel requestModel = new RequestModel(url);

        requestModel.addParam("passport", passportId);
        requestModel.addParam("appkey", appkey);
        requestModel.addParam("ip", "10.129.192.245");
        requestModel.addParam("so_sig", computeSigSingle(passportId));
        int errors = 0;

        String result = SGHttpClient.executeStr(requestModel);

        Map<String, Map<String, Object>> map = null;
        try {
            map = new ObjectMapper().readValue(result, Map.class);
        } catch (IOException e) {
            errors++;
        }

        List list = null;
        //List allList = new LinkedList();
       /* try {
            list = new ObjectMapper().readValue(map.get("data"), List.class);
        } catch (IOException e) {
            errors++;
        }*/

        //allList.addAll(list);

        if (errors != 0) {
            System.out.println(errors);
        }

        return list;
        //return allList;
    }


/*    @Test
    public void testSig() {
        String sids = "89057280,102587270";
        String sig = computeSig(sids);
        System.out.println(sig);

        Map<String, String> map = new HashMap();
        map.put("mobile", "18210696900");
        map.put("appkey", appkey);

        System.out.println(computeSigCommon(map));
    }*/

    /**
     * 计算签名
     *
     * @param map
     * @return
     */

    private String computeSigCommon(Map<String, String> map) {
        Set keys = map.keySet();
        TreeMap<String, String> treeMap = new TreeMap(map);
        StringBuilder sb = new StringBuilder();

        Iterator it = treeMap.keySet().iterator();

        while (it.hasNext()) {
            String key = (String) it.next();
            sb.append(key + "=" + treeMap.get(key));
        }
        sb.append(secret);
        try {
            return Coder.encryptMD5(sb.toString());
        } catch (Exception e) {
            return null;
        }
    }

    private String computeSig(String sids) {

        try {
            String so_sig = Coder.encryptMD5("appkey=30000004" + "sids=" + sids + secret);
            // String so_sig = Coder.encryptMD5("appkey=30000004" + "ip=10.129.192.245" +"passport=hujunfei1986@163.com"+secret);
            return so_sig;
        } catch (Exception e) {
            System.err.println("sig error");
        }
        return null;
    }

    private String computeSigSingle(String passportId) {
        try {
            String so_sig = Coder.encryptMD5("appkey=30000004" + "ip=10.129.192.245" + "passport=" + passportId + secret);
            return so_sig;
        } catch (Exception e) {
            System.err.println("sig error");
        }
        return null;
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

                    String url = "http://rest.plus.sohuno.com/spassportrest/passport/autoconvert";
                    Map<String, String> map = new HashMap();
                    map.put("appkey", appkey);
                    map.put("passport", passportId);
                    map.put("ip", ip);

                    SohuPassportSidMapping sohuPassportSidMapping = null;
                    try {
                        String data = sendSpassportSingleHttpReq(url, map);
                        sohuPassportSidMapping = new ObjectMapper().readValue(data, SohuPassportSidMapping.class);
                    } catch (IOException e) {
                        System.err.println("error parse");
                    }

                    AccountBaseInfo accountBaseInfo = accountBaseInfoDAO.getAccountBaseInfoByPassportId(passportId);


                    printWriter.println(i + "\t" + compareResult(snamePassportMapping, sohuPassportSidMapping) + "\t" + compareNickAndAvator(accountBaseInfo, sohuPassportSidMapping));

                }
                printWriter.flush();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    //
                }

/*                for (int i=j; i<j+STEP && i<end && i<list.size(); i++) {
                    SnamePassportMapping snamePassportMapping = list.get(i);
                    sids += snamePassportMapping.getSid();
                }
                List<String> resultList = sendSpassportHttpReq(sids);

                for (int i=j; i<STEP+j && i<end && i < list.size(); i++) {
                    try {
                        SohuPassportMapping sohuPassportMapping = objectMapper.get().readValue(resultList.get(i-j), SohuPassportMapping.class);
                        SnamePassportMapping snamePassportMapping = list.get(i);
                        if (!compareResult(snamePassportMapping, sohuPassportMapping)) {
                            dataOutputStream.writeUTF(snamePassportMapping.getSid()+"\tfail\t"+Integer.valueOf(i + start)+"\n");
                            dataOutputStream.flush();
                        } else {
                            dataOutputStream.writeUTF(snamePassportMapping.getSid()+"\tsuccess\t"+Integer.valueOf(i+start)+"\n");
                            dataOutputStream.flush();
                            successNum++;
                        }
                    } catch (IOException e) {
                        System.out.println("error:" + flag);
                    }
                }*/
            }

            success.set(new Integer(successNum));
            System.out.println(success.get() + ":" + successNum);
        }

        private boolean compareResult(SnamePassportMapping loc, SohuPassportMapping rem) {
            return loc.getSid().equals(rem.getSid()) && loc.getSname().equals(rem.getSname())
                    && loc.getPassportId().equals(rem.getEmail());
        }

        private boolean compareResult(SnamePassportMapping loc, SohuPassportSidMapping rem) {
            if (rem == null) {
                return false;
            }
            boolean result = loc.getSid().equals(rem.getSid()) && loc.getSname().equals(rem.getSname());
            if (result == true) {
                if (StringUtil.isBlank(loc.getMobile()) && !StringUtil.isBlank(rem.getMobile())) {
                    return false;
                }
                if (!StringUtil.isBlank(loc.getMobile()) && !loc.getMobile().equals(rem.getMobile())) {
                    return false;
                }
            }
            return result;
        }


        private String compareNickAndAvator(AccountBaseInfo accountBaseInfo, SohuPassportSidMapping sohuPassportSidMapping) {
            if (accountBaseInfo == null) {
                if (sohuPassportSidMapping != null && (sohuPassportSidMapping.getLarge_avator() != null || sohuPassportSidMapping.getNick() != null)) {
                    return "error: sohu has nick and avator";
                }
                if (sohuPassportSidMapping == null) {
                    return "ok";
                }
            }
            if (sohuPassportSidMapping == null) {
                return "error: sohu not existed";
            }
            String nickSohu = sohuPassportSidMapping.getNick();
            String avatarSohu = sohuPassportSidMapping.getLarge_avator();
            String avatar = accountBaseInfo.getAvatar();
            String nick = accountBaseInfo.getUniqname();

            if (nick == null) {
                if (nickSohu != null) {
                    return "error: sohu has nick - " + nickSohu;
                }

            } else {
                if (!nick.equals(nickSohu)) {
                    return "error: nick - " + nick;
                }
            }
            if (avatar == null) {
                if (avatarSohu != null) {
                    return "error: sohu has avatar - " + avatarSohu;
                }
            } else {
                if (!avatar.equals(avatarSohu)) {
                    return "error: avatar - " + avatar;
                }
            }

            return "ok";
        }
    }

    class SohuPassportSidMapping {
        private String sid;
        private String sname;
        private String email;
        private String nick;
        private String large_avator;
        private String mid_avator;
        private String tiny_avator;
        private String update_at;
        private String active_at;
        private String create_at;
        private String create_ip;
        private String status;
        private String sname_active;
        private String mobile;


        String getStatus() {
            return status;
        }

        void setStatus(String status) {
            this.status = status;
        }

        String getSname_active() {
            return sname_active;
        }

        void setSname_active(String sname_active) {
            this.sname_active = sname_active;
        }

        String getMobile() {
            return mobile;
        }

        void setMobile(String mobile) {
            this.mobile = mobile;
        }

        String getSid() {
            return sid;
        }

        String getSname() {
            return sname;
        }

        String getEmail() {
            return email;
        }

        String getNick() {
            return nick;
        }

        String getLarge_avator() {
            return large_avator;
        }

        String getMid_avator() {
            return mid_avator;
        }

        String getTiny_avator() {
            return tiny_avator;
        }

        String getUpdate_at() {
            return update_at;
        }

        String getActive_at() {
            return active_at;
        }

        String getCreate_at() {
            return create_at;
        }

        String getCreate_ip() {
            return create_ip;
        }

        void setSid(String sid) {
            this.sid = sid;
        }

        void setSname(String sname) {
            this.sname = sname;
        }

        void setEmail(String email) {
            this.email = email;
        }

        void setNick(String nick) {
            this.nick = nick;
        }

        void setLarge_avator(String large_avator) {
            this.large_avator = large_avator;
        }

        void setMid_avator(String mid_avator) {
            this.mid_avator = mid_avator;
        }

        void setTiny_avator(String tiny_avator) {
            this.tiny_avator = tiny_avator;
        }

        void setUpdate_at(String update_at) {
            this.update_at = update_at;
        }

        void setActive_at(String active_at) {
            this.active_at = active_at;
        }

        void setCreate_at(String create_at) {
            this.create_at = create_at;
        }

        void setCreate_ip(String create_ip) {
            this.create_ip = create_ip;
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
