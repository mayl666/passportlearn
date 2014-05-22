package com.sogou.upd.passport.service.dataimport.util;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-7
 * Time: 上午11:32
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    private static final int HOLD_NUMBER = 500000;

    private List<String> subFile = Lists.newLinkedList();


    /**
     * 存储操作日志到本地
     *
     * @param path
     * @param result
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void storeFile(String path, List<String> result) throws IOException, URISyntaxException {
        try {
            Path filePath = Paths.get(path);
            Files.deleteIfExists(filePath);
            BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset());
            if (CollectionUtils.isNotEmpty(result)) {
                for (String item : result) {
                    writer.write(item);
                    writer.newLine();
                }
                writer.flush();
            }
        } catch (Exception e) {
            LOGGER.error("FileUtil storeFile  error. ", e);
        }
    }

    /**
     * 存储操作日志到本地
     *
     * @param path
     * @param result
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void storeFileToLocal(String path, List<Map<String, String>> result) throws IOException, URISyntaxException {
        try {
            Path filePath = Paths.get(path);
            Files.deleteIfExists(filePath);
            BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset());
            if (CollectionUtils.isNotEmpty(result)) {
                for (Map<String, String> map : result) {
                    if (!map.isEmpty()) {
                        for (Map.Entry entry : map.entrySet()) {
                            writer.write(entry.getKey() + ":" + entry.getValue());
                            writer.newLine();
                        }
                    }
                }
                writer.flush();
            }
        } catch (Exception e) {
            LOGGER.error("FileUtil storeFile  error. ", e);
        }
    }


    public static void split(String filePath, int count) throws Exception {

        try {
            File raf = new File(filePath);
            long length = raf.length();

            long theadMaxSize = length / count; //每份的大小 1024 * 1000L;

            long offset = 0L;
            for (int i = 0; i < count - 1; i++) //这里不去处理最后一份
            {
                long fbegin = offset;
                long fend = (i + 1) * theadMaxSize;
                offset = write(filePath, i, fbegin, fend);
            }

            if (length - offset > 0) //将剩余的都写入最后一份
                write(filePath, count - 1, offset, length);
        } catch (Exception e) {
            e.printStackTrace();
        }

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


    public void splitBigFile(String pathName) {

        Path filePath = Paths.get(pathName);

        File file = new File(pathName);


        try (BufferedReader reader = Files.newBufferedReader(filePath, Charset.defaultCharset())) {
            String line;
            int fileSize = subFile.size();
            while ((line = reader.readLine()) != null) {
                if (Strings.isNullOrEmpty(line)) {
                    break;
                }
                subFile.add(line);
                if (fileSize > HOLD_NUMBER) {
                    break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
