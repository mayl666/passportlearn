package com.sogou.upd.passport.service.dataimport.util;

import org.apache.commons.collections.CollectionUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 14-5-7
 * Time: 上午11:32
 */
public class FileUtil {


    /**
     * 存储操作日志到本地
     *
     * @param fileName
     * @param result
     * @throws IOException
     * @throws URISyntaxException
     */
    public static void storeFile(String fileName, List<String> result) throws IOException, URISyntaxException {
        Path filePath = Paths.get("D:\\logs\\" + fileName);
        Files.deleteIfExists(filePath);
        BufferedWriter writer = Files.newBufferedWriter(filePath, Charset.defaultCharset());
        if (CollectionUtils.isNotEmpty(result)) {
            for (String item : result) {
                writer.write(item);
                writer.newLine();
            }
            writer.flush();
        }
    }

}
