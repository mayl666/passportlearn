package com.sogou.upd.passport.service.dataimport.fulldatacheck;

import com.google.common.collect.Lists;
import com.sogou.upd.passport.service.dataimport.util.FileUtil;

import java.io.*;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-13
 * Time: 下午8:01
 */
public class ReadCsvFile {


    public static void main(String[] args) {
        try {
            File csv = new File("D:\\项目\\非第三方账号迁移\\2014.1.1-2014.5.7_sgwan_other_userid.csv"); // CSV文件

            BufferedReader br = new BufferedReader(new FileReader(csv));

            List<String> list = Lists.newArrayList();

            // 读取直到最后一行
            String line = "";
            while ((line = br.readLine()) != null) {
                /*// 把一行数据分割成多个字段
                StringTokenizer st = new StringTokenizer(line, ",");

                while (st.hasMoreTokens()) {
                    // 每一行的多个字段用TAB隔开表示
                    System.out.print(st.nextToken() + "/t");
                }*/
                list.add(line);
//                System.out.println(line);
            }
            br.close();

            FileUtil.storeFile("2014.1.1-2014.5.7_sgwan_other_userid_new.txt", list);

        } catch (FileNotFoundException e) {
            // 捕获File对象生成时的异常
            e.printStackTrace();
        } catch (IOException e) {
            // 捕获BufferedReader对象关闭时的异常
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
