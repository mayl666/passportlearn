package com.sogou.upd.passport.service.dataimport.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-12-18
 * Time: 下午8:45
 */
public class FileUtilTest {


    @Test
    public void testFile() {
        String fileName = "d:/jmap/ip_zhanbi.txt";

        List<String> ipList = Lists.newArrayList();
        List<String> blList = Lists.newArrayList();

        BufferedReader reader = null;
        try {
            File file = new File(fileName);
            reader = new BufferedReader(new java.io.FileReader(file));
            String tempString = null;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                ipList.add(tempString.split(" ")[0]);
                blList.add(tempString.split(" ")[1].replace("%", ""));
            }

//            String ips = Joiner.on(",").join(ipList);

//            String bls = Joiner.on(",").join(blList);

//            System.out.println("ips:" + ips);
//            System.out.println("bls:" + bls);


            FileUtil.storeFile("d:/jmap/ips.txt", ipList);
            FileUtil.storeFile("d:/jmap/bls.txt", blList);

            reader.close();
        } catch (Exception e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }

    }
}
