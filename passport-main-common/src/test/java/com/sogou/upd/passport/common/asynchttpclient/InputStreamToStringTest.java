package com.sogou.upd.passport.common.asynchttpclient;

import com.google.common.io.CharStreams;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: chengang
 * Date: 15-1-9
 * Time: 上午11:47
 */
public class InputStreamToStringTest {


    @Test
    public void givenUsingCommonsIoWithCopy_whenConvertingAnInputStreamToAString_thenCorrect()
            throws IOException {
        String originalString = "";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        StringWriter writer = new StringWriter();
        String encoding = StandardCharsets.UTF_8.name();
        IOUtils.copy(inputStream, writer, encoding);
    }


    @Test
    public void givenUsingJava5_whenConvertingAnInputStreamToAString_thenCorrect()
            throws IOException {
        String originalString = "";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        StringBuilder textBuilder = new StringBuilder();
        try (Reader reader = new BufferedReader(new InputStreamReader
                (inputStream, Charset.forName(StandardCharsets.UTF_8.name())))) {
            int c;
            while ((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
        }
    }


    @Test
    public void givenUsingJava7_whenConvertingAnInputStreamToAString_thenCorrect()
            throws IOException {
        String originalString = "";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text;
        try (Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            text = scanner.useDelimiter("\\A").next();
        }
    }


    @Test
    public void givenUsingGuavaAndJava7_whenConvertingAnInputStreamToAString_thenCorrect()
            throws IOException {
        String originalString = "";
        InputStream inputStream = new ByteArrayInputStream(originalString.getBytes());

        String text = null;
        try (final Reader reader = new InputStreamReader(inputStream)) {
            text = CharStreams.toString(reader);
        }
    }


}
