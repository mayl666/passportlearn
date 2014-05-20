package com.sogou.upd.passport.service.dataimport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-6
 * Time: 上午11:29
 */
public class FileReader {


    private static final Logger LOGGER = LoggerFactory.getLogger(FileReader.class);

    public static void main() throws Exception {
        Path filePath = Paths.get("D:\\logs\\increase\\increase_1.txt");
        String fileName = "D:\\logs\\increase\\increase_1.txt";

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line3 = null;
            while ((line3 = reader.readLine()) != null) {
                System.out.println(" try with resources Files.newBufferedReader :" + line3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("read file error.", e.getMessage());
        }

       /* try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line4 = null;
            while ((line4 = reader.readLine()) != null) {
                System.out.println(" try with resources FileReader :" + line4);
            }
        }*/

        try {
            InputStream in = Files.newInputStream(filePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line1 = null;
            while ((line1 = reader.readLine()) != null) {
                System.out.println(" Files.newInputStream:" + line1);
            }
        } catch (Exception e) {
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(filePath)))) {
            String line2 = null;
            while ((line2 = reader.readLine()) != null) {
                System.out.println(" try with resources  Files.newInputStream:" + line2);
            }
        }

        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
            int inByte;
            while ((inByte = in.read()) != -1) {
                System.out.printf("%02X ", inByte);
            }
            System.out.printf("%n%n");
        } catch (Exception e) {
        }
    }


    /**
     * <p>拆分文件</p>
     *
     * @param file  源文件
     * @param count 拆分的文件个数
     * @throws Exception
     */

    public static void split(String file, int count) throws Exception {

        RandomAccessFile raf = new RandomAccessFile(new File(file), "r");
        long length = raf.length();

        long theadMaxSize = length / count; //每份的大小 1024 * 1000L;
        raf.close();

        long offset = 0L;
        for (int i = 0; i < count - 1; i++) //这里不去处理最后一份
        {
            long fbegin = offset;
            long fend = (i + 1) * theadMaxSize;
            offset = write(file, i, fbegin, fend);
        }

        if (length - offset > 0) //将剩余的都写入最后一份
            write(file, count - 1, offset, length);
    }


    /**
     * <p>指定每份文件的范围写入不同文件</p>
     *
     * @param file  源文件
     * @param index 文件顺序标识
     * @param begin 开始指针位置
     * @param end   结束指针位置
     * @return
     * @throws Exception
     */

    private static long write(String file, int index, long begin, long end) throws Exception {
        RandomAccessFile in = new RandomAccessFile(new File(file), "r");
        RandomAccessFile out = new RandomAccessFile(new File(file + "_" + index + ".tmp"), "rw");
        byte[] b = new byte[1024];
        int n = 0;
        in.seek(begin);//从指定位置读取

        while (in.getFilePointer() <= end && (n = in.read(b)) != -1) {
            out.write(b, 0, n);
        }
        long endPointer = in.getFilePointer();
        in.close();
        out.close();
        return endPointer;
    }








    /**
     * <p>合并文件</p>
     *
     * @param file      指定合并后的文件
     * @param tempFiles 分割前的文件名
     * @param tempCount 文件个数
     * @throws Exception
     */
    public static void merge(String file, String tempFiles, int tempCount) throws Exception {
        RandomAccessFile ok = new RandomAccessFile(new File(file), "rw");

        for (int i = 0; i < tempCount; i++) {
            RandomAccessFile read = new RandomAccessFile(new File(tempFiles + "_" + i + ".tmp"), "r");
            byte[] b = new byte[1024];
            int n = 0;
            while ((n = read.read(b)) != -1) {
                ok.write(b, 0, n);
            }
            read.close();
        }
        ok.close();
    }


}

