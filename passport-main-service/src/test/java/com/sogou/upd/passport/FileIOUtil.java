package com.sogou.upd.passport;

import com.google.common.collect.Lists;
import org.junit.Ignore;

import java.io.*;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: shipengzhi
 * Date: 13-12-4
 * Time: 上午12:47
 * To change this template use File | Settings | File Templates.
 */
@Ignore
public class FileIOUtil {

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static List<String> readFileByLines(String fileName) {
        List<String> LinesList = Lists.newArrayList();
        BufferedReader reader = null;
        try {
            File file = new File(fileName);
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                LinesList.add(tempString);
            }
            reader.close();
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return LinesList;
    }

    public static BufferedWriter newWriter(String fileName) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream("c:/1.txt");
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        return new BufferedWriter(osw);
    }

}
